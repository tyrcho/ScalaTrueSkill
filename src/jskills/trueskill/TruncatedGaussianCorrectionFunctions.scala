package jskills.trueskill

import jskills.numerics.GaussianDistribution._

/**
 * These functions from the bottom of page 4 of the TrueSkill paper.
 */
object TruncatedGaussianCorrectionFunctions {
  /**
   * The "V" function where the team performance difference is greater than the draw margin.
   * <remarks>In the reference F# implementation, this is referred to as "the additive
   * correction of a single-sided truncated Gaussian with unit variance."</remarks>
   * @param teamPerformanceDifference
   * @param drawMargin In the paper, it's referred to as just "?".
   * @returns
   */
  def VExceedsMargin(teamPerformanceDifference: Double, drawMargin: Double, c: Double): Double =
    VExceedsMargin(teamPerformanceDifference / c, drawMargin / c)

  def VExceedsMargin(teamPerformanceDifference: Double, drawMargin: Double): Double = {
    val denominator = cumulativeTo(teamPerformanceDifference - drawMargin)
    if (denominator < 2.222758749e-162) {
      return -teamPerformanceDifference + drawMargin
    }
    return at(teamPerformanceDifference - drawMargin) / denominator
  }

  /**
   * The "W" function where the team performance difference is greater than the draw margin.
   * <remarks>In the reference F# implementation, this is referred to as "the multiplicative
   * correction of a single-sided truncated Gaussian with unit variance."</remarks>
   * @param teamPerformanceDifference
   * @param drawMargin
   * @param c
   * @returns
   */
  def WExceedsMargin(teamPerformanceDifference: Double, drawMargin: Double, c: Double): Double = {
    return WExceedsMargin(teamPerformanceDifference / c, drawMargin / c)
    //var vWin = VExceedsMargin(teamPerformanceDifference, drawMargin, c)
    //return vWin * (vWin + (teamPerformanceDifference - drawMargin) / c)
  }

  def WExceedsMargin(teamPerformanceDifference: Double, drawMargin: Double): Double = {
    val denominator = cumulativeTo(teamPerformanceDifference - drawMargin)

    if (denominator < 2.222758749e-162) {
      if (teamPerformanceDifference < 0.0) {
        return 1.0
      }
      return 0.0
    }

    val vWin = VExceedsMargin(teamPerformanceDifference, drawMargin)
    return vWin * (vWin + teamPerformanceDifference - drawMargin)
  }

  // the additive correction of a double-sided truncated Gaussian with unit variance
  def VWithinMargin(teamPerformanceDifference: Double, drawMargin: Double, c: Double): Double = {
    VWithinMargin(teamPerformanceDifference / c, drawMargin / c)
  }

  // from F#:
  def VWithinMargin(teamPerformanceDifference: Double, drawMargin: Double): Double = {
    val teamPerformanceDifferenceAbsoluteValue = Math.abs(teamPerformanceDifference)
    val denominator =
      cumulativeTo(drawMargin - teamPerformanceDifferenceAbsoluteValue) -
        cumulativeTo(-drawMargin - teamPerformanceDifferenceAbsoluteValue)
    if (denominator < 2.222758749e-162) {
      if (teamPerformanceDifference < 0.0) {
        return -teamPerformanceDifference - drawMargin
      }

      return -teamPerformanceDifference + drawMargin
    }

    val numerator = at(-drawMargin - teamPerformanceDifferenceAbsoluteValue) -
      at(drawMargin - teamPerformanceDifferenceAbsoluteValue)

    if (teamPerformanceDifference < 0.0) {
      return -numerator / denominator
    }

    return numerator / denominator
  }

  // the multiplicative correction of a double-sided truncated Gaussian with unit variance
  def WWithinMargin(teamPerformanceDifference: Double, drawMargin: Double, c: Double): Double =
    WWithinMargin(teamPerformanceDifference / c, drawMargin / c)

  // From F#:
  def WWithinMargin(teamPerformanceDifference: Double, drawMargin: Double): Double = {
    val teamPerformanceDifferenceAbsoluteValue = Math.abs(teamPerformanceDifference)
    val denominator = cumulativeTo(drawMargin - teamPerformanceDifferenceAbsoluteValue) -
      cumulativeTo(-drawMargin - teamPerformanceDifferenceAbsoluteValue)

    if (denominator < 2.222758749e-162) {
      return 1.0
    }

    val vt = VWithinMargin(teamPerformanceDifferenceAbsoluteValue, drawMargin)

    return vt * vt +
      ((drawMargin - teamPerformanceDifferenceAbsoluteValue) * at(drawMargin - teamPerformanceDifferenceAbsoluteValue) -
        (-drawMargin - teamPerformanceDifferenceAbsoluteValue) * at(-drawMargin - teamPerformanceDifferenceAbsoluteValue)) /
        denominator
  }
}