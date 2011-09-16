package jskills.elo

import jskills.GameInfo

/**
 * Including ELO's scheme as a simple comparison. See
 * http://en.wikipedia.org/wiki/Elo_rating_system#Theory for more details
 */
class FideEloCalculator(kFactor: FideKFactor) extends TwoPlayerEloCalculator(kFactor) {
  //for java calls
  def this() = this(new FideKFactor())

  override def getPlayerWinProbability(gameInfo: GameInfo, playerRating: Double, opponentRating: Double): Double = {
    val ratingDifference = opponentRating - playerRating
    return 1.0 / (1.0 + Math.pow(10.0, ratingDifference / (2 * gameInfo.beta)))
  }
}