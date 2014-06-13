package jskills.trueskill

import org.junit.Before
import org.junit.Test
import jskills.Rating
import jskills.GameInfo
import jskills.PairwiseComparison

class TwoPlayerTrueSkillCalculatorTest extends TrueSkillCalculatorTestUtils {
  import TrueSkillCalculatorTestUtils._

  var calculator: TwoPlayerTrueSkillCalculator = null

  @Before def setup() {
    calculator = new TwoPlayerTrueSkillCalculator()
  }

  @Test def testAllTwoPlayerScenarios() {
    // We only support two players
    TestAllTwoPlayerScenarios(calculator)
  }

  @Test def specificTwoPlayerScenario() {
    val gameInfo = new GameInfo(1200.0, 1200.0 / 3.0, 200.0, 1200.0 / 300.0, 0.03)

    val (player1NewRating, player2NewRating) = calculator.calculateNewRatings(
      gameInfo,
      new Rating(1301.0007, 42.9232),
      new Rating(1188.7560, 42.5570),
      PairwiseComparison.WIN)

    assertRating(1304.7820836053318, 42.843513887848658, player1NewRating)

    assertRating(1185.0383099003536, 42.485604606897752, player2NewRating)
  }
}