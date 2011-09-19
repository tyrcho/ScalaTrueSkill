package jskills.factorgraphs

class Message[T](var value: T, nameFormat: String, args: Object*) {
  override def toString() = if (nameFormat == null) super.toString() else format(nameFormat, args)
}