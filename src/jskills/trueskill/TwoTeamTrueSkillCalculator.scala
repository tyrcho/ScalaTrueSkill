package jskills.trueskill

import jskills.numerics.MathUtils._

import java.util.Collection
import java.util.EnumSet
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Map.Entry

import jskills.GameInfo
import jskills.Guard
import jskills.IPlayer
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.Range
import collection.JavaConversions._

/**
 * Calculates new ratings for only two teams where each team has 1 or more
 * players. [remarks] When you only have two teams, the math is still simple: no
 * factor graphs are used yet. [/remarks]
 */
class TwoTeamTrueSkillCalculator
  extends SkillCalculator(Seq(), Range.exactly(2), Range.atLeast(1)) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Collection[_ <: ITeam], teamRanks: Seq[Int]): Map[IPlayer, Rating] = {
    Guard.argumentNotNull(gameInfo, "gameInfo")
    validateTeamCountAndPlayersCountPerTeam(teams)

    val teamsl = RankSorter.sort(teams, teamRanks)

    val team1 = teamsl.get(0)
    val team2 = teamsl.get(1)

    val wasDraw = (teamRanks(0) == teamRanks(1))

    val results = new HashMap[IPlayer, Rating]()

    UpdatePlayerRatings(gameInfo, results, team1, team2,
      if (wasDraw) PairwiseComparison.DRAW else PairwiseComparison.WIN)

    UpdatePlayerRatings(gameInfo, results, team2, team1,
      if (wasDraw) PairwiseComparison.DRAW else PairwiseComparison.LOSE)

    return results
  }

  private def UpdatePlayerRatings(gameInfo: GameInfo,
    newPlayerRatings: Map[IPlayer, Rating],
    selfTeam: ITeam,
    otherTeam: ITeam,
    selfToOtherTeamComparison: PairwiseComparison) {
    val drawMargin = DrawMargin.GetDrawMarginFromDrawProbability(
      gameInfo.drawProbability, gameInfo.beta)
    val betaSquared = square(gameInfo.beta)
    val tauSquared = square(gameInfo.dynamicsFactor)

    val totalPlayers = selfTeam.size() + otherTeam.size()

    var selfMeanSum = 0.0
    for (r <- selfTeam.values()) selfMeanSum += r.mean
    var otherTeamMeanSum = 0.0
    for (r <- otherTeam.values()) otherTeamMeanSum += r.mean

    var sum = 0.0
    for (r <- selfTeam.values()) sum += square(r.standardDeviation)
    for (r <- otherTeam.values()) sum += square(r.standardDeviation)

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
      v = TruncatedGaussianCorrectionFunctions.VExceedsMargin(meanDelta,
        drawMargin, c)
      w = TruncatedGaussianCorrectionFunctions.WExceedsMargin(meanDelta,
        drawMargin, c)
      rankMultiplier = selfToOtherTeamComparison.multiplier
    } else {
      // assume draw
      v = TruncatedGaussianCorrectionFunctions.VWithinMargin(meanDelta,
        drawMargin, c)
      w = TruncatedGaussianCorrectionFunctions.WWithinMargin(meanDelta,
        drawMargin, c)
      rankMultiplier = 1
    }

    for (teamPlayerRatingPair <- selfTeam.entrySet()) {
      val previousPlayerRating = teamPlayerRatingPair.getValue()

      val meanMultiplier = (square(previousPlayerRating.standardDeviation) + tauSquared) / c
      val stdDevMultiplier = (square(previousPlayerRating.standardDeviation) + tauSquared) / square(c)

      val playerMeanDelta = (rankMultiplier * meanMultiplier * v)
      val newMean = previousPlayerRating.mean + playerMeanDelta

      val newStdDev = Math.sqrt((square(previousPlayerRating
        .standardDeviation) + tauSquared)
        * (1 - w * stdDevMultiplier))

      newPlayerRatings.put(teamPlayerRatingPair.getKey(), new Rating(newMean, newStdDev))
    }
  }

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Collection[_ <: ITeam]): Double = {
    Guard.argumentNotNull(gameInfo, "gameInfo")
    validateTeamCountAndPlayersCountPerTeam(teams)

    val teamsIt = teams.iterator()

    // We've verified that there's just two teams
    val team1 = teamsIt.next().values()
    val team1Count = team1.size()

    val team2 = teamsIt.next().values()
    val team2Count = team2.size()

    val totalPlayers = team1Count + team2Count

    val betaSquared = square(gameInfo.beta)

    var team1MeanSum = 0.0
    for (r <- team1) team1MeanSum += r.mean
    var team1StdDevSquared = 0.0
    for (r <- team1) team1StdDevSquared += square(r.standardDeviation)

    var team2MeanSum = 0.0
    for (r <- team2) team2MeanSum += r.mean
    var team2SigmaSquared = 0.0
    for (r <- team2) team2SigmaSquared += square(r.standardDeviation)

    // This comes from equation 4.1 in the TrueSkill paper on page 8
    // The equation was broken up into the part under the square root sign
    // and
    // the exponential part to make the code easier to read.

    val sqrtPart = Math
      .sqrt((totalPlayers * betaSquared)
        / (totalPlayers * betaSquared + team1StdDevSquared + team2SigmaSquared))

    val expPart = Math
      .exp((-1 * square(team1MeanSum - team2MeanSum))
        / (2 * (totalPlayers * betaSquared + team1StdDevSquared + team2SigmaSquared)))

    return expPart * sqrtPart
  }
}