package jskills

import jskills.numerics.MathUtils.square

import jskills.numerics.GaussianDistribution
import jskills.numerics.GaussianDistribution._
import math._

object Rating {
  private val defaultConservativeStandardDeviationMultiplier: Double = 3

  def partialUpdate(prior: Rating, fullPosterior: Rating, updatePercentage: Double): Rating = {
    val priorGaussian = GaussianDistribution(prior)
    val posteriorGaussian = GaussianDistribution(fullPosterior)

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
      prior.conservativeStandardDeviationMultiplier)
  }

  def calcMeanMean(ratings: Seq[Rating]): Double = (ratings map (_.mean) sum) / ratings.size

  implicit def toGaussian(r: Rating) = GaussianDistribution(r.mean, r.standardDeviation)

  def avgFromTeam(teamRatings: Iterable[Rating]): Rating = {
    val mean = (teamRatings map (_.mean) sum) / teamRatings.size
    val stdDev = sqrt(teamRatings map (r => square(r.standardDeviation)) sum) / teamRatings.size
    Rating(mean, stdDev)
  }
}

/** Container for a player's rating. **/
case class Rating(
  /** The statistical mean value of the rating (also known as μ).*/
  mean: Double,
  /** The number of standardDeviation to subtract from the mean to achieve a conservative rating.*/
  standardDeviation: Double,
  /** The number of standardDeviations to subtract from the mean to achieve a conservative rating.*/
  conservativeStandardDeviationMultiplier: Double = Rating.defaultConservativeStandardDeviationMultiplier) {

  /** A conservative estimate of skill based on the mean and standard deviation. **/
  val conservativeRating: Double = mean - conservativeStandardDeviationMultiplier * standardDeviation

  /** The variance of the rating (standard deviation squared) **/
  def getVariance: Double = square(standardDeviation)

  override def toString= f"Mean(μ)=$mean%.2f, Std-Dev(σ)=$standardDeviation%.2f, conservativeRating=$conservativeRating%.2f"

}
