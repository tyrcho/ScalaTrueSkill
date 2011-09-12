package jskills;

object PartialPlay {

  def getPartialPlayPercentage(player: Object): Double = {
    // If the player doesn't support the interface, assume 1.0 == 100%
    if (player.isInstanceOf[ISupportPartialPlay]) {
      var partialPlayPercentage = (player.asInstanceOf[ISupportPartialPlay]).getPartialPlayPercentage();
      // HACK to get around bug near 0
      val smallestPercentage = 0.0001;
      if (partialPlayPercentage < smallestPercentage) {
        partialPlayPercentage = smallestPercentage;
      }
      return partialPlayPercentage;
    }
    return 1.0;
  }
}