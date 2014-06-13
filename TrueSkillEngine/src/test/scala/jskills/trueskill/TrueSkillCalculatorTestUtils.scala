package jskills.trueskill

import org.junit.Assert._

import jskills.GameInfo
import jskills.Player
import jskills.ITeam
import jskills.Player
import jskills.Rating
import jskills.SkillCalculator

trait TrueSkillCalculatorTestUtils {
  import TrueSkillCalculatorTestUtils._

  // These are the roll-up ones

  def TestAllTwoPlayerScenarios(calculator: SkillCalculator) {
    TwoPlayerTestNotDrawn(calculator)
    TwoPlayerTestDrawn(calculator)
    OneOnOneMassiveUpsetDrawTest(calculator)

    TwoPlayerChessTestNotDrawn(calculator)
  }

  def TestAllTwoTeamScenarios(calculator: SkillCalculator) {
    OneOnTwoSimpleTest(calculator)
    OneOnTwoDrawTest(calculator)
    OneOnTwoSomewhatBalanced(calculator)
    OneOnThreeDrawTest(calculator)
    OneOnThreeSimpleTest(calculator)
    OneOnSevenSimpleTest(calculator)

    TwoOnTwoSimpleTest(calculator)
    TwoOnTwoUnbalancedDrawTest(calculator)
    TwoOnTwoDrawTest(calculator)
    TwoOnTwoUpsetTest(calculator)

    ThreeOnTwoTests(calculator)

    FourOnFourSimpleTest(calculator)
  }

  def TestAllMultipleTeamScenarios(calculator: SkillCalculator) {
    ThreeTeamsOfOneNotDrawn(calculator)
    ThreeTeamsOfOneDrawn(calculator)
    FourTeamsOfOneNotDrawn(calculator)
    FiveTeamsOfOneNotDrawn(calculator)
    EightTeamsOfOneDrawn(calculator)
    EightTeamsOfOneUpset(calculator)
    SixteenTeamsOfOneNotDrawn(calculator)

    TwoOnFourOnTwoWinDraw(calculator)
  }

  def TestPartialPlayScenarios(calculator: SkillCalculator) {
    OneOnTwoBalancedPartialPlay(calculator)
  }

  //------------------- Actual Tests ---------------------------
  // If you see more than 3 digits of precision in the decimal point, then the 
  // expected values calculated from F# RalfH's implementation with the same 
  // input. It didn't support teams, so team values all came from the online 
  // calculator at 
  // http://atom.research.microsoft.com/trueskill/rankcalculator.aspx
  //
  // All match quality expected values came from the online calculator

  // In both cases, there may be some discrepancy after the first decimal point. 
  // I think this is due to my implementation using slightly higher precision in 
  // GaussianDistribution.

  //------------------------------------------------------------------------------
  // Two Player Tests
  //------------------------------------------------------------------------------

  private def TwoPlayerTestNotDrawn(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val teams = Seq(team1, team2)

    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    val player1NewRating = newRatings(player1)
    assertRating(29.39583201999924, 7.171475587326186, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(20.60416798000076, 7.171475587326186, player2NewRating)

    assertMatchQuality(0.447, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def TwoPlayerTestDrawn(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val teams = Seq(team1, team2)

    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1))

    val player1NewRating = newRatings(player1)
    assertRating(25.0, 6.4575196623173081, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(25.0, 6.4575196623173081, player2NewRating)

    assertMatchQuality(0.447, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def TwoPlayerChessTestNotDrawn(calculator: SkillCalculator) {
    // Inspired by a real bug :-)
    val player1 = new Player(1)
    val player2 = new Player(2)
    val gameInfo = new GameInfo(1200.0, 1200.0 / 3.0, 200.0, 1200.0 / 300.0, 0.03)

    val team1 = Team(player1, new Rating(1301.0007, 42.9232))
    val team2 = Team(player2, new Rating(1188.7560, 42.5570))

    val newRatings = calculator.calculateNewRatings(gameInfo, Seq(team1, team2), Seq(1, 2))

    val player1NewRating = newRatings(player1)
    assertRating(1304.7820836053318, 42.843513887848658, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(1185.0383099003536, 42.485604606897752, player2NewRating)
  }

  private def OneOnOneMassiveUpsetDrawTest(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)

    val player2 = new Player(2)

    val team2 = Team(player2, new Rating(50, 12.5))

    val teams = Seq(team1, team2)

    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1))

    // Winners
    assertRating(31.662, 7.137, newRatingsWinLose(player1))

    // Losers
    assertRating(35.010, 7.910, newRatingsWinLose(player2))

    assertMatchQuality(0.110, calculator.calculateMatchQuality(gameInfo, teams))
  }

  //------------------------------------------------------------------------------
  // Two Team Tests
  //------------------------------------------------------------------------------

  private def TwoOnTwoSimpleTest(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating,
      player2 -> gameInfo.getDefaultRating)

    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = Map(
      player3 -> gameInfo.getDefaultRating,
      player4 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(28.108, 7.774, newRatingsWinLose(player1))
    assertRating(28.108, 7.774, newRatingsWinLose(player2))

    // Losers
    assertRating(21.892, 7.774, newRatingsWinLose(player3))
    assertRating(21.892, 7.774, newRatingsWinLose(player4))

    assertMatchQuality(0.447, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def TwoOnTwoDrawTest(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating,
      player2 -> gameInfo.getDefaultRating)

    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = Map(
      player3 -> gameInfo.getDefaultRating,
      player4 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1))

    // Winners
    assertRating(25, 7.455, newRatingsWinLose(player1))
    assertRating(25, 7.455, newRatingsWinLose(player2))

    // Losers
    assertRating(25, 7.455, newRatingsWinLose(player3))
    assertRating(25, 7.455, newRatingsWinLose(player4))

    assertMatchQuality(0.447, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def TwoOnTwoUnbalancedDrawTest(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> new Rating(15, 8),
      player2 -> new Rating(20, 6))

    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = Map(
      player3 -> new Rating(25, 4),
      player4 -> new Rating(30, 3))

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1))

    // Winners
    assertRating(21.570, 6.556, newRatingsWinLose(player1))
    assertRating(23.696, 5.418, newRatingsWinLose(player2))

    // Losers
    assertRating(23.357, 3.833, newRatingsWinLose(player3))
    assertRating(29.075, 2.931, newRatingsWinLose(player4))

    assertMatchQuality(0.214, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def TwoOnTwoUpsetTest(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> new Rating(20, 8),
      player2 -> new Rating(25, 6))

    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = Map(
      player3 -> new Rating(35, 7),
      player4 -> new Rating(40, 5))

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(29.698, 7.008, newRatingsWinLose(player1))
    assertRating(30.455, 5.594, newRatingsWinLose(player2))

    // Losers
    assertRating(27.575, 6.346, newRatingsWinLose(player3))
    assertRating(36.211, 4.768, newRatingsWinLose(player4))

    assertMatchQuality(0.084, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def FourOnFourSimpleTest(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating,
      player2 -> gameInfo.getDefaultRating,
      player3 -> gameInfo.getDefaultRating,
      player4 -> gameInfo.getDefaultRating)

    val player5 = new Player(5)
    val player6 = new Player(6)
    val player7 = new Player(7)
    val player8 = new Player(8)

    val team2 = Map(
      player5 -> gameInfo.getDefaultRating,
      player6 -> gameInfo.getDefaultRating,
      player7 -> gameInfo.getDefaultRating,
      player8 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)

    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(27.198, 8.059, newRatingsWinLose(player1))
    assertRating(27.198, 8.059, newRatingsWinLose(player2))
    assertRating(27.198, 8.059, newRatingsWinLose(player3))
    assertRating(27.198, 8.059, newRatingsWinLose(player4))

    // Losers
    assertRating(22.802, 8.059, newRatingsWinLose(player5))
    assertRating(22.802, 8.059, newRatingsWinLose(player6))
    assertRating(22.802, 8.059, newRatingsWinLose(player7))
    assertRating(22.802, 8.059, newRatingsWinLose(player8))

    assertMatchQuality(0.447, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def OneOnTwoSimpleTest(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating)

    val player2 = new Player(2)
    val player3 = new Player(3)

    val team2 = Map(
      player2 -> gameInfo.getDefaultRating,
      player3 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(33.730, 7.317, newRatingsWinLose(player1))

    // Losers
    assertRating(16.270, 7.317, newRatingsWinLose(player2))
    assertRating(16.270, 7.317, newRatingsWinLose(player3))

    assertMatchQuality(0.135, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def OneOnTwoSomewhatBalanced(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> new Rating(40, 6))

    val player2 = new Player(2)
    val player3 = new Player(3)

    val team2 = Map(
      player2 -> new Rating(20, 7),
      player3 -> new Rating(25, 8))

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(42.744, 5.602, newRatingsWinLose(player1))

    // Losers
    assertRating(16.266, 6.359, newRatingsWinLose(player2))
    assertRating(20.123, 7.028, newRatingsWinLose(player3))

    assertMatchQuality(0.478, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def OneOnThreeSimpleTest(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating)

    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = Map(
      player2 -> gameInfo.getDefaultRating,
      player3 -> gameInfo.getDefaultRating,
      player4 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(36.337, 7.527, newRatingsWinLose(player1))

    // Losers
    assertRating(13.663, 7.527, newRatingsWinLose(player2))
    assertRating(13.663, 7.527, newRatingsWinLose(player3))
    assertRating(13.663, 7.527, newRatingsWinLose(player4))

    assertMatchQuality(0.012, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def OneOnTwoDrawTest(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating)

    val player2 = new Player(2)
    val player3 = new Player(3)

    val team2 = Map(
      player2 -> gameInfo.getDefaultRating,
      player3 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1))

    // Winners
    assertRating(31.660, 7.138, newRatingsWinLose(player1))

    // Losers
    assertRating(18.340, 7.138, newRatingsWinLose(player2))
    assertRating(18.340, 7.138, newRatingsWinLose(player3))

    assertMatchQuality(0.135, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def OneOnThreeDrawTest(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating)

    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)

    val team2 = Map(
      player2 -> gameInfo.getDefaultRating,
      player3 -> gameInfo.getDefaultRating,
      player4 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1))

    // Winners
    assertRating(34.990, 7.455, newRatingsWinLose(player1))

    // Losers
    assertRating(15.010, 7.455, newRatingsWinLose(player2))
    assertRating(15.010, 7.455, newRatingsWinLose(player3))
    assertRating(15.010, 7.455, newRatingsWinLose(player4))

    assertMatchQuality(0.012, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def OneOnSevenSimpleTest(calculator: SkillCalculator) {
    val player1 = new Player(1)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> gameInfo.getDefaultRating)

    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)
    val player5 = new Player(5)
    val player6 = new Player(6)
    val player7 = new Player(7)
    val player8 = new Player(8)

    val team2 = Map(
      player2 -> gameInfo.getDefaultRating,
      player3 -> gameInfo.getDefaultRating,
      player4 -> gameInfo.getDefaultRating,
      player5 -> gameInfo.getDefaultRating,
      player6 -> gameInfo.getDefaultRating,
      player7 -> gameInfo.getDefaultRating,
      player8 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(40.582, 7.917, newRatingsWinLose(player1))

    // Losers
    assertRating(9.418, 7.917, newRatingsWinLose(player2))
    assertRating(9.418, 7.917, newRatingsWinLose(player3))
    assertRating(9.418, 7.917, newRatingsWinLose(player4))
    assertRating(9.418, 7.917, newRatingsWinLose(player5))
    assertRating(9.418, 7.917, newRatingsWinLose(player6))
    assertRating(9.418, 7.917, newRatingsWinLose(player7))
    assertRating(9.418, 7.917, newRatingsWinLose(player8))

    assertMatchQuality(0.000, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def ThreeOnTwoTests(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)

    val team1 = Map(
      player1 -> new Rating(28, 7),
      player2 -> new Rating(27, 6),
      player3 -> new Rating(26, 5))

    val player4 = new Player(4)
    val player5 = new Player(5)

    val team2 = Map(
      player4 -> new Rating(30, 4),
      player5 -> new Rating(31, 3))

    val gameInfo = GameInfo.defaultGameInfo

    val teams = Seq(team1, team2)
    val newRatingsWinLoseExpected = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))

    // Winners
    assertRating(28.658, 6.770, newRatingsWinLoseExpected(player1))
    assertRating(27.484, 5.856, newRatingsWinLoseExpected(player2))
    assertRating(26.336, 4.917, newRatingsWinLoseExpected(player3))

    // Losers
    assertRating(29.785, 3.958, newRatingsWinLoseExpected(player4))
    assertRating(30.879, 2.983, newRatingsWinLoseExpected(player5))

    val newRatingsWinLoseUpset = calculator
      .calculateNewRatings(gameInfo, Seq(team1, team2), Seq(2, 1))

    // Winners
    assertRating(32.012, 3.877, newRatingsWinLoseUpset(player4))
    assertRating(32.132, 2.949, newRatingsWinLoseUpset(player5))

    // Losers
    assertRating(21.840, 6.314, newRatingsWinLoseUpset(player1))
    assertRating(22.474, 5.575, newRatingsWinLoseUpset(player2))
    assertRating(22.857, 4.757, newRatingsWinLoseUpset(player3))

    assertMatchQuality(0.254, calculator.calculateMatchQuality(gameInfo, teams))
  }

  // ------------------------------------------------------------------------------
  // Multiple Teams Tests
  //------------------------------------------------------------------------------

  private def TwoOnFourOnTwoWinDraw(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)

    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Map(
      player1 -> new Rating(40, 4),
      player2 -> new Rating(45, 3))

    val player3 = new Player(3)
    val player4 = new Player(4)
    val player5 = new Player(5)
    val player6 = new Player(6)

    val team2 = Map(
      player3 -> new Rating(20, 7),
      player4 -> new Rating(19, 6),
      player5 -> new Rating(30, 9),
      player6 -> new Rating(10, 4))

    val player7 = new Player(7)
    val player8 = new Player(8)

    val team3 = Map(
      player7 -> new Rating(50, 5),
      player8 -> new Rating(30, 2))

    val teams = Seq(team1, team2, team3)
    val newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2, 2))

    // Winners
    assertRating(40.877, 3.840, newRatingsWinLose(player1))
    assertRating(45.493, 2.934, newRatingsWinLose(player2))
    assertRating(19.609, 6.396, newRatingsWinLose(player3))
    assertRating(18.712, 5.625, newRatingsWinLose(player4))
    assertRating(29.353, 7.673, newRatingsWinLose(player5))
    assertRating(9.872, 3.891, newRatingsWinLose(player6))
    assertRating(48.830, 4.590, newRatingsWinLose(player7))
    assertRating(29.813, 1.976, newRatingsWinLose(player8))

    assertMatchQuality(0.367, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def ThreeTeamsOfOneNotDrawn(calculator: SkillCalculator) {

    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val team3 = Team(player3, gameInfo.getDefaultRating)

    val teams = Seq(team1, team2, team3)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2, 3))

    val player1NewRating = newRatings(player1)
    assertRating(31.675352419172107, 6.6559853776206905, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(25.000000000003912, 6.2078966412243233, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(18.324647580823971, 6.6559853776218318, player3NewRating)

    assertMatchQuality(0.200, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def ThreeTeamsOfOneDrawn(calculator: SkillCalculator) {

    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val team3 = Team(player3, gameInfo.getDefaultRating)

    val teams = Seq(team1, team2, team3)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1, 1))

    val player1NewRating = newRatings(player1)
    assertRating(25.000, 5.698, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(25.000, 5.695, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(25.000, 5.698, player3NewRating)

    assertMatchQuality(0.200, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def FourTeamsOfOneNotDrawn(calculator: SkillCalculator) {

    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val team3 = Team(player3, gameInfo.getDefaultRating)
    val team4 = Team(player4, gameInfo.getDefaultRating)

    val teams = Seq(team1, team2, team3, team4)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2, 3, 4))

    val player1NewRating = newRatings(player1)
    assertRating(33.206680965631264, 6.3481091698077057, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(27.401454693843323, 5.7871629348447584, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(22.598545306188374, 5.7871629348413451, player3NewRating)

    val player4NewRating = newRatings(player4)
    assertRating(16.793319034361271, 6.3481091698144967, player4NewRating)

    assertMatchQuality(0.089, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def FiveTeamsOfOneNotDrawn(calculator: SkillCalculator) {
    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)
    val player5 = new Player(5)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val team3 = Team(player3, gameInfo.getDefaultRating)
    val team4 = Team(player4, gameInfo.getDefaultRating)
    val team5 = Team(player5, gameInfo.getDefaultRating)

    val teams = Seq(team1, team2, team3, team4,
      team5)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2, 3, 4, 5))

    val player1NewRating = newRatings(player1)
    assertRating(34.363135705841188, 6.1361528798112692, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(29.058448805636779, 5.5358352402833413, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(25.000000000031758, 5.4200805474429847, player3NewRating)

    val player4NewRating = newRatings(player4)
    assertRating(20.941551194426314, 5.5358352402709672, player4NewRating)

    val player5NewRating = newRatings(player5)
    assertRating(15.636864294158848, 6.136152879829349, player5NewRating)

    assertMatchQuality(0.040, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def EightTeamsOfOneDrawn(calculator: SkillCalculator) {

    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)
    val player5 = new Player(5)
    val player6 = new Player(6)
    val player7 = new Player(7)
    val player8 = new Player(8)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val team3 = Team(player3, gameInfo.getDefaultRating)
    val team4 = Team(player4, gameInfo.getDefaultRating)
    val team5 = Team(player5, gameInfo.getDefaultRating)
    val team6 = Team(player6, gameInfo.getDefaultRating)
    val team7 = Team(player7, gameInfo.getDefaultRating)
    val team8 = Team(player8, gameInfo.getDefaultRating)

    val teams = Seq(team1, team2, team3, team4,
      team5, team6, team7, team8)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 1, 1, 1, 1, 1, 1, 1))

    val player1NewRating = newRatings(player1)
    assertRating(25.000, 4.592, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(25.000, 4.583, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(25.000, 4.576, player3NewRating)

    val player4NewRating = newRatings(player4)
    assertRating(25.000, 4.573, player4NewRating)

    val player5NewRating = newRatings(player5)
    assertRating(25.000, 4.573, player5NewRating)

    val player6NewRating = newRatings(player6)
    assertRating(25.000, 4.576, player6NewRating)

    val player7NewRating = newRatings(player7)
    assertRating(25.000, 4.583, player7NewRating)

    val player8NewRating = newRatings(player8)
    assertRating(25.000, 4.592, player8NewRating)

    assertMatchQuality(0.004, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def EightTeamsOfOneUpset(calculator: SkillCalculator) {

    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)
    val player5 = new Player(5)
    val player6 = new Player(6)
    val player7 = new Player(7)
    val player8 = new Player(8)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, new Rating(10, 8))
    val team2 = Team(player2, new Rating(15, 7))
    val team3 = Team(player3, new Rating(20, 6))
    val team4 = Team(player4, new Rating(25, 5))
    val team5 = Team(player5, new Rating(30, 4))
    val team6 = Team(player6, new Rating(35, 3))
    val team7 = Team(player7, new Rating(40, 2))
    val team8 = Team(player8, new Rating(45, 1))

    val teams = Seq(team1, team2, team3, team4,
      team5, team6, team7, team8)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2, 3, 4, 5, 6, 7, 8))

    val player1NewRating = newRatings(player1)
    assertRating(35.135, 4.506, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(32.585, 4.037, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(31.329, 3.756, player3NewRating)

    val player4NewRating = newRatings(player4)
    assertRating(30.984, 3.453, player4NewRating)

    val player5NewRating = newRatings(player5)
    assertRating(31.751, 3.064, player5NewRating)

    val player6NewRating = newRatings(player6)
    assertRating(34.051, 2.541, player6NewRating)

    val player7NewRating = newRatings(player7)
    assertRating(38.263, 1.849, player7NewRating)

    val player8NewRating = newRatings(player8)
    assertRating(44.118, 0.983, player8NewRating)

    assertMatchQuality(0.000, calculator.calculateMatchQuality(gameInfo, teams))
  }

  private def SixteenTeamsOfOneNotDrawn(calculator: SkillCalculator) {

    val player1 = new Player(1)
    val player2 = new Player(2)
    val player3 = new Player(3)
    val player4 = new Player(4)
    val player5 = new Player(5)
    val player6 = new Player(6)
    val player7 = new Player(7)
    val player8 = new Player(8)
    val player9 = new Player(9)
    val player10 = new Player(10)
    val player11 = new Player(11)
    val player12 = new Player(12)
    val player13 = new Player(13)
    val player14 = new Player(14)
    val player15 = new Player(15)
    val player16 = new Player(16)
    val gameInfo = GameInfo.defaultGameInfo

    val team1 = Team(player1, gameInfo.getDefaultRating)
    val team2 = Team(player2, gameInfo.getDefaultRating)
    val team3 = Team(player3, gameInfo.getDefaultRating)
    val team4 = Team(player4, gameInfo.getDefaultRating)
    val team5 = Team(player5, gameInfo.getDefaultRating)
    val team6 = Team(player6, gameInfo.getDefaultRating)
    val team7 = Team(player7, gameInfo.getDefaultRating)
    val team8 = Team(player8, gameInfo.getDefaultRating)
    val team9 = Team(player9, gameInfo.getDefaultRating)
    val team10 = Team(player10, gameInfo.getDefaultRating)
    val team11 = Team(player11, gameInfo.getDefaultRating)
    val team12 = Team(player12, gameInfo.getDefaultRating)
    val team13 = Team(player13, gameInfo.getDefaultRating)
    val team14 = Team(player14, gameInfo.getDefaultRating)
    val team15 = Team(player15, gameInfo.getDefaultRating)
    val team16 = Team(player16, gameInfo.getDefaultRating)

    val teams = Seq(
      team1, team2, team3, team4, team5,
      team6, team7, team8, team9, team10,
      team11, team12, team13, team14, team15,
      team16)
    val newRatings = calculator.calculateNewRatings(
      gameInfo, teams, Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16))

    val player1NewRating = newRatings(player1)
    assertRating(40.53945776946920, 5.27581643889050, player1NewRating)

    val player2NewRating = newRatings(player2)
    assertRating(36.80951229454210, 4.71121217610266, player2NewRating)

    val player3NewRating = newRatings(player3)
    assertRating(34.34726355544460, 4.52440328139991, player3NewRating)

    val player4NewRating = newRatings(player4)
    assertRating(32.33614722608720, 4.43258628279632, player4NewRating)

    val player5NewRating = newRatings(player5)
    assertRating(30.55048814671730, 4.38010805034365, player5NewRating)

    val player6NewRating = newRatings(player6)
    assertRating(28.89277312234790, 4.34859291776483, player6NewRating)

    val player7NewRating = newRatings(player7)
    assertRating(27.30952161972210, 4.33037679041216, player7NewRating)

    val player8NewRating = newRatings(player8)
    assertRating(25.76571046519540, 4.32197078088701, player8NewRating)

    val player9NewRating = newRatings(player9)
    assertRating(24.23428953480470, 4.32197078088703, player9NewRating)

    val player10NewRating = newRatings(player10)
    assertRating(22.69047838027800, 4.33037679041219, player10NewRating)

    val player11NewRating = newRatings(player11)
    assertRating(21.10722687765220, 4.34859291776488, player11NewRating)

    val player12NewRating = newRatings(player12)
    assertRating(19.44951185328290, 4.38010805034375, player12NewRating)

    val player13NewRating = newRatings(player13)
    assertRating(17.66385277391300, 4.43258628279643, player13NewRating)

    val player14NewRating = newRatings(player14)
    assertRating(15.65273644455550, 4.52440328139996, player14NewRating)

    val player15NewRating = newRatings(player15)
    assertRating(13.19048770545810, 4.71121217610273, player15NewRating)

    val player16NewRating = newRatings(player16)
    assertRating(9.46054223053080, 5.27581643889032, player16NewRating)
  }

  //------------------------------------------------------------------------------
  // Partial Play Tests
  //------------------------------------------------------------------------------

  private def OneOnTwoBalancedPartialPlay(calculator: SkillCalculator) {
    val gameInfo = GameInfo.defaultGameInfo

    val p1 = new Player(1)
    val team1 = Team(p1, gameInfo.getDefaultRating)

    val p2 = new Player(2, 0.0)
    val p3 = new Player(3, 1.00)

    val team2 = Map(
      p2 -> gameInfo.getDefaultRating,
      p3 -> gameInfo.getDefaultRating)

    val teams = Seq(team1, team2)
    val newRatings = calculator.calculateNewRatings(gameInfo, teams, Seq(1, 2))
    val matchQuality = calculator.calculateMatchQuality(gameInfo, teams)
    // TODO assert something
  }

  //------------------------------------------------------------------------------
  // Helpers
  //------------------------------------------------------------------------------

  object Team {
    def apply(p: Player, r: Rating) = Map(p -> r)
  }
}

object TrueSkillCalculatorTestUtils {
  val ErrorTolerance = 0.085

  def assertRating(expectedMean: Double, expectedStandardDeviation: Double, actual: Rating) {
    assertEquals(actual.mean, expectedMean, ErrorTolerance)
    assertEquals(actual.standardDeviation, expectedStandardDeviation, ErrorTolerance)
  }

  def assertMatchQuality(expectedMatchQuality: Double, actualMatchQuality: Double) {
    assertEquals(actualMatchQuality, expectedMatchQuality, 0.0005)
  }
}