package jskills.factorgraphs

import java.lang.Math._
import collection.JavaConversions._

import java.util.Collection

class ScheduleSequence[TValue](name: String, schedules: Collection[Schedule[TValue]])
  extends Schedule[TValue](name) {

  override def visit(depth: Int, maxDepth: Int): Double = 
    schedules.foldLeft(0.0)((d, s) => max(s.visit(depth + 1, maxDepth), d))
}
