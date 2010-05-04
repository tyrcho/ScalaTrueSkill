package jskills.trueskill;

import org.testng.annotations.Test;

public class TwoTeamTrueSkillCalculatorTest
{
    @Test
    public void TwoTeamTrueSkillCalculatorTests()
    {
        TwoTeamTrueSkillCalculator calculator = new TwoTeamTrueSkillCalculator();

        // This calculator supports up to two teams with many players each
        TrueSkillCalculatorTests.TestAllTwoPlayerScenarios(calculator);
        TrueSkillCalculatorTests.TestAllTwoTeamScenarios(calculator);
    }
}