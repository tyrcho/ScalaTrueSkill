package jskills.trueskill;

import org.testng.annotations.Test;

public class FactorGraphTrueSkillCalculatorTests {

    @Test
    public void FullFactorGraphCalculatorTests() {
        FactorGraphTrueSkillCalculator calculator = new FactorGraphTrueSkillCalculator();

        // We can test all classes 
        TrueSkillCalculatorTests.TestAllTwoPlayerScenarios(calculator);
        TrueSkillCalculatorTests.TestAllTwoTeamScenarios(calculator);
        TrueSkillCalculatorTests.TestAllMultipleTeamScenarios(calculator);

        TrueSkillCalculatorTests.TestPartialPlayScenarios(calculator);
    }
}