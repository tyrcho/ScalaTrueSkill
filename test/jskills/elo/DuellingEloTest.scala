package jskills.elo

import java.util.Arrays
import java.util.Collection
import java.util.Map

import jskills.GameInfo
import jskills.IPlayer
import jskills.ITeam
import jskills.Player
import jskills.Rating
import jskills.SkillCalculator
import jskills.Team

import org.junit.Test
import org.junit.Assert._

class DuellingEloTest {
  val ErrorTolerance = 0.1

  @Test
  def twoOnTwoDuellingTest() {
    val calculator = new DuellingEloCalculator(new GaussianEloCalculator())

    val gameInfo = GameInfo.getDefaultGameInfo()

    val player1 = new Player(1)
    val player2 = new Player(2)

    val team1 = new Team()
      .addPlayer(player1, gameInfo.getDefaultRating())
      .addPlayer(player2, gameInfo.getDefaultRating())

    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = new Team()
      .addPlayer(player3, gameInfo.getDefaultRating())
      .addPlayer(player4, gameInfo.getDefaultRating())

    val teams = Arrays.asList(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(2, 1))

    // TODO: Verify?
    AssertRating(37, newRatingsWinLose.get(player1))
    AssertRating(37, newRatingsWinLose.get(player2))
    AssertRating(13, newRatingsWinLose.get(player3))
    AssertRating(13, newRatingsWinLose.get(player4))

    val quality = calculator.calculateMatchQuality(gameInfo, teams)
    assertEquals(1.0, quality, 0.001)
  }

  def AssertRating(expected: Double, actual: Rating) {
    assertEquals(expected, actual.getMean(), ErrorTolerance)
  }
}