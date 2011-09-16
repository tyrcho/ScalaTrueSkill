package jskills.factorgraphs
import scala.reflect.BeanProperty

class Message[T](@BeanProperty var value: T, nameFormat: String, args: Object*) {
  def this() { this(null.asInstanceOf[T], null, null) }

  override def toString() = if (nameFormat == null) super.toString() else format(nameFormat, args)
}