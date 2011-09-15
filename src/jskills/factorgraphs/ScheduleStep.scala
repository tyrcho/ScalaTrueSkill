package jskills.factorgraphs;

class ScheduleStep[T](name: String, val factor: Factor[T], index: Int) extends Schedule[T](name) {
  override def visit(depth: Int, maxDepth: Int): Double =
    factor.updateMessage(index);
}