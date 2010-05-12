package jskills.trueskill;

import org.testng.annotations.Test;

public class TwoPlayerTrueSkillCalculatorTest {
    @Test
    public void TwoPlayerTrueSkillCalculatorTests() {
        TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

        // We only support two players
        TrueSkillCalculatorTests.TestAllTwoPlayerScenarios(calculator);

        // TODO: Assert failures for larger teams
    }    
}