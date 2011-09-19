package jskills.numerics


import collection.JavaConversions._

/**
 * For all the functions that aren't in java.lang.Math
 */
object MathUtils {
  def square(x: Double) = x * x
  def mean(collection: Seq[Double]): Double = collection.sum / collection.size()
}
