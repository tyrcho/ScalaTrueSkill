package jskills.trueskill

import org.junit.Before
import org.junit.Test

class TwoPlayerTrueSkillCalculatorTest extends TrueSkillCalculatorTestUtils {

  var calculator: TwoPlayerTrueSkillCalculator = null

  @Before def setup() {
    calculator = new TwoPlayerTrueSkillCalculator()
  }

  @Test def testAllTwoPlayerScenarios() {
    // We only support two players
    TestAllTwoPlayerScenarios(calculator)
  }

  // TODO: Assert failures for larger teams
}