package jskills.trueskill

import org.junit.Before
import org.junit.Test

class TwoTeamTrueSkillCalculatorTest extends TrueSkillCalculatorTestUtils {

  var calculator: TwoTeamTrueSkillCalculator = null

  @Before def setup() {
    calculator = new TwoTeamTrueSkillCalculator()
  }

  @Test def testAllTwoPlayerScenarios() {
    // This calculator supports up to two teams with many players each
    TestAllTwoPlayerScenarios(calculator)
  }

  @Test def testAllTwoTeamScenarios() {
    // This calculator supports up to two teams with many players each
    TestAllTwoTeamScenarios(calculator)
  }
}