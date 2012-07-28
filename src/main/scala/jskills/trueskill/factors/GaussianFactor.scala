package jskills.trueskill.factors

import jskills.numerics.GaussianDistribution

import jskills.factorgraphs.Factor
import jskills.factorgraphs.Message
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution

abstract class GaussianFactor(name: String) extends Factor[GaussianDistribution](name) {

  /** Sends the factor-graph message with and s the log-normalization constant **/
  override protected def sendMessage(message: Message[GaussianDistribution], variable: Variable[GaussianDistribution]): Double = {
    val marginal = variable.value
    val messageValue = message.value
    val logZ = GaussianDistribution.logProductNormalization(marginal, messageValue)
    variable.value = marginal.mult(messageValue)
     logZ
  }

  override def createVariableToMessageBinding(variable: Variable[GaussianDistribution]): Message[GaussianDistribution] =
    createVariableToMessageBinding(variable, new Message[GaussianDistribution](GaussianDistribution.fromPrecisionMean(0, 0), "message from %s to %s", this, variable))

}