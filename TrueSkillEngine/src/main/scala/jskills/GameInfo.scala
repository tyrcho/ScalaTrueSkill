package jskills

import jskills.trueskill.DrawMargin

/**
 * TrueSkill Default Values
 * As mentioned on page 8 of the TrueSkill paper (http://research.microsoft.com/apps/pubs/default.aspx?id=67956.), the initial values for a player are:
 * mean = 25
 * sigma = 25/3 (8.33)
 * beta = sigma/2  (4.166)
 * tau (dynamics factor) = sigma/100 = 0.08
 * This leads to reasonable dynamics, but you might need to adjust as needed
 */
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
 *
 * TrueSkill co-inventor Ralf Herbrich gives a good definition of β as defining the length of the “skill
 * chain.” If a game has a wide range of skills, then β will tell you how wide each link is in the skill chain.
 * This can also be thought of how wide (in terms of skill points) each skill class.
 * Similiarly, β tells us the number of skill points a person must have above someone else to identify an
 * 80% probability of win against that person.
 *
 * For example, if β is 4 then a player Alice with a skill of “30” will tend to win against Bob who has a skill of
 * “26” approximately 80% of the time.
 */
case class GameInfo(
  initialMean: Double,
  initialStandardDeviation: Double,
  beta: Double,
  dynamicsFactor: Double,
  drawProbability: Double) {

  def getDefaultRating: Rating =
    new Rating(initialMean, initialStandardDeviation)

  def drawMargin = DrawMargin.getDrawMarginFromDrawProbability(drawProbability, beta)
}