package jskills

/**
 * Base class for all skill calculator implementations.
 */
abstract class SkillCalculator(supportedOptions: Seq[SupportedOptions],
  totalTeamsAllowed: Range,
  playerPerTeamAllowed: Range) {

  def isSupported(option: SupportedOptions) = supportedOptions.contains(option)

  /**
   * Calculates new ratings based on the prior ratings and team ranks.
   *
   * @param gameInfo
   *            Parameters for the game.
   * @param teams
   *            A mapping of team players and their ratings.
   * @param teamRanks
   *            The ranks of the teams where 1 is first place. For a tie,
   *            repeat the number (e.g. 1, 2, 2)
   * @s All the players and their new ratings.
   */
  def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[Map[Player, Rating]], teamRanks: Seq[Int]): Map[Player, Rating]

  /**
   * Calculates the match quality as the likelihood of all teams drawing.
   *
   * @param gameInfo
   *            Parameters for the game.
   * @param teams
   *            A mapping of team players and their ratings.
   * @s The quality of the match between the teams as a percentage (0% =
   *          bad, 100% = well matched).
   */
  def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[Map[Player, Rating]]): Double

  protected def validateTeamCountAndPlayersCountPerTeam(
    teams: Seq[Map[Player, Rating]]) {
    validateTeamCountAndPlayersCountPerTeam(teams, totalTeamsAllowed, playerPerTeamAllowed)
  }

  private def validateTeamCountAndPlayersCountPerTeam(
    teams: Seq[Map[Player, Rating]], totalTeams: Range, playersPerTeam: Range) {
    for (currentTeam <- teams) {
      require(playersPerTeam contains currentTeam.size)
    }
    require(totalTeams contains teams.size)
  }
}

sealed trait SupportedOptions

object SupportedOptions {
  case object PartialPlay extends SupportedOptions
  case object PartialUpdate extends SupportedOptions
}