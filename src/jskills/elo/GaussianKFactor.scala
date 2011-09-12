package jskills.elo;

import jskills.GameInfo;

class GaussianKFactor(value: Double = GaussianKFactor.StableDynamicsKFactor) extends KFactor(value) {
  def this(gameInfo: GameInfo, latestGameWeightingFactor: Double) {
    this(latestGameWeightingFactor * gameInfo.getBeta() * Math.sqrt(Math.Pi))
  }
}
object GaussianKFactor {
  // From paper
  val StableDynamicsKFactor = 24.0;
}