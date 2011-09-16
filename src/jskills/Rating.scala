package jskills

import collection.JavaConversions._
import jskills.numerics.MathUtils.square
import java.util.Collection
import jskills.numerics.GaussianDistribution
import scala.reflect.BeanProperty

object Rating {
  private val defaultConservativeStandardDeviationMultiplier: Double = 3

  def partialUpdate(prior: Rating, fullPosterior: Rating, updatePercentage: Double): Rating = {
    val priorGaussian = new GaussianDistribution(prior)
    val posteriorGaussian = new GaussianDistribution(fullPosterior)

    // From a clarification email from Ralf Herbrich:
    // "the idea is to compute a linear interpolation between the prior and
    // posterior skills of each player ... in the canonical space of
    // parameters"

    val precisionDifference = posteriorGaussian.precision - priorGaussian.precision
    val partialPrecisionDifference = updatePercentage * precisionDifference

    val precisionMeanDifference = posteriorGaussian.precisionMean - priorGaussian.precisionMean
    val partialPrecisionMeanDifference = updatePercentage * precisionMeanDifference

    val partialPosteriorGaussion = GaussianDistribution.fromPrecisionMean(
      priorGaussian.precisionMean + partialPrecisionMeanDifference,
      priorGaussian.precision + partialPrecisionDifference)

    new Rating(partialPosteriorGaussion.mean, partialPosteriorGaussion.standardDeviation,
      prior.getConservativeStandardDeviationMultiplier())
  }

  def calcMeanMean(ratings: Collection[Rating]): Double = (ratings map (_.mean) sum) / ratings.size
}

/** Container for a player's rating. **/
class Rating(
  /** The statistical mean value of the rating (also known as μ).*/
  @BeanProperty val mean: Double,
  /** The number of standardDeviation to subtract from the mean to achieve a conservative rating.*/
  @BeanProperty val standardDeviation: Double,
  /** The number of standardDeviations to subtract from the mean to achieve a conservative rating.*/
  @BeanProperty val conservativeStandardDeviationMultiplier: Double) {

  //needed for java compatibility
  def this(mean: Double, standardDeviation: Double) = this(mean, standardDeviation, Rating.defaultConservativeStandardDeviationMultiplier)

  /** A conservative estimate of skill based on the mean and standard deviation. **/
  @BeanProperty val conservativeRating: Double = mean - conservativeStandardDeviationMultiplier * standardDeviation

  /** The variance of the rating (standard deviation squared) **/
  def getVariance(): Double = square(standardDeviation)

  override def toString(): String = format("Mean(μ)=%f, Std-Dev(σ)=%f", mean, standardDeviation)

}