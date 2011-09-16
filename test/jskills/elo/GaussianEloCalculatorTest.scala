package jskills.elo

import jskills.PairwiseComparison
import jskills.elo.EloAssert._
import org.junit.Test

class GaussianEloCalculatorTest {
  @Test
  def gaussianEloCalculatorTests() {
    val defaultKFactor = 24.0
    val calc = new GaussianEloCalculator()

    assertChessRating(calc, 1200, 1200, PairwiseComparison.WIN, 1212, 1188)
    assertChessRating(calc, 1200, 1200, PairwiseComparison.DRAW, 1200, 1200)
    assertChessRating(calc, 1200, 1200, PairwiseComparison.LOSE, 1188, 1212)

    // verified using TrueSkill paper equation
    assertChessRating(calc, 1200, 1000, PairwiseComparison.WIN, 1200 + ((1 - 0.76024993890652326884) * defaultKFactor), 1000 - (1 - 0.76024993890652326884) * defaultKFactor)
    assertChessRating(calc, 1200, 1000, PairwiseComparison.DRAW, 1200 - (0.76024993890652326884 - 0.5) * defaultKFactor, 1000 + (0.76024993890652326884 - 0.5) * defaultKFactor)
    assertChessRating(calc, 1200, 1000, PairwiseComparison.LOSE, 1200 - 0.76024993890652326884 * defaultKFactor, 1000 + 0.76024993890652326884 * defaultKFactor)
  }
}