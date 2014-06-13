package jskills

/**
 * Represents a player who has a {@link Rating}.
 */

case class Player(
  /**
   * The identifier for the player, such as a name.
   */
  id: Any,
  /**
   * Indicates the percent of the time the player should be weighted where 0.0
   * indicates the player didn't play and 1.0 indicates the player played 100%
   * of the time.
   */
  partialPlayPercentage: Double = Player.DefaultPartialPlayPercentage,
  /**
   * Indicated how much of a skill update a player should receive where 0.0
   * represents no update and 1.0 represents 100% of the update.
   */
  partialUpdatePercentage: Double = Player.DefaultPartialUpdatePercentage)
  extends SupportPartialPlay with SupportPartialUpdate

object Player {
  /** = 100% play time **/
  val DefaultPartialPlayPercentage = 1
  /** = receive 100% update **/
  val DefaultPartialUpdatePercentage = 1
}