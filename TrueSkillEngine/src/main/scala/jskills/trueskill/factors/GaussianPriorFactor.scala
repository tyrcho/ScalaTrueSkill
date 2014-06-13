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
  extends GaussianFactor(String.format("Prior value going to %s", variable)) {
  val newMessage = GaussianDistribution(mean, Math.sqrt(variance))

  createVariableToMessageBinding(variable,
    new Message[GaussianDistribution](
      GaussianDistribution.fromPrecisionMean(0, 0), "message from %s to %s",
      this, variable))

  override protected def updateMessage(message: Message[GaussianDistribution], variable: Variable[GaussianDistribution]): Double = {
    val oldMarginal = GaussianDistribution(variable.value)
    val oldMessage = message
    val newMarginal = GaussianDistribution.fromPrecisionMean(
      oldMarginal.precisionMean + newMessage.precisionMean - oldMessage.value.precisionMean,
      oldMarginal.precision + newMessage.precision - oldMessage.value.precision)
    variable.value = newMarginal
    message.value = newMessage
    sub(oldMarginal, newMarginal)
  }

  override def getLogNormalization() = 0
}
