package jskills

object PartialPlay {
  val smallestPercentage = 0.0001

  // If the player doesn't support the interface, assume 1.0 == 100%
  def getPartialPlayPercentage(player: Any): Double = {
    player match {
      case partial: SupportPartialPlay => {
        var percentage = partial.partialPlayPercentage
        // HACK to get around bug near 0
        if (percentage < smallestPercentage) smallestPercentage else percentage
      }
      case _ => 1
    }
  }
}