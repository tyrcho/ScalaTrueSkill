package jskills.elo

import java.util.ArrayList
import java.util.Collection
import java.util.EnumSet
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
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
import collection.JavaConversions._

abstract class TwoPlayerEloCalculator(kFactor: KFactor)
  extends SkillCalculator(Seq(), Range.exactly(2), Range.exactly(1)) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Collection[_ <: ITeam], teamRanks: Seq[Int]): Map[IPlayer, Rating] = {
    validateTeamCountAndPlayersCountPerTeam(teams)
    val teamsl = RankSorter.sort(teams, teamRanks)

    val result = new HashMap[IPlayer, Rating]()
    val isDraw = (teamRanks(0) == teamRanks(1))

    val players = new ArrayList[IPlayer](2)
    for (team <- teamsl)
      players.add(team.keySet().toArray(new Array[IPlayer](1))(0))

    val player1Rating = teamsl.get(0).get(players.get(0)).getMean()
    val player2Rating = teamsl.get(1).get(players.get(1)).getMean()

    result.put(
      players.get(0),
      calculateNewRating(gameInfo, player1Rating, player2Rating,
        if (isDraw) PairwiseComparison.DRAW else PairwiseComparison.WIN))
    result.put(
      players.get(1),
      calculateNewRating(gameInfo, player2Rating, player1Rating,
        if (isDraw) PairwiseComparison.DRAW else PairwiseComparison.LOSE))

    return result
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

    return new EloRating(newRating)
  }

  protected def getPlayerWinProbability(gameInfo: GameInfo, playerRating: Double, opponentRating: Double): Double

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Collection[_ <: ITeam]): Double = {
    validateTeamCountAndPlayersCountPerTeam(teams)

    // Extract both players from the teams
    val players = new ArrayList[IPlayer](2)
    for (team <- teams)
      players.add(team.keySet().toArray(new Array[IPlayer](0))(0))

    // Extract each player's rating from their team
    val teamit = teams.iterator()
    val player1Rating = teamit.next().get(players.get(0)).getMean()
    val player2Rating = teamit.next().get(players.get(1)).getMean()

    // The TrueSkill paper mentions that they used s1 - s2 (rating
    // difference) to determine match quality. I convert that to a
    // percentage as a delta from 50% using the cumulative density function
    // of the specific curve being used
    val deltaFrom50Percent = Math.abs(getPlayerWinProbability(gameInfo,
      player1Rating, player2Rating) - 0.5)
    return (0.5 - deltaFrom50Percent) / 0.5
  }
}

object TwoPlayerEloCalculator {
  private def getScoreFromComparison(comparison: PairwiseComparison): Double = {
    comparison match {
      case WIN => 1
      case DRAW => 0.5
      case LOSE => 0
    }
  }
}