package jskills.factorgraphs;

abstract class Schedule[T](name: String) {
  def visit(depth: Int, maxDepth: Int): Double

  def visit(): Double = visit(-1, 0)

  override def toString() = name

}
