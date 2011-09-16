package jskills;

import java.util.Map
import java.util.Collection

import jskills.trueskill.FactorGraphTrueSkillCalculator

/**
 * Calculates a TrueSkill rating using {@link FactorGraphTrueSkillCalculator}.
 */
object TrueSkillCalculator {
  // Keep a singleton around
  val _Calculator = new FactorGraphTrueSkillCalculator();

  /**
   * Calculates new ratings based on the prior ratings and team ranks.
   * @param gameInfo Parameters for the game.
   * @param teams A mapping of team players and their ratings.
   * @param teamRanks The ranks of the teams where 1 is first place. For a tie, repeat the number (e.g. 1, 2, 2)
   * @returns All the players and their new ratings.
   */
  def calculateNewRatings(gameInfo: GameInfo,
    teams: Collection[ITeam], teamRanks: Seq[Int]): Map[IPlayer, Rating] =
    // Just punt the work to the full implementation
    _Calculator.calculateNewRatings(gameInfo, teams, teamRanks);

  /**
   * Calculates the match quality as the likelihood of all teams drawing.
   * @param gameInfo Parameters for the game.
   * @param teams A mapping of team players and their ratings.
   * @returns The match quality as a percentage (between 0.0 and 1.0).
   */
  def calculateMatchQuality(gameInfo: GameInfo, teams: Collection[ITeam]): Double =
    // Just punt the work to the full implementation
    _Calculator.calculateMatchQuality(gameInfo, teams);
}