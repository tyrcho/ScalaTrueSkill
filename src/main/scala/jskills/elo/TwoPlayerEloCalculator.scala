package jskills.elo

import jskills.GameInfo
import jskills.IPlayer
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.Range
import jskills.SupportedOptions
import jskills.PairwiseComparison._

import collection.mutable.Map

abstract class TwoPlayerEloCalculator(kFactor: KFactor)
  extends SkillCalculator(Seq(), Range.exactly(2), Range.exactly(1)) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[_ <: ITeam],
    teamRanks: Seq[Int]): Map[IPlayer, Rating] = {
    validateTeamCountAndPlayersCountPerTeam(teams)
    val teamsl = RankSorter.sort(teams, teamRanks)

    val result = Map.empty[IPlayer, Rating]
    val isDraw = (teamRanks(0) == teamRanks(1))

    val players = teamsl map (_.keySet.head)

    val player1Rating = teamsl(0)(players(0)).mean
    val player2Rating = teamsl(1)(players(1)).mean

    result.put(players(0),
      calculateNewRating(gameInfo, player1Rating, player2Rating,
        if (isDraw) PairwiseComparison.DRAW else PairwiseComparison.WIN))
    result.put(players(1),
      calculateNewRating(gameInfo, player2Rating, player1Rating,
        if (isDraw) PairwiseComparison.DRAW else PairwiseComparison.LOSE))

    result
  }

  protected def calculateNewRating(gameInfo: GameInfo,
    selfRating: Double, opponentRating: Double,
    selfToOpponentComparison: PairwiseComparison): EloRating = {
    val expectedProbability = getPlayerWinProbability(gameInfo,
      selfRating, opponentRating)
    val actualProbability = TwoPlayerEloCalculator.getScoreFromComparison(selfToOpponentComparison)
    val k = kFactor.getValueForRating(selfRating)
    val ratingChange = k * (actualProbability - expectedProbability)
    val newRating = selfRating + ratingChange

    new EloRating(newRating)
  }

  protected def getPlayerWinProbability(gameInfo: GameInfo, playerRating: Double, opponentRating: Double): Double

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[_ <: ITeam]): Double = {
    validateTeamCountAndPlayersCountPerTeam(teams)

    // Extract both players from the teams
    val players = teams map (_.keySet.head)

    // Extract each player's rating from their team
    val teamit = teams.iterator
    val player1Rating = teamit.next()(players(0)).mean
    val player2Rating = teamit.next()(players(1)).mean

    // The TrueSkill paper mentions that they used s1 - s2 (rating
    // difference) to determine match quality. I convert that to a
    // percentage as a delta from 50% using the cumulative density function
    // of the specific curve being used
    val deltaFrom50Percent = Math.abs(getPlayerWinProbability(gameInfo,
      player1Rating, player2Rating) - 0.5)
    (0.5 - deltaFrom50Percent) / 0.5
  }
}

object TwoPlayerEloCalculator {
  private def getScoreFromComparison(comparison: PairwiseComparison): Double = comparison match {
    case WIN => 1
    case DRAW => 0.5
    case LOSE => 0
  }
}