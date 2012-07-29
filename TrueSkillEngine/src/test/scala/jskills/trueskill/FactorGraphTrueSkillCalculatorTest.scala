package jskills.trueskill

import org.junit.Before
import org.junit.Test

class FactorGraphTrueSkillCalculatorTest extends TrueSkillCalculatorTestUtils {
  var calculator: FactorGraphTrueSkillCalculator = null

  @Before def setup() {
    calculator = new FactorGraphTrueSkillCalculator()
  }

  @Test def testAllTwoTeamScenarios() {
    TestAllTwoTeamScenarios(calculator)
  }

  @Test def testAllTwoPlayerScenarios() {
    TestAllTwoPlayerScenarios(calculator)
  }

  @Test def testAllMultipleTeamScenarios() {
    TestAllMultipleTeamScenarios(calculator)
  }

  @Test def testPartialPlayScenarios() {
    TestPartialPlayScenarios(calculator)
  }
}