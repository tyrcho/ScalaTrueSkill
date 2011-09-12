package jskills.trueskill.factors

import jskills.numerics.GaussianDistribution

import jskills.factorgraphs.Factor
import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution

abstract class GaussianFactor(name: String) extends Factor[GaussianDistribution](name) {

  /** Sends the factor-graph message with and returns the log-normalization constant **/
  override protected def SendMessage(message: Message[GaussianDistribution], variable: Variable[GaussianDistribution]): Double = {
    val marginal = variable.getValue()
    val messageValue = message.getValue()
    val logZ = GaussianDistribution.logProductNormalization(marginal, messageValue)
    variable.setValue(marginal.mult(messageValue))
    return logZ
  }

  override def CreateVariableToMessageBinding(variable: Variable[GaussianDistribution]): Message[GaussianDistribution] =
    CreateVariableToMessageBinding(variable, new Message[GaussianDistribution](GaussianDistribution.fromPrecisionMean(0, 0), "message from %s to %s", this, variable))

}