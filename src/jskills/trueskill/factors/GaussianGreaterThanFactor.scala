package jskills.trueskill.factors

import jskills.numerics.GaussianDistribution._
import jskills.trueskill.TruncatedGaussianCorrectionFunctions._
import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
/**
 * Factor representing a team difference that has exceeded the draw margin.
 * [remarks]See the accompanying math paper for more details.[/remarks]
 */
class GaussianGreaterThanFactor(epsilon: Double, variable: Variable[GaussianDistribution]) extends GaussianFactor(format("%s ] %4.3f", variable, epsilon)) {
  CreateVariableToMessageBinding(variable)

  override def getLogNormalization(): Double = {
    val marginal = variables.get(0).value
    val message = messages.get(0).value
    val messageFromVariable = divide(marginal, message)
    return -logProductNormalization(messageFromVariable, message)
    +Math.log(cumulativeTo((messageFromVariable.mean - epsilon) / messageFromVariable.standardDeviation))
  }

  override protected def updateMessage(message: Message[GaussianDistribution], variable: Variable[GaussianDistribution]): Double = {
    val oldMarginal = new GaussianDistribution(variable.value)
    val oldMessage = new GaussianDistribution(message.value)
    val messageFromVar = divide(oldMarginal, oldMessage)

    val c = messageFromVar.precision
    var d = messageFromVar.precisionMean

    val sqrtC = Math.sqrt(c)

    val dOnSqrtC = d / sqrtC

    val epsilsonTimesSqrtC = epsilon * sqrtC
    d = messageFromVar.precisionMean

    val denom = 1.0 - WExceedsMargin(dOnSqrtC, epsilsonTimesSqrtC)

    val newPrecision = c / denom
    val newPrecisionMean = (d + sqrtC * VExceedsMargin(dOnSqrtC, epsilsonTimesSqrtC)) / denom

    val newMarginal = fromPrecisionMean(newPrecisionMean,
      newPrecision)

    val newMessage = divide(prod(oldMessage, newMarginal),
      oldMarginal)

    // Update the message and marginal
    message.value = newMessage
    variable.value = newMarginal

    // Return the difference in the new marginal
    return sub(newMarginal, oldMarginal)
  }
}
