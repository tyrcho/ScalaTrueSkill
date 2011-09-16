package jskills.factorgraphs

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.List
import java.util.Map
import collection.JavaConversions._
import jskills.Guard._
import scala.reflect.BeanProperty

abstract class Factor[T](@BeanProperty name: String) {
  @BeanProperty protected val messages = new ArrayList[Message[T]]()
  @BeanProperty val messageToVariableBinding = new HashMap[Message[T], Variable[T]]()
  @BeanProperty val variables: List[Variable[T]] = new ArrayList[Variable[T]]()

  /** Returns the log-normalization constant of that factor **/
  def getLogNormalization(): Double

  /** Returns the number of messages that the factor has **/
  def getNumberOfMessages(): Int = messages.size()

  /** Update the message and marginal of the i-th variable that the factor is connected to **/
  def updateMessage(messageIndex: Int): Double = {
    argumentIsValidIndex(messageIndex, messages.size(), "messageIndex")
    return updateMessage(messages.get(messageIndex), messageToVariableBinding.get(messages.get(messageIndex)))
  }

  protected def updateMessage(message: Message[T], variable: Variable[T]): Double =
    throw new UnsupportedOperationException()

  /** Resets the marginal of the variables a factor is connected to **/
  def ResetMarginals() { messageToVariableBinding.values foreach (_.resetToPrior) }

  /**
   * Sends the ith message to the marginal and returns the log-normalization
   * constant
   */
  def SendMessage(messageIndex: Int): Double = {
    argumentIsValidIndex(messageIndex, messages.size(), "messageIndex")
    val message = messages.get(messageIndex)
    val variable = messageToVariableBinding.get(message)
    return SendMessage(message, variable)
  }

  protected def SendMessage(message: Message[T], variable: Variable[T]): Double

  def CreateVariableToMessageBinding(variable: Variable[T]): Message[T]

  protected def CreateVariableToMessageBinding(variable: Variable[T], message: Message[T]): Message[T] = {
    messages.add(message)
    messageToVariableBinding.put(message, variable)
    variables.add(variable)
    return message
  }

  override def toString() = if (name != null) name else super.toString()
}