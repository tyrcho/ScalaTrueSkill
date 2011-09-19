package jskills.trueskill

import org.junit.Assert.assertEquals

import org.junit.Test

class DrawMarginTest {

  val ErrorTolerance = .000001

  @Test def getDrawMarginFromDrawProbabilityTest() {
    val beta = 25.0 / 6.0
    // The expected values were compared against Ralf Herbrich's implementation in F#
    AssertDrawMargin(0.10, beta, 0.74046637542690541)
    AssertDrawMargin(0.25, beta, 1.87760059883033)
    AssertDrawMargin(0.33, beta, 2.5111010132487492)
  }

  def AssertDrawMargin(drawProbability: Double, beta: Double, expected: Double) {
    val actual = DrawMargin.getDrawMarginFromDrawProbability(drawProbability, beta)
    assertEquals(expected, actual, ErrorTolerance)
  }
}