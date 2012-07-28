package jskills

object GameInfo {
  val defaultInitialMean = 25.0
  val defaultBeta = defaultInitialMean / 6.0
  val defaultDrawProbability = 0.10
  val defaultDynamicsFactor = defaultInitialMean / 300.0
  val defaultInitialStandardDeviation = defaultInitialMean / 3.0

  val defaultGameInfo = new GameInfo(defaultInitialMean,
    defaultInitialStandardDeviation,
    defaultBeta,
    defaultDynamicsFactor,
    defaultDrawProbability)
}
/**
 * Parameters about the game for calculating the TrueSkill.
 */
class GameInfo(
  val initialMean: Double,
  val initialStandardDeviation: Double,
  val beta: Double,
  val dynamicsFactor: Double,
  val drawProbability: Double) {

  def getDefaultRating(): Rating =
    new Rating(initialMean, initialStandardDeviation)
}