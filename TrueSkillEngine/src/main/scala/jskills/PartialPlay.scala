package jskills

object PartialPlay {
  val smallestPercentage = 0.0001

  // If the player doesn't support the interface, assume 1.0 == 100%
  def getPartialPlayPercentage(player: Player): Double =
    player match {
      case partial: SupportPartialPlay =>
        // HACK to get around bug near 0
        math.max(partial.partialPlayPercentage, smallestPercentage)
      case _ => 1
    }

}