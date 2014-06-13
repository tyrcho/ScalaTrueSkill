package jskills.elo

import jskills.GameInfo

class GaussianKFactor(value: Double = GaussianKFactor.StableDynamicsKFactor) extends KFactor(value)

object GaussianKFactor {
  def apply(gameInfo: GameInfo, latestGameWeightingFactor: Double) =
    new GaussianKFactor(latestGameWeightingFactor * gameInfo.beta * Math.sqrt(Math.PI))

  // From paper
  val StableDynamicsKFactor = 24.0
}