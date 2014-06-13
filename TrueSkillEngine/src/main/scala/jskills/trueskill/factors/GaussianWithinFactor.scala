package jskills.trueskill.factors

import jskills.numerics.GaussianDistribution._
import jskills.trueskill.TruncatedGaussianCorrectionFunctions._
import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution

/**
 * Factor representing a team difference that has not exceeded the draw margin.
 * [remarks]See the accompanying math paper for more details.[/remarks]
 */
class GaussianWithinFactor(epsilon: Double, variable: Variable[GaussianDistribution])
  extends GaussianFactor(f"$variable [= $epsilon%4.3f") {
  createVariableToMessageBinding(variable)

  override def getLogNormalization(): Double = {
    val marginal = variables(0).value
    val message = messages(0).value
    val messageFromVariable = divide(marginal, message)
    val mean = messageFromVariable.mean
    val std = messageFromVariable.standardDeviation
    val z = cumulativeTo((epsilon - mean) / std) - cumulativeTo((-epsilon - mean) / std)

    -logProductNormalization(messageFromVariable, message) + Math.log(z)
  }

  override protected def updateMessage(message: Message[GaussianDistribution], variable: Variable[GaussianDistribution]): Double = {
    val oldMarginal = GaussianDistribution(variable.value)
    val oldMessage = GaussianDistribution(message.value)
    val messageFromVariable = oldMarginal / oldMessage

    val c = messageFromVariable.precision
    var d = messageFromVariable.precisionMean

    val sqrtC = Math.sqrt(c)
    val dOnSqrtC = d / sqrtC

    val epsilonTimesSqrtC = epsilon * sqrtC
    d = messageFromVariable.precisionMean

    val denominator = 1.0 - WWithinMargin(dOnSqrtC, epsilonTimesSqrtC)
    val newPrecision = c / denominator
    val newPrecisionMean = (d + sqrtC * VWithinMargin(dOnSqrtC, epsilonTimesSqrtC)) / denominator

    val newMarginal = fromPrecisionMean(newPrecisionMean, newPrecision)
    val newMessage = oldMessage * newMarginal / oldMarginal

    // Update the message and marginal
    message.value = newMessage
    variable.value = newMarginal

    // Return the difference in the new marginal
    sub(newMarginal, oldMarginal)
  }
}
