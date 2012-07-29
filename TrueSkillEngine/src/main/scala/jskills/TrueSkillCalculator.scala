package jskills

import collection.mutable.Map

import jskills.trueskill.FactorGraphTrueSkillCalculator

/**
 * Calculates a TrueSkill rating using {@link FactorGraphTrueSkillCalculator}.
 */
object TrueSkillCalculator {
  // Keep a singleton around
  val calculator = new FactorGraphTrueSkillCalculator()

  /**
   * Calculates new ratings based on the prior ratings and team ranks.
   * @param gameInfo Parameters for the game.
   * @param teams A mapping of team players and their ratings.
   * @param teamRanks The ranks of the teams where 1 is first place. For a tie, repeat the number (e.g. 1, 2, 2)
   * @s All the players and their new ratings.
   */
  def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[ITeam], teamRanks: Seq[Int]): Map[IPlayer, Rating] =
    // Just punt the work to the full implementation
    calculator.calculateNewRatings(gameInfo, teams, teamRanks)

  /**
   * Calculates the match quality as the likelihood of all teams drawing.
   * @param gameInfo Parameters for the game.
   * @param teams A mapping of team players and their ratings.
   * @s The match quality as a percentage (between 0.0 and 1.0).
   */
  def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[ITeam]): Double =
    // Just punt the work to the full implementation
    calculator.calculateMatchQuality(gameInfo, teams)
}