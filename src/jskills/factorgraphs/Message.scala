package jskills.factorgraphs


class Message[T]( var value: T, nameFormat: String, args: Object*) {
  def this() { this(null.asInstanceOf[T], null, null) }

  override def toString() = if (nameFormat == null) super.toString() else format(nameFormat, args)
}