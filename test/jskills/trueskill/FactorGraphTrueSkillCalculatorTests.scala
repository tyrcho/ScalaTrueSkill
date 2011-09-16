package jskills.trueskill

import org.junit.Before
import org.junit.Test

class FactorGraphTrueSkillCalculatorTests {
  var calculator: FactorGraphTrueSkillCalculator = null

  @Before def setup() {
    calculator = new FactorGraphTrueSkillCalculator()
  }

  @Test def TestAllTwoTeamScenarios() {
    TrueSkillCalculatorTests.TestAllTwoTeamScenarios(calculator)
  }

  @Test def TestAllTwoPlayerScenarios() {
    TrueSkillCalculatorTests.TestAllTwoPlayerScenarios(calculator)
  }

  @Test def TestAllMultipleTeamScenarios() {
    TrueSkillCalculatorTests.TestAllMultipleTeamScenarios(calculator)
  }

  @Test def TestPartialPlayScenarios() {
    TrueSkillCalculatorTests.TestPartialPlayScenarios(calculator)
  }
}