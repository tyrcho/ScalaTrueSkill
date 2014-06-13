package jskills.predict

import jskills.numerics.GaussianDistribution
import jskills.GameInfo

object Prediction {
  def predict(teamDifference: GaussianDistribution, gameInfo: GameInfo) = {
    val margin = gameInfo.drawMargin
    val win = teamDifference.cumulativeTo(-margin)
    val loss = 1 - teamDifference.cumulativeTo(margin)
    val draw = 1 - win - loss
    val p = Prediction(win, loss, draw)
    println(s"prediction for $teamDifference and $margin : $p")
    p
  }
}

case class Prediction(homeWinsP: Double, awayWinsP: Double, drawsP: Double) {
  def +(other: Prediction) = Prediction(homeWinsP + other.homeWinsP, awayWinsP + other.awayWinsP, drawsP + other.drawsP)
  def /(ratio: Double) = Prediction(homeWinsP / ratio, awayWinsP / ratio, drawsP / ratio)
  override def toString = f"home=$homeWinsP%.4f	away=$awayWinsP%.4f	draw=$drawsP%.4f"
}