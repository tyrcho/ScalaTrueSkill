package jskills.trueskill.factors

import jskills.numerics.GaussianDistribution._

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import jskills.Guard
import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import collection.JavaConversions._

object GaussianWeightedSumFactor {
  def createName(sumVariable: Variable[GaussianDistribution],
    variablesToSum: List[_ <: Variable[GaussianDistribution]],
    weights: Array[Double]): String =
    {
      var sb = new StringBuffer()
      sb.append(sumVariable.toString())
      sb.append(" = ")
      for (i <- 0 until variablesToSum.size()) {
        val isFirst = (i == 0)

        if (isFirst && (weights(i) < 0)) {
          sb.append("-")
        }

        sb.append(format("%.2f", Math.abs(weights(i))))
        sb.append("*[")
        sb.append(variablesToSum.get(i))
        sb.append("]")

        val isLast = (i == variablesToSum.size() - 1)

        if (!isLast) {
          if (weights(i + 1) >= 0) {
            sb.append(" + ")
          } else {
            sb.append(" - ")
          }
        }
      }

      return sb.toString()
    }
}

/**
 * Factor that sums together multiple Gaussians.
 * [remarks]See the accompanying math paper for more details.[/remarks]
 */
class GaussianWeightedSumFactor(
  sumVariable: Variable[GaussianDistribution],
  variablesToSum: List[_ <: Variable[GaussianDistribution]],
  var variableWeights: Array[Double] = null)
  extends GaussianFactor(GaussianWeightedSumFactor.createName(sumVariable, variablesToSum, variableWeights)) // By default, set the weight to 1.0, which is what null indicates
  {
  val variableIndexOrdersForWeights = new ArrayList[Array[Int]]()

  if (variableWeights == null) {
    variableWeights = new Array[Double](variablesToSum.size())
    Arrays.fill(variableWeights, 1.)
  }
  // This following is used for convenience, for example, the first entry is [0, 1, 2] 
  // corresponding to v(0) = a1*v[1] + a2*v[2]
  val weights = new Array[Array[Double]](variableWeights.length + 1)
  val weightsSquared = new Array[Array[Double]](weights.length)

  // The first weights are a straightforward copy
  // v_0 = a_1*v_1 + a_2*v_2 + ... + a_n * v_n
  weights(0) = new Array[Double](variableWeights.length)
  System.arraycopy(variableWeights, 0, weights(0), 0, variableWeights.length)
  weightsSquared(0) = new Array[Double](variableWeights.length)
  for (i <- 0 until weights(0).length)
    weightsSquared(0)(i) = weights(0)(i) * weights(0)(i)

  // 0..n-1
  val temp = new Array[Int](1 + variablesToSum.size())
  for (i <- 0 until temp.length) temp(i) = i
  variableIndexOrdersForWeights.add(temp)

  // The rest move the variables around and divide out the constant. 
  // For example:
  // v_1 = (-a_2 / a_1) * v_2 + (-a3/a1) * v_3 + ... + (1.0 / a_1) * v_0
  // By convention, we'll put the v_0 term at the end
  for (weightsIndex <- 1 until weights.length) {
    val currentWeights = new Array[Double](variableWeights.length)
    weights(weightsIndex) = currentWeights

    val variableIndices = new Array[Int](variableWeights.length + 1)
    variableIndices(0) = weightsIndex

    val currentWeightsSquared = new Array[Double](variableWeights.length)
    weightsSquared(weightsIndex) = currentWeightsSquared

    // keep a single variable to keep track of where we are in the array.
    // This is helpful since we skip over one of the spots
    var currentDestinationWeightIndex = 0

    for (currentWeightSourceIndex <- 0 until variableWeights.length) {
      if (currentWeightSourceIndex != (weightsIndex - 1)) {

        var currentWeight = (-variableWeights(currentWeightSourceIndex) / variableWeights(weightsIndex - 1))

        if (variableWeights(weightsIndex - 1) == 0) {
          // HACK: Getting around division by zero
          currentWeight = 0
        }

        currentWeights(currentDestinationWeightIndex) = currentWeight
        currentWeightsSquared(currentDestinationWeightIndex) = currentWeight * currentWeight

        variableIndices(currentDestinationWeightIndex + 1) = currentWeightSourceIndex + 1
        currentDestinationWeightIndex += 1
      }
    }

    // And the final one
    var finalWeight = 1.0 / variableWeights(weightsIndex - 1)

    if (variableWeights(weightsIndex - 1) == 0) {
      // HACK: Getting around division by zero
      finalWeight = 0
    }
    currentWeights(currentDestinationWeightIndex) = finalWeight
    currentWeightsSquared(currentDestinationWeightIndex) = finalWeight * finalWeight
    variableIndices(variableIndices.length - 1) = 0
    variableIndexOrdersForWeights.add(variableIndices)
  }

  CreateVariableToMessageBinding(sumVariable)

  variablesToSum foreach CreateVariableToMessageBinding

  override def getLogNormalization(): Double = {
    val vars = variables
    var result = 0.0

    // We start at 1 since offset 0 has the sum
    for (i <- 1 until vars.size()) {
      result += GaussianDistribution.logRatioNormalization(vars.get(i).value, messages.get(i).value)
    }

    return result
  }

  private def UpdateHelper(weights: Array[Double],
    weightsSquared: Array[Double],
    messages: List[Message[GaussianDistribution]],
    variables: List[Variable[GaussianDistribution]]): Double =
    {
      // Potentially look at http://mathworld.wolfram.com/NormalSumDistribution.html for clues as 
      // to what it's doing

      val message0 = new GaussianDistribution(messages.get(0).value)
      val marginal0 = new GaussianDistribution(variables.get(0).value)

      // The math works out so that 1/newPrecision = sum of a_i^2 /marginalsWithoutMessages(i)
      var inverseOfNewPrecisionSum = 0.0
      var anotherInverseOfNewPrecisionSum = 0.0
      var weightedMeanSum = 0.0
      var anotherWeightedMeanSum = 0.0

      for (i <- 0 until weightsSquared.length) {
        // These flow directly from the paper

        inverseOfNewPrecisionSum += weightsSquared(i) /
          (variables.get(i + 1).value.precision - messages.get(i + 1).value.precision)

        val diff = divide(variables.get(i + 1).value, messages.get(i + 1).value)
        anotherInverseOfNewPrecisionSum += weightsSquared(i) / diff.precision

        weightedMeanSum += weights(i) *
          (variables.get(i + 1).value.precisionMean - messages.get(i + 1).value.precisionMean) /
          (variables.get(i + 1).value.precision - messages.get(i + 1).value.precision)

        anotherWeightedMeanSum += weights(i) * diff.precisionMean / diff.precision
      }

      val newPrecision = 1.0 / inverseOfNewPrecisionSum
      val anotherNewPrecision = 1.0 / anotherInverseOfNewPrecisionSum

      val newPrecisionMean = newPrecision * weightedMeanSum
      val anotherNewPrecisionMean = anotherNewPrecision * anotherWeightedMeanSum

      val oldMarginalWithoutMessage = divide(marginal0, message0)

      val newMessage = GaussianDistribution.fromPrecisionMean(newPrecisionMean, newPrecision)
      val anotherNewMessage = GaussianDistribution.fromPrecisionMean(anotherNewPrecisionMean, anotherNewPrecision)
      if (!newMessage.equals(anotherNewMessage))
        throw new RuntimeException("newMessage and anotherNewMessage aren't the same")

      val newMarginal = prod(oldMarginalWithoutMessage, newMessage)

      // Update the message and marginal

      messages.get(0).value = newMessage
      variables.get(0).value = newMarginal

      // Return the difference in the new marginal
      return sub(newMarginal, marginal0)
    }

  override def updateMessage(messageIndex: Int): Double =
    {
      val allMessages = messages
      val allVariables = variables

      Guard.argumentIsValidIndex(messageIndex, allMessages.size(), "messageIndex")

      val updatedMessages = new ArrayList[Message[GaussianDistribution]]()
      val updatedVariables = new ArrayList[Variable[GaussianDistribution]]()

      val indicesToUse = variableIndexOrdersForWeights.get(messageIndex)

      // The tricky part here is that we have to put the messages and variables in the same
      // order as the weights. Thankfully, the weights and messages share the same index numbers,
      // so we just need to make sure they're consistent
      for (i <- 0 until allMessages.size()) {
        updatedMessages.add(allMessages.get(indicesToUse(i)))
        updatedVariables.add(allVariables.get(indicesToUse(i)))
      }

      return UpdateHelper(weights(messageIndex), weightsSquared(messageIndex), updatedMessages, updatedVariables)
    }

}
