package jskills.trueskill

import jskills.numerics.MathUtils._

import jskills.GameInfo
import jskills.Guard
import jskills.IPlayer
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.Range

import collection.mutable.Map

/**
 * Calculates the new ratings for only two players. [remarks] When you only have
 * two players, a lot of the math simplifies. The main purpose of this class is
 * to show the bare minimum of what a TrueSkill implementation should have.
 * [/remarks]
 */
class TwoPlayerTrueSkillCalculator
  extends SkillCalculator(Seq(), Range.exactly(2), Range.exactly(1)) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[_ <: ITeam], tr: Seq[Int]): Map[IPlayer, Rating] = {
    // Basic argument checking
    Guard.argumentNotNull(gameInfo, "gameInfo")
    validateTeamCountAndPlayersCountPerTeam(teams)

    // Make sure things are in order
    val teamsl = RankSorter.sort(teams, tr)
    val teamRanks = tr.sortBy(i => i)

    // Since we verified that each team has one player, we know the player
    // is the first one
    val winningTeam = teamsl(0)
    val winner = winningTeam.head._1
    val winnerPreviousRating = winningTeam(winner)

    val losingTeam = teamsl(1)
    val loser = losingTeam.head._1
    val loserPreviousRating = losingTeam(loser)

    val wasDraw = (teamRanks(0) == teamRanks(1))

    val results = Map.empty[IPlayer, Rating]
    results.put(winner,
      calculateNewRating(gameInfo, winnerPreviousRating,
        loserPreviousRating, if (wasDraw) PairwiseComparison.DRAW else PairwiseComparison.WIN))
    results.put(loser,
      calculateNewRating(gameInfo, loserPreviousRating,
        winnerPreviousRating, if (wasDraw) PairwiseComparison.DRAW else PairwiseComparison.LOSE))

    results
  }

  private def calculateNewRating(gameInfo: GameInfo,
    selfRating: Rating, opponentRating: Rating,
    comparison: PairwiseComparison): Rating = {
    val drawMargin = DrawMargin.getDrawMarginFromDrawProbability(gameInfo.drawProbability, gameInfo.beta)

    val c = Math.sqrt(square(selfRating.standardDeviation) + square(opponentRating.standardDeviation) + 2 * square(gameInfo.beta))

    var winningMean = selfRating.mean
    var losingMean = opponentRating.mean

    comparison match {
      case PairwiseComparison.LOSE => {
        winningMean = opponentRating.mean
        losingMean = selfRating.mean
      }
      case _ =>
    }

    val meanDelta = winningMean - losingMean

    var v: Double = 0
    var w: Double = 0
    var rankMultiplier: Double = 0

    if (comparison != PairwiseComparison.DRAW) {
      // non-draw case
      v = TruncatedGaussianCorrectionFunctions.VExceedsMargin(meanDelta, drawMargin, c)
      w = TruncatedGaussianCorrectionFunctions.WExceedsMargin(meanDelta, drawMargin, c)
      rankMultiplier = comparison.multiplier
    } else {
      v = TruncatedGaussianCorrectionFunctions.VWithinMargin(meanDelta, drawMargin, c)
      w = TruncatedGaussianCorrectionFunctions.WWithinMargin(meanDelta, drawMargin, c)
      rankMultiplier = 1
    }

    val meanMultiplier = (square(selfRating.standardDeviation) + square(gameInfo.dynamicsFactor)) / c

    val varianceWithDynamics = square(selfRating.standardDeviation) + square(gameInfo.dynamicsFactor)
    val stdDevMultiplier = varianceWithDynamics / square(c)

    val newMean = selfRating.mean + (rankMultiplier * meanMultiplier * v)
    val newStdDev = Math.sqrt(varianceWithDynamics * (1 - w * stdDevMultiplier))

     new Rating(newMean, newStdDev)
  }

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[_ <: ITeam]): Double = {
    Guard.argumentNotNull(gameInfo, "gameInfo")
    validateTeamCountAndPlayersCountPerTeam(teams)

    val player1Rating = teams(0).values.head
    val player2Rating = teams(1).values.head

    // We just use equation 4.1 found on page 8 of the TrueSkill 2006 paper:
    val betaSquared = square(gameInfo.beta)
    val player1SigmaSquared = square(player1Rating.standardDeviation)
    val player2SigmaSquared = square(player2Rating.standardDeviation)

    // This is the square root part of the equation:
    val sqrtPart = Math.sqrt((2 * betaSquared) / (2 * betaSquared + player1SigmaSquared + player2SigmaSquared))

    // This is the exponent part of the equation:
    val expPart = Math
      .exp((-1 * square(player1Rating.mean
        - player2Rating.mean))
        / (2 * (2 * betaSquared + player1SigmaSquared + player2SigmaSquared)))

    sqrtPart * expPart
  }
}