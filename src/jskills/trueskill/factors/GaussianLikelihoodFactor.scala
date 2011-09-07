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
  CreateVariableToMessageBinding(variable1)
  CreateVariableToMessageBinding(variable2)

  override def getLogNormalization(): Double = logRatioNormalization(variables.get(0).getValue(), messages.get(0).getValue())

  private def UpdateHelper(
    message1: Message[GaussianDistribution],
    message2: Message[GaussianDistribution],
    variable1: Variable[GaussianDistribution],
    variable2: Variable[GaussianDistribution]): Double = {
    val message1Value = new GaussianDistribution(
      message1.getValue())
    val message2Value = new GaussianDistribution(
      message2.getValue())

    val marginal1 = new GaussianDistribution(
      variable1.getValue())
    val marginal2 = new GaussianDistribution(
      variable2.getValue())

    val a = precision / (precision + marginal2.getPrecision() - message2Value
      .getPrecision())

    val newMessage = GaussianDistribution.fromPrecisionMean(a * (marginal2.getPrecisionMean() - message2Value.getPrecisionMean()),
      a * (marginal2.getPrecision() - message2Value.getPrecision()))

    val oldMarginalWithoutMessage = divide(marginal1, message1Value)

    val newMarginal = prod(oldMarginalWithoutMessage, newMessage)

    // Update the message and marginal
    message1.setValue(newMessage)
    variable1.setValue(newMarginal)

    // Return the difference in the new marginal
    return sub(newMarginal, marginal1)
  }

  override def updateMessage(messageIndex: Int): Double = {
    messageIndex match {
      case 0 => UpdateHelper(getMessages().get(0), getMessages().get(1), getVariables().get(0), getVariables().get(1))
      case 1 => UpdateHelper(getMessages().get(1), getMessages().get(0), getVariables().get(1), getVariables().get(0))
      case _ => throw new IllegalArgumentException()
    }
  }
}
