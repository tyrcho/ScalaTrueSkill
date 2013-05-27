package jskills.elo

import jskills.GameInfo
import scala.math._

class GaussianKFactor(value: Double = GaussianKFactor.StableDynamicsKFactor) extends KFactor(value)

object GaussianKFactor {
  def apply(gameInfo: GameInfo, latestGameWeightingFactor: Double) =
    new GaussianKFactor(latestGameWeightingFactor * gameInfo.beta * sqrt(Pi))

  // From paper
  val StableDynamicsKFactor = 24.0
}