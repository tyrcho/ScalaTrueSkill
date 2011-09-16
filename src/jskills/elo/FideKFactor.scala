package jskills.elo

/** @see http://ratings.fide.com/calculator_rtd.phtml for details **/
class FideKFactor(value: Double = -1) extends KFactor(value) {
  override def getValueForRating(rating: Double): Double = if (rating < 2400) 15 else 10
}

object FideKFactor {
  /** Indicates someone who has played less than 30 games. **/
  class Provisional extends FideKFactor {
    override def getValueForRating(rating: Double) = 25
  }
}