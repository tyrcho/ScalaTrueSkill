package jskills.factorgraphs

class DefaultVariable[TValue] extends Variable[TValue](null.asInstanceOf[TValue], "Default") {
  override def setValue(value: TValue) {
    throw new UnsupportedOperationException()
  }
}