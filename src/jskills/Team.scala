package jskills

import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.List

/**
 * Helper class for working with a single team.
 */
class Team extends HashMap[IPlayer, Rating] with ITeam {
  /**
   * Constructs a Team and populates it with the specified player.
   *
   * @param player
   *            The player to add.
   * @param rating
   *            The rating of the player.
   */
  def this(player: IPlayer, rating: Rating) {
    this()
    addPlayer(player, rating)
  }

  /**
   * Adds the player to the team.
   *
   * @param player
   *            The player to add.
   * @param rating
   *            The rating of the player
   * @returns The instance of the team (for chaining convenience).
   */
  def addPlayer(player: IPlayer, rating: Rating): Team = {
    put(player, rating)
    return this
  }
}