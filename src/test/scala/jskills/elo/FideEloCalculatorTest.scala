package jskills.elo

import jskills.PairwiseComparison

import org.junit.Test
import jskills.elo.EloAssert._

class FideEloCalculatorTest {

  @Test
  def fideProvisionalEloCalculatorTests() {
    // verified against http://ratings.fide.com/calculator_rtd.phtml
    val calc = new FideEloCalculator(new FideKFactor.Provisional())

    assertChessRating(calc, 1200, 1500, PairwiseComparison.WIN, 1221.25, 1478.75)
    assertChessRating(calc, 1200, 1500, PairwiseComparison.DRAW, 1208.75, 1491.25)
    assertChessRating(calc, 1200, 1500, PairwiseComparison.LOSE, 1196.25, 1503.75)
  }

  @Test
  def fideNonProvisionalEloCalculatorTests() {
    // verified against http://ratings.fide.com/calculator_rtd.phtml
    val calc = new FideEloCalculator()

    assertChessRating(calc, 1200, 1200, PairwiseComparison.WIN, 1207.5, 1192.5)
    assertChessRating(calc, 1200, 1200, PairwiseComparison.DRAW, 1200, 1200)
    assertChessRating(calc, 1200, 1200, PairwiseComparison.LOSE, 1192.5, 1207.5)

    assertChessRating(calc, 2600, 2500, PairwiseComparison.WIN, 2603.6, 2496.4)
    assertChessRating(calc, 2600, 2500, PairwiseComparison.DRAW, 2598.6, 2501.4)
    assertChessRating(calc, 2600, 2500, PairwiseComparison.LOSE, 2593.6, 2506.4)
  }
}