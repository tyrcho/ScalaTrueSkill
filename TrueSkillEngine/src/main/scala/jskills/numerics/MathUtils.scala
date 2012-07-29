package jskills.numerics

/**
 * For all the functions that aren't in java.lang.Math
 */
object MathUtils {
  @inline def square(x: Double) = x * x
  def mean(collection: Iterable[Double]): Double = collection.sum / collection.size
}
