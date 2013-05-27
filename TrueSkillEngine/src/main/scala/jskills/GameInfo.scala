package jskills

object GameInfo {
  val defaultInitialMean = 25.0

  val defaultGameInfo = new GameInfo(
    initialMean = defaultInitialMean,
    initialStandardDeviation = defaultInitialMean / 3.0,
    beta = defaultInitialMean / 6.0,
    dynamicsFactor = defaultInitialMean / 300.0,
    drawProbability = 0.10)
}
/**
 * Parameters about the game for calculating the TrueSkill.
 */
case class GameInfo(
  initialMean: Double,
  initialStandardDeviation: Double,
  beta: Double,
  dynamicsFactor: Double,
  drawProbability: Double) {

  def getDefaultRating: Rating =
    new Rating(initialMean, initialStandardDeviation)
}