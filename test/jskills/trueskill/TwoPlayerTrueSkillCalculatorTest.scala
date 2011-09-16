package jskills.trueskill;

import org.junit.Before;
import org.junit.Test;

class TwoPlayerTrueSkillCalculatorTest {

  var calculator: TwoPlayerTrueSkillCalculator = null

  @Before def setup() {
    calculator = new TwoPlayerTrueSkillCalculator();
  }

  @Test def TestAllTwoPlayerScenarios() {
    // We only support two players
    TrueSkillCalculatorTests.TestAllTwoPlayerScenarios(calculator);
  }

  // TODO: Assert failures for larger teams
}