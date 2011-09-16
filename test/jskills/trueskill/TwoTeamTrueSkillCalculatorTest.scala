package jskills.trueskill;

import org.junit.Before;
import org.junit.Test;

class TwoTeamTrueSkillCalculatorTest {

  var calculator: TwoTeamTrueSkillCalculator = null

  @Before def setup() {
    calculator = new TwoTeamTrueSkillCalculator();
  }

  @Test def TestAllTwoPlayerScenarios() {
    // This calculator supports up to two teams with many players each
    TrueSkillCalculatorTests.TestAllTwoPlayerScenarios(calculator);
  }

  @Test def TestAllTwoTeamScenarios() {
    // This calculator supports up to two teams with many players each
    TrueSkillCalculatorTests.TestAllTwoTeamScenarios(calculator);
  }
}