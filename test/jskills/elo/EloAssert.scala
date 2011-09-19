package jskills.elo

import org.junit.Assert._

import java.util.Arrays

import jskills.GameInfo
import jskills.IPlayer
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.Player
import jskills.Rating
import jskills.Team
import collection.JavaConversions._

object EloAssert {
  val ErrorTolerance = 0.1

  def assertChessRating(calculator: TwoPlayerEloCalculator,
    player1BeforeRating: Double,
    player2BeforeRating: Double,
    player1Result: PairwiseComparison,
    player1AfterRating: Double,
    player2AfterRating: Double) {
    val player1 = new Player[Integer](1)
    val player2 = new Player[Integer](2)

    val team1 = Team(player1, new EloRating(player1BeforeRating))
    val team2 = Team(player2, new EloRating(player2BeforeRating))
    val teams = Seq(team1, team2)

    val chessGameInfo = new GameInfo(1200, 0, 200, 0, 0)

    val result = calculator.calculateNewRatings(chessGameInfo, teams,
      player1Result match {
        case PairwiseComparison.WIN => Seq(1, 2)
        case PairwiseComparison.LOSE => Seq(2, 1)
        case PairwiseComparison.DRAW => Seq(1, 1)
      })

    assertEquals(player1AfterRating, result(player1).mean, ErrorTolerance)
    assertEquals(player2AfterRating, result(player2).mean, ErrorTolerance)
  }
}