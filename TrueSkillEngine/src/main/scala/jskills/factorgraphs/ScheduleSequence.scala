package jskills.factorgraphs

import java.lang.Math._




class ScheduleSequence[TValue](name: String, schedules: Seq[Schedule[TValue]])
  extends Schedule[TValue](name) {

  override def visit(depth: Int, maxDepth: Int): Double = 
    schedules.foldLeft(0.0)((d, s) => max(s.visit(depth + 1, maxDepth), d))
}
