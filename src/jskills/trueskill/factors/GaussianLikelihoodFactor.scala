package jskills.trueskill.factors

import jskills.numerics.GaussianDistribution._
import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution

/**
 * Connects two variables and adds uncertainty. [remarks]See the accompanying
 * math paper for more details.[/remarks]
 */
class GaussianLikelihoodFactor(betaSquared: Double, variable1: Variable[GaussianDistribution], variable2: Variable[GaussianDistribution])
  extends GaussianFactor(format("Likelihood of %s going to %s", variable2, variable1)) {

  val precision = 1.0 / betaSquared
  createVariableToMessageBinding(variable1)
  createVariableToMessageBinding(variable2)

  override def getLogNormalization(): Double = logRatioNormalization(variables(0).value, messages(0).value)

  private def updateHelper(
    message1: Message[GaussianDistribution],
    message2: Message[GaussianDistribution],
    variable1: Variable[GaussianDistribution],
    variable2: Variable[GaussianDistribution]): Double = {
    val message1Value = GaussianDistribution(message1.value)
    val message2Value = GaussianDistribution(message2.value)

    val marginal1 = GaussianDistribution(variable1.value)
    val marginal2 = GaussianDistribution(variable2.value)

    val a = precision / (precision + marginal2.precision - message2Value.precision)

    val newMessage = GaussianDistribution.fromPrecisionMean(
      a * (marginal2.precisionMean - message2Value.precisionMean),
      a * (marginal2.precision - message2Value.precision))

    val oldMarginalWithoutMessage = divide(marginal1, message1Value)

    val newMarginal = prod(oldMarginalWithoutMessage, newMessage)

    // Update the message and marginal
    message1.value = newMessage
    variable1.value = newMarginal

    // Return the difference in the new marginal
     sub(newMarginal, marginal1)
  }

  override def updateMessage(messageIndex: Int): Double = {
    messageIndex match {
      case 0 => updateHelper(messages(0), messages(1), variables(0), variables(1))
      case 1 => updateHelper(messages(1), messages(0), variables(1), variables(0))
      case _ => throw new IllegalArgumentException()
    }
  }
}
