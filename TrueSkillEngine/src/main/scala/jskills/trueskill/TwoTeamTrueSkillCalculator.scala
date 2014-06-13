package jskills.trueskill

import jskills.GameInfo
import jskills.Player
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.MathUtils.square
/**
 *
 * Calculates new ratings for only two teams where each team has 1 or more
 * players. [remarks] When you only have two teams, the math is still simple: no
 * factor graphs are used yet. [/remarks]
 */
class TwoTeamTrueSkillCalculator
  extends SkillCalculator(Seq(), 2 to 2, 1 to Int.MaxValue) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[Map[Player, Rating]], teamRanks: Seq[Int]): Map[Player, Rating] = {
    validateTeamCountAndPlayersCountPerTeam(teams)

    val teamsl = RankSorter.sort(teams, teamRanks)

    val team1 = teamsl(0)
    val team2 = teamsl(1)

    val wasDraw = (teamRanks(0) == teamRanks(1))

    val results = updatePlayerRatings(gameInfo, Map.empty[Player, Rating], team1, team2, if (wasDraw) PairwiseComparison.DRAW else PairwiseComparison.WIN)
    val results2 = updatePlayerRatings(gameInfo, results, team2, team1, if (wasDraw) PairwiseComparison.DRAW else PairwiseComparison.LOSE)

    results2
  }

  private def updatePlayerRatings(gameInfo: GameInfo,
    newPlayerRatings: Map[Player, Rating],
    selfTeam: ITeam,
    otherTeam: ITeam,
    selfToOtherTeamComparison: PairwiseComparison) = {
    val drawMargin = DrawMargin.getDrawMarginFromDrawProbability(gameInfo.drawProbability, gameInfo.beta)
    val betaSquared = square(gameInfo.beta)
    val tauSquared = square(gameInfo.dynamicsFactor)

    val totalPlayers = selfTeam.size + otherTeam.size

    val selfMeanSum = selfTeam.values map (_.mean) sum
    val otherTeamMeanSum = otherTeam.values map (_.mean) sum

    val sum = (selfTeam.values.toList ::: otherTeam.values.toList) map
      (r => square(r.standardDeviation)) sum

    val c = Math.sqrt(sum + totalPlayers * betaSquared)

    var winningMean = selfMeanSum
    var losingMean = otherTeamMeanSum

    selfToOtherTeamComparison match {
      case PairwiseComparison.LOSE => {
        winningMean = otherTeamMeanSum
        losingMean = selfMeanSum
      }
      case _ =>
    }

    val meanDelta = winningMean - losingMean

    var v = 0.0
    var w = 0.0
    var rankMultiplier = 0.0

    if (selfToOtherTeamComparison != PairwiseComparison.DRAW) {
      // non-draw case
      v = TruncatedGaussianCorrectionFunctions.VExceedsMargin(meanDelta, drawMargin, c)
      w = TruncatedGaussianCorrectionFunctions.WExceedsMargin(meanDelta, drawMargin, c)
      rankMultiplier = selfToOtherTeamComparison.multiplier
    } else {
      // assume draw
      v = TruncatedGaussianCorrectionFunctions.VWithinMargin(meanDelta, drawMargin, c)
      w = TruncatedGaussianCorrectionFunctions.WWithinMargin(meanDelta, drawMargin, c)
      rankMultiplier = 1
    }

    var ratings = newPlayerRatings
    for (teamPlayerRatingPair <- selfTeam) {
      val previousPlayerRating = teamPlayerRatingPair._2

      val meanMultiplier = (square(previousPlayerRating.standardDeviation) + tauSquared) / c
      val stdDevMultiplier = (square(previousPlayerRating.standardDeviation) + tauSquared) / square(c)

      val playerMeanDelta = (rankMultiplier * meanMultiplier * v)
      val newMean = previousPlayerRating.mean + playerMeanDelta

      val newStdDev = Math.sqrt((square(previousPlayerRating.standardDeviation) + tauSquared) * (1 - w * stdDevMultiplier))

      ratings += teamPlayerRatingPair._1 -> new Rating(newMean, newStdDev)
    }
    ratings
  }

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[Map[Player, Rating]]): Double = {
    validateTeamCountAndPlayersCountPerTeam(teams)

    // We've verified that there's just two teams
    val team1 = teams(0).values
    val team1Count = team1.size

    val team2 = teams(1).values
    val team2Count = team2.size

    val totalPlayers = team1Count + team2Count

    val betaSquared = square(gameInfo.beta)

    val team1MeanSum = team1 map (_.mean) sum
    val team1StdDevSquared = team1 map (r => square(r.standardDeviation)) sum

    val team2MeanSum = team2 map (_.mean) sum
    val team2SigmaSquared = team2 map (r => square(r.standardDeviation)) sum

    // This comes from equation 4.1 in the TrueSkill paper on page 8
    // The equation was broken up into the part under the square root sign
    // and
    // the exponential part to make the code easier to read.

    val sqrtPart = Math.sqrt((totalPlayers * betaSquared)
      / (totalPlayers * betaSquared + team1StdDevSquared + team2SigmaSquared))

    val expPart = Math.exp((-1 * square(team1MeanSum - team2MeanSum))
      / (2 * (totalPlayers * betaSquared + team1StdDevSquared + team2SigmaSquared)))

    expPart * sqrtPart
  }
}