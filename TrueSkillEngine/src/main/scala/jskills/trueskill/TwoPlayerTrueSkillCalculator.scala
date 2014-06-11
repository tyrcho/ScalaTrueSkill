package jskills.trueskill

import jskills.GameInfo
import jskills.PairwiseComparison
import jskills.PairwiseComparison.DRAW
import jskills.PairwiseComparison.LOSE
import jskills.PairwiseComparison.WIN
import jskills.Player
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.MathUtils.square

/**
 * Calculates the new ratings for only two players. [remarks] When you only have
 * two players, a lot of the math simplifies. The main purpose of this class is
 * to show the bare minimum of what a TrueSkill implementation should have.
 * [/remarks]
 */
class TwoPlayerTrueSkillCalculator
  extends SkillCalculator(Seq(), 2 to 2, 1 to Int.MaxValue) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[Map[Player, Rating]], tr: Seq[Int]): Map[Player, Rating] = {
    // Basic argument checking
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

    Map(winner ->
      calculateNewRating(gameInfo, winnerPreviousRating,
        loserPreviousRating, if (wasDraw) DRAW else WIN),
      loser ->
        calculateNewRating(gameInfo, loserPreviousRating,
          winnerPreviousRating, if (wasDraw) DRAW else LOSE))
  }

  def calculateNewRatings(
    gameInfo: GameInfo,
    winnerRating: Rating,
    loserRating: Rating,
    result: PairwiseComparison): (Rating, Rating) =
    (calculateNewRating(gameInfo, winnerRating, loserRating, result),
      calculateNewRating(gameInfo, loserRating, winnerRating, result.opposite))

  private def calculateNewRating(gameInfo: GameInfo,
    selfRating: Rating, opponentRating: Rating,
    comparison: PairwiseComparison): Rating = {
    val drawMargin = gameInfo.drawMargin

    val c = Math.sqrt(square(selfRating.standardDeviation) + square(opponentRating.standardDeviation) + 2 * square(gameInfo.beta))

    val (winningMean, losingMean) = comparison match {
      case LOSE =>
        (opponentRating.mean, selfRating.mean)
      case _ =>
        (selfRating.mean, opponentRating.mean)
    }

    val meanDelta = winningMean - losingMean

    import TruncatedGaussianCorrectionFunctions._
    val (v, w, rankMultiplier) =
      if (comparison != DRAW)
        (VExceedsMargin(meanDelta, drawMargin, c),
          WExceedsMargin(meanDelta, drawMargin, c),
          comparison.multiplier)
      else
        (VWithinMargin(meanDelta, drawMargin, c),
          WWithinMargin(meanDelta, drawMargin, c),
          1)

    val meanMultiplier = (square(selfRating.standardDeviation) + square(gameInfo.dynamicsFactor)) / c

    val varianceWithDynamics = square(selfRating.standardDeviation) + square(gameInfo.dynamicsFactor)
    val stdDevMultiplier = varianceWithDynamics / square(c)

    val newMean = selfRating.mean + (rankMultiplier * meanMultiplier * v)
    val newStdDev = Math.sqrt(varianceWithDynamics * (1 - w * stdDevMultiplier))

    Rating(newMean, newStdDev)
  }

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[Map[Player, Rating]]): Double = {
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