package jskills.factorgraphs;

class ScheduleLoop[T](name: String, scheduleToLoop: Schedule[T], maxDelta: Double) extends Schedule[T](name) {

  val MAX_ITERATIONS = 100;

  override def visit(depth: Int, maxDepth: Int): Double = {
    var delta = scheduleToLoop.visit(depth + 1, maxDepth);
    var totalIterations = 1;
    while (delta > maxDelta) {
      delta = scheduleToLoop.visit(depth + 1, maxDepth);
      if (totalIterations > MAX_ITERATIONS)
        throw new RuntimeException(format("Maximum iterations (%d) reached.", MAX_ITERATIONS));
      totalIterations += 1
    }
    return delta;
  }
}