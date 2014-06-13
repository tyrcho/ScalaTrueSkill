package jskills.factorgraphs

class Variable[T](prior: T, name: String) {
  resetToPrior()

  var value: T = prior

  def resetToPrior() { value = prior }

  override def toString() = name
}

object Variable {
  def apply[T](prior: T, name: String, args: Any*) = {
    new Variable[T](prior, String.format(name, args))
  }
}