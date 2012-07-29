package jskills.factorgraphs

import jskills.Guard._
import collection.mutable.Map
import scala.collection.mutable.Seq
import scala.collection.mutable.ListBuffer

abstract class Factor[T](name: String, val messages: ListBuffer[Message[T]] = ListBuffer.empty[Message[T]]) {
  val messageToVariableBinding = Map.empty[Message[T], Variable[T]]
  val variables = ListBuffer.empty[Variable[T]]

  /** Returns the log-normalization constant of that factor **/
  def getLogNormalization(): Double

  /** Returns the number of messages that the factor has **/
  def getNumberOfMessages(): Int = messages.size

  /** Update the message and marginal of the i-th variable that the factor is connected to **/
  def updateMessage(messageIndex: Int): Double = {
    argumentIsValidIndex(messageIndex, messages.size, "messageIndex")
     updateMessage(messages(messageIndex), messageToVariableBinding(messages(messageIndex)))
  }

  protected def updateMessage(message: Message[T], variable: Variable[T]): Double =
    throw new UnsupportedOperationException()

  /** Resets the marginal of the variables a factor is connected to **/
  def resetMarginals() { messageToVariableBinding.values foreach (_.resetToPrior) }

  /**
   * Sends the ith message to the marginal and s the log-normalization
   * constant
   */
  def sendMessage(messageIndex: Int): Double = {
    argumentIsValidIndex(messageIndex, messages.size, "messageIndex")
    val message = messages(messageIndex)
    val variable = messageToVariableBinding(message)
     sendMessage(message, variable)
  }

  protected def sendMessage(message: Message[T], variable: Variable[T]): Double

  def createVariableToMessageBinding(variable: Variable[T]): Message[T]

  protected def createVariableToMessageBinding(variable: Variable[T], message: Message[T]): Message[T] = {
    messages += message
    messageToVariableBinding.put(message, variable)
    variables += variable
     message
  }

  override def toString() = if (name != null) name else super.toString()
}