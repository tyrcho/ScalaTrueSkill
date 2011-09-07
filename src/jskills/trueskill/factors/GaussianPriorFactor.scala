package jskills.trueskill.factors

import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.numerics.GaussianDistribution._

/**
 * Supplies the factor graph with prior information.
 * [remarks]See the accompanying math paper for more details.[/remarks]
 */
class GaussianPriorFactor(mean: Double, variance: Double, variable: Variable[GaussianDistribution])
  extends GaussianFactor(format("Prior value going to %s", variable)) {
  val newMessage = new GaussianDistribution(mean, Math.sqrt(variance))
  CreateVariableToMessageBinding(variable,
    new Message[GaussianDistribution](
      GaussianDistribution.fromPrecisionMean(0, 0), "message from %s to %s",
      this, variable))

  override protected def updateMessage(message: Message[GaussianDistribution], variable: Variable[GaussianDistribution]): Double = {
    val oldMarginal = new GaussianDistribution(variable.getValue())
    val oldMessage = message
    val newMarginal =
      GaussianDistribution.fromPrecisionMean(
        oldMarginal.getPrecisionMean() + newMessage.getPrecisionMean() - oldMessage.getValue().getPrecisionMean(),
        oldMarginal.getPrecision() + newMessage.getPrecision() - oldMessage.getValue().getPrecision())
    variable.setValue(newMarginal)
    message.setValue(newMessage)
    return sub(oldMarginal, newMarginal)
  }

  override def getLogNormalization() = 0
}
