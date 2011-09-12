package jskills.elo;

import jskills.GameInfo;
import jskills.numerics.GaussianDistribution;

class GaussianEloCalculator(factor: KFactor = GaussianEloCalculator.StableKFactor)
  extends TwoPlayerEloCalculator(factor) {
  //for java
  def this() { this(GaussianEloCalculator.StableKFactor) }
  override def getPlayerWinProbability(gameInfo: GameInfo, playerRating: Double, opponentRating: Double): Double = {
    val ratingDifference = playerRating - opponentRating;
    // See equation 1.1 in the TrueSkill paper
    return GaussianDistribution.cumulativeTo(ratingDifference / (Math.sqrt(2) * gameInfo.getBeta()));
  }
}

object GaussianEloCalculator {
  // From the paper
  val StableKFactor = new KFactor(24)
}