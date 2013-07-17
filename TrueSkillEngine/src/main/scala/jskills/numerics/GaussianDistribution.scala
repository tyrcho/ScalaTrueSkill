package jskills.numerics

import java.lang.Math._
import jskills.numerics.MathUtils._
import jskills.Rating

/**
 * Immutable representation of the Gaussian distribution of one variable. Not
 * normalized:
 *
 * <pre>
 *            1          -(x)^2 / (2)
 * P(x) = ----------- * e
 *        sqrt(2*pi)
 * </pre>
 *
 * Normalized:
 *
 * <pre>
 *               1           -(x-\u03bc)^2 / (2*\u03c3^2)
 * P(x) = --------------- * e
 *        \u03c3 * sqrt(2*pi)
 * </pre>
 *
 * @see http://mathworld.wolfram.com/NormalDistribution.html
 */
case class GaussianDistribution(
  /** The peak of the Gaussian, \u03bc **/
  mean: Double,
  /** The width of the Gaussian, \u03c3, where the height drops to max/e **/
  standardDeviation: Double,
  /** The square of the standard deviation, \u03c3^2 **/
  variance: Double,
  // Precision and PrecisionMean are used because they make multiplying and
  // dividing simpler (see the accompanying math paper for more details)
  /** 1/\u03c3^2 **/
  precision: Double,
  /** Precision times mean, \u03bc/\u03c3^2 **/
  precisionMean: Double) {

  import GaussianDistribution._

  /**
   * The normalization constant multiplies the exponential and causes the
   * integral over (-Inf,Inf) to equal 1
   *
   * @ 1/sqrt(2*pi*\u03c3)
   */
  // Great derivation of this is at
  // http://www.astro.psu.edu/~mce/A451_2/A451/downloads/notes0.pdf
  def getNormalizationConstant = 1.0 / (sqrt(2 * PI) * standardDeviation)

  def *(other: GaussianDistribution): GaussianDistribution = GaussianDistribution.prod(this, other)
  def /(other: GaussianDistribution): GaussianDistribution = GaussianDistribution.divide(this, other)

  def *(ratio: Double): GaussianDistribution = GaussianDistribution(ratio * mean, ratio * standardDeviation)

  def +(shift: Double): GaussianDistribution = GaussianDistribution(mean + shift, standardDeviation)
  def -(shift: Double): GaussianDistribution = GaussianDistribution(mean - shift, standardDeviation)

  def +(other: GaussianDistribution): GaussianDistribution =
    GaussianDistribution(other.mean + mean,
      Math.sqrt(standardDeviation * standardDeviation + other.standardDeviation * other.standardDeviation))

  override def toString = format("Mean(\u03bc)=%f, Std-Dev(\u03c3)=%f", mean, standardDeviation)

  /**
   * <pre>
   *               1          -(x)^2 / (2*stdDev^2)
   *   P(x) = ------------ * e
   *           sqrt(2*pi)
   * </pre>
   *
   * @param x
   *            the location to evaluate a normalized Gaussian at
   * @ the value at x of a normalized Gaussian centered at 0 of unit
   *         width.
   * @see http://mathworld.wolfram.com/NormalDistribution.html
   */
  def apply(x: Double): Double = {
    val multiplier = 1.0 / (standardDeviation * sqrt(2 * PI))
    val expPart = exp((-1.0 * pow(x - mean, 2.0)) / (2 * (standardDeviation * standardDeviation)))
    multiplier * expPart
  }

  // From numerical recipes, page 320
  def inverseCumulativeTo(x: Double): Double =
    mean - sqrt(2) * standardDeviation * inverseErrorFunctionCumulativeTo(2 * x)

}

object GaussianDistribution {
  /**
   * The Gaussian representation of a flat line.
   */
  def UNIFORM: GaussianDistribution = fromPrecisionMean(0, 0)
  def STANDARD = GaussianDistribution(0, 1)

  def apply(mean: Double, standardDeviation: Double) =
    new GaussianDistribution(mean, standardDeviation, square(standardDeviation), 1.0 / square(standardDeviation), mean / square(standardDeviation))

  def apply(rating: Rating): GaussianDistribution =
    GaussianDistribution(rating.mean, rating.standardDeviation)

  def apply(distribution: GaussianDistribution) =
    new GaussianDistribution(distribution.mean, distribution.standardDeviation, distribution.variance, distribution.precision, distribution.precisionMean)

  def cumulativeTo(x: Double): Double = {
    val invsqrt2 = -0.7071067811865476
    val result = errorFunctionCumulativeTo(invsqrt2 * x)
    0.5 * result
  }

  private def inverseErrorFunctionCumulativeTo(p: Double): Double = {
    // From page 265 of numerical recipes                       
    if (p >= 2.0) -100
    if (p <= 0.0) 100
    val pp = if (p < 1.0) p else 2 - p
    val t = sqrt(-2 * log(pp / 2.0)) // Initial guess
    var x = -0.70711 * ((2.30753 + t * 0.27061) / (1.0 + t * (0.99229 + t * 0.04481)) - t)

    for (j <- 0 until 2) {
      val err = errorFunctionCumulativeTo(x) - pp
      x += err / (1.1283791670955126 * exp(-(x * x)) - x * err) // Halley
    }
    if (p < 1.0) x else -x
  }

  def inverseCumulativeTo(x: Double): Double = STANDARD.inverseCumulativeTo(x)

  def at(x: Double): Double = STANDARD(x)

  def errorFunctionCumulativeTo(x: Double): Double = {
    // Derived from page 265 of Numerical Recipes 3rd Edition            
    val z = abs(x)
    val t = 2.0 / (2.0 + z)
    val ty = 4 * t - 2
    val coefficients = Array(-1.3026537197817094, 0.6419697923564902, 0.019476473204185836, -0.00956151478680863, -9.46595344482036E-4, 3.66839497852761E-4, 4.2523324806907E-5, -2.0278578112534E-5, -1.624290004647E-6, 1.30365583558E-6, 1.5626441722E-8, -8.5238095915E-8, 6.529054439E-9, 5.059343495E-9, -9.91364156E-10, -2.27365122E-10, 9.6467911E-11, 2.394038E-12, -6.886027E-12, 8.94487E-13, 3.13092E-13, -1.12708E-13, 3.81E-16, 7.106E-15, -1.523E-15, -9.4E-17, 1.21E-16, -2.8E-17)
    val ncof = coefficients.length
    var d = 0.0
    var dd = 0.0
    for (j <- (ncof - 1).until(0, -1)) {
      val tmp = d
      d = ty * d - dd + coefficients(j)
      dd = tmp
    }
    val ans = t * exp(-z * z + 0.5 * (coefficients(0) + ty * d) - dd)
    if (x >= 0.0) ans else (2.0 - ans)
  }

  // Although we could use equations from
  // http://www.tina-vision.net/tina-knoppix/tina-memo/2003-003.pdf
  // for multiplication, the precision mean ones are easier to write :)
  def prod(left: GaussianDistribution, right: GaussianDistribution): GaussianDistribution =
    fromPrecisionMean(left.precisionMean + right.precisionMean, left.precision + right.precision)

  /** Computes the absolute difference between two Gaussians **/
  def absoluteDifference(left: GaussianDistribution, right: GaussianDistribution): Double =
    max(abs(left.precisionMean - right.precisionMean), sqrt(abs(left.precision - right.precision)))

  /** Computes the absolute difference between two Gaussians **/
  def sub(left: GaussianDistribution, right: GaussianDistribution) = absoluteDifference(left, right)

  def logProductNormalization(left: GaussianDistribution, right: GaussianDistribution): Double = {
    if ((left.precision == 0) || (right.precision == 0)) 0
    val varianceSum = left.variance + right.variance
    val meanDifference = left.mean - right.mean
    val logSqrt2Pi = log(sqrt(2 * PI))
    -logSqrt2Pi - (log(varianceSum) / 2.0) - (square(meanDifference) / (2.0 * varianceSum))
  }

  def divide(numerator: GaussianDistribution, denominator: GaussianDistribution): GaussianDistribution =
    fromPrecisionMean(numerator.precisionMean - denominator.precisionMean, numerator.precision - denominator.precision)

  def logRatioNormalization(numerator: GaussianDistribution, denominator: GaussianDistribution): Double = {
    if ((numerator.precision == 0) || (denominator.precision == 0)) 0
    val varianceDifference = denominator.variance - numerator.variance
    val meanDifference = numerator.mean - denominator.mean
    val logSqrt2Pi = log(sqrt(2 * PI))
    log(denominator.variance) + logSqrt2Pi - log(varianceDifference) / 2.0 + square(meanDifference) / (2 * varianceDifference)
  }

  def fromPrecisionMean(precisionMean: Double, precision: Double) =
    new GaussianDistribution(precisionMean / precision, sqrt(1.0 / precision), 1.0 / precision, precision, precisionMean)
}
