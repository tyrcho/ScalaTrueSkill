package jskills

import collection.mutable.HashMap

/**
 * Helper class for working with a single team.
 */
class Team extends HashMap[Player, Rating] with ITeam {
  /**
   * Adds the player to the team.
   *
   * @param player
   *            The player to add.
   * @param rating
   *            The rating of the player
   * @s The instance of the team (for chaining convenience).
   */
  def addPlayer(player: Player, rating: Rating): Team = {
    put(player, rating)
    this
  }
}

object Team {
  /**
   * Constructs a Team and populates it with the specified player.
   *
   * @param player
   *            The player to add.
   * @param rating
   *            The rating of the player.
   */
  def apply(player: Player, rating: Rating) = {
    new Team().addPlayer(player, rating)
  }

  def apply(players: Iterable[(Player, Rating)]) = {
    val t = new Team()
    t ++= players
    t
  }
}