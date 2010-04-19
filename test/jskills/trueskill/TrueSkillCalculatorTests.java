using Moserware.Skills;
using NUnit.Framework;

namespace UnitTests.TrueSkill
{
    public static class TrueSkillCalculatorTests
    {
        private final double ErrorTolerance = 0.085;

        // These are the roll-up ones
        
        public static void TestAllTwoPlayerScenarios(SkillCalculator calculator)
        {            
            TwoPlayerTestNotDrawn(calculator);
            TwoPlayerTestDrawn(calculator);
            OneOnOneMassiveUpsetDrawTest(calculator);
                        
            TwoPlayerChessTestNotDrawn(calculator);
        }

        public static void TestAllTwoTeamScenarios(SkillCalculator calculator)
        {
            OneOnTwoSimpleTest(calculator);            
            OneOnTwoDrawTest(calculator);
            OneOnTwoSomewhatBalanced(calculator);
            OneOnThreeDrawTest(calculator);
            OneOnThreeSimpleTest(calculator);
            OneOnSevenSimpleTest(calculator);

            TwoOnTwoSimpleTest(calculator);
            TwoOnTwoUnbalancedDrawTest(calculator);
            TwoOnTwoDrawTest(calculator);
            TwoOnTwoUpsetTest(calculator);            
            
            ThreeOnTwoTests(calculator);

            FourOnFourSimpleTest(calculator);            
        }

        public static void TestAllMultipleTeamScenarios(SkillCalculator calculator)
        {
            ThreeTeamsOfOneNotDrawn(calculator);
            ThreeTeamsOfOneDrawn(calculator);
            FourTeamsOfOneNotDrawn(calculator);
            FiveTeamsOfOneNotDrawn(calculator);            
            EightTeamsOfOneDrawn(calculator);
            EightTeamsOfOneUpset(calculator);
            SixteenTeamsOfOneNotDrawn(calculator);            

            TwoOnFourOnTwoWinDraw(calculator);     
        }

        public static void TestPartialPlayScenarios(SkillCalculator calculator)
        {
            OneOnTwoBalancedPartialPlay(calculator);
        }

        //------------------- Actual Tests ---------------------------
        // If you see more than 3 digits of precision in the decimal point, then the expected values calculated from 
        // F# RalfH's implementation with the same input. It didn't support teams, so team values all came from the 
        // online calculator at http://atom.research.microsoft.com/trueskill/rankcalculator.aspx
        //
        // All match quality expected values came from the online calculator

        // In both cases, there may be some discrepancy after the first decimal point. I think this is due to my implementation
        // using slightly higher precision in GaussianDistribution.

        //------------------------------------------------------------------------------
        // Two Player Tests
        //------------------------------------------------------------------------------
        
        private static void TwoPlayerTestNotDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            gameInfo = GameInfo.DefaultGameInfo;
            
            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            teams = Teams.Concat(team1, team2);

            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);
                    
            player1NewRating = newRatings[player1];
            AssertRating(29.39583201999924, 7.171475587326186, player1NewRating);
            
            player2NewRating = newRatings[player2];
            AssertRating(20.60416798000076, 7.171475587326186, player2NewRating);

            AssertMatchQuality(0.447, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void TwoPlayerTestDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 1);
            
            player1NewRating = newRatings[player1];
            AssertRating(25.0, 6.4575196623173081, player1NewRating);

            player2NewRating = newRatings[player2];
            AssertRating(25.0, 6.4575196623173081, player2NewRating);

            AssertMatchQuality(0.447, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void TwoPlayerChessTestNotDrawn(SkillCalculator calculator)
        {
            // Inspired by a real bug :-)
            player1 = new Player(1);
            player2 = new Player(2);
            gameInfo = new GameInfo(1200.0, 1200.0 / 3.0, 200.0, 1200.0 / 300.0, 0.03);

            team1 = new Team(player1, new Rating(1301.0007, 42.9232));
            team2 = new Team(player2, new Rating(1188.7560, 42.5570));

            newRatings = calculator.CalculateNewRatings(gameInfo, Teams.Concat(team1, team2), 1, 2);

            player1NewRating = newRatings[player1];
            AssertRating(1304.7820836053318, 42.843513887848658, player1NewRating);

            player2NewRating = newRatings[player2];
            AssertRating(1185.0383099003536, 42.485604606897752, player2NewRating);
        }

        private static void OneOnOneMassiveUpsetDrawTest(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating);

            player2 = new Player(2);

            team2 = new Team()
                        .AddPlayer(player2, new Rating(50, 12.5));

            teams = Teams.Concat(team1, team2);

            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 1);

            // Winners
            AssertRating(31.662, 7.137, newRatingsWinLose[player1]);

            // Losers
            AssertRating(35.010, 7.910, newRatingsWinLose[player2]);

            AssertMatchQuality(0.110, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        //------------------------------------------------------------------------------
        // Two Team Tests
        //------------------------------------------------------------------------------
        
        private static void TwoOnTwoSimpleTest(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating)
                .AddPlayer(player2, gameInfo.DefaultRating);

            player3 = new Player(3);
            player4 = new Player(4);

            team2 = new Team()
                        .AddPlayer(player3, gameInfo.DefaultRating)
                        .AddPlayer(player4, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(28.108, 7.774, newRatingsWinLose[player1]);
            AssertRating(28.108, 7.774, newRatingsWinLose[player2]);
            
            // Losers
            AssertRating(21.892, 7.774, newRatingsWinLose[player3]);
            AssertRating(21.892, 7.774, newRatingsWinLose[player4]);

            AssertMatchQuality(0.447, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void TwoOnTwoDrawTest(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating)
                .AddPlayer(player2, gameInfo.DefaultRating);

            player3 = new Player(3);
            player4 = new Player(4);

            team2 = new Team()
                        .AddPlayer(player3, gameInfo.DefaultRating)
                        .AddPlayer(player4, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 1);

            // Winners
            AssertRating(25, 7.455, newRatingsWinLose[player1]);
            AssertRating(25, 7.455, newRatingsWinLose[player2]);

            // Losers
            AssertRating(25, 7.455, newRatingsWinLose[player3]);
            AssertRating(25, 7.455, newRatingsWinLose[player4]);

            AssertMatchQuality(0.447, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void TwoOnTwoUnbalancedDrawTest(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, new Rating(15, 8))
                .AddPlayer(player2, new Rating(20, 6));

            player3 = new Player(3);
            player4 = new Player(4);

            team2 = new Team()
                        .AddPlayer(player3, new Rating(25, 4))
                        .AddPlayer(player4, new Rating(30, 3));
            
            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 1);

            // Winners
            AssertRating(21.570, 6.556, newRatingsWinLose[player1]);
            AssertRating(23.696, 5.418, newRatingsWinLose[player2]);

            // Losers
            AssertRating(23.357, 3.833, newRatingsWinLose[player3]);
            AssertRating(29.075, 2.931, newRatingsWinLose[player4]);

            AssertMatchQuality(0.214, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void TwoOnTwoUpsetTest(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, new Rating(20, 8))
                .AddPlayer(player2, new Rating(25, 6));

            player3 = new Player(3);
            player4 = new Player(4);

            team2 = new Team()
                        .AddPlayer(player3, new Rating(35, 7))
                        .AddPlayer(player4, new Rating(40, 5));

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(29.698, 7.008, newRatingsWinLose[player1]);
            AssertRating(30.455, 5.594, newRatingsWinLose[player2]);

            // Losers
            AssertRating(27.575, 6.346, newRatingsWinLose[player3]);
            AssertRating(36.211, 4.768, newRatingsWinLose[player4]);

            AssertMatchQuality(0.084, calculator.CalculateMatchQuality(gameInfo, teams));
        }
                        
        private static void FourOnFourSimpleTest(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating)
                .AddPlayer(player2, gameInfo.DefaultRating)
                .AddPlayer(player3, gameInfo.DefaultRating)
                .AddPlayer(player4, gameInfo.DefaultRating);

            player5 = new Player(5);
            player6 = new Player(6);
            player7 = new Player(7);
            player8 = new Player(8);

            team2 = new Team()
                        .AddPlayer(player5, gameInfo.DefaultRating)
                        .AddPlayer(player6, gameInfo.DefaultRating)
                        .AddPlayer(player7, gameInfo.DefaultRating)
                        .AddPlayer(player8, gameInfo.DefaultRating);


            teams = Teams.Concat(team1, team2);

            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(27.198, 8.059, newRatingsWinLose[player1]);
            AssertRating(27.198, 8.059, newRatingsWinLose[player2]);
            AssertRating(27.198, 8.059, newRatingsWinLose[player3]);
            AssertRating(27.198, 8.059, newRatingsWinLose[player4]);            

            // Losers
            AssertRating(22.802, 8.059, newRatingsWinLose[player5]);
            AssertRating(22.802, 8.059, newRatingsWinLose[player6]);
            AssertRating(22.802, 8.059, newRatingsWinLose[player7]);
            AssertRating(22.802, 8.059, newRatingsWinLose[player8]);

            AssertMatchQuality(0.447, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void OneOnTwoSimpleTest(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating);

            player2 = new Player(2);
            player3 = new Player(3);

            team2 = new Team()
                        .AddPlayer(player2, gameInfo.DefaultRating)
                        .AddPlayer(player3, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(33.730, 7.317, newRatingsWinLose[player1]);

            // Losers
            AssertRating(16.270, 7.317, newRatingsWinLose[player2]);
            AssertRating(16.270, 7.317, newRatingsWinLose[player3]);

            AssertMatchQuality(0.135, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void OneOnTwoSomewhatBalanced(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, new Rating(40, 6));

            player2 = new Player(2);
            player3 = new Player(3);

            team2 = new Team()
                        .AddPlayer(player2, new Rating(20, 7))
                        .AddPlayer(player3, new Rating(25, 8));

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(42.744, 5.602, newRatingsWinLose[player1]);

            // Losers
            AssertRating(16.266, 6.359, newRatingsWinLose[player2]);
            AssertRating(20.123, 7.028, newRatingsWinLose[player3]);

            AssertMatchQuality(0.478, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void OneOnThreeSimpleTest(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating);

            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);

            team2 = new Team()
                        .AddPlayer(player2, gameInfo.DefaultRating)
                        .AddPlayer(player3, gameInfo.DefaultRating)
                        .AddPlayer(player4, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(36.337, 7.527, newRatingsWinLose[player1]);

            // Losers
            AssertRating(13.663, 7.527, newRatingsWinLose[player2]);
            AssertRating(13.663, 7.527, newRatingsWinLose[player3]);
            AssertRating(13.663, 7.527, newRatingsWinLose[player4]);

            AssertMatchQuality(0.012, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void OneOnTwoDrawTest(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating);

            player2 = new Player(2);
            player3 = new Player(3);

            team2 = new Team()
                        .AddPlayer(player2, gameInfo.DefaultRating)
                        .AddPlayer(player3, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 1);

            // Winners
            AssertRating(31.660, 7.138, newRatingsWinLose[player1]);

            // Losers
            AssertRating(18.340, 7.138, newRatingsWinLose[player2]);
            AssertRating(18.340, 7.138, newRatingsWinLose[player3]);

            AssertMatchQuality(0.135, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void OneOnThreeDrawTest(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating);

            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);

            team2 = new Team()
                        .AddPlayer(player2, gameInfo.DefaultRating)
                        .AddPlayer(player3, gameInfo.DefaultRating)
                        .AddPlayer(player4, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 1);

            // Winners
            AssertRating(34.990, 7.455, newRatingsWinLose[player1]);

            // Losers
            AssertRating(15.010, 7.455, newRatingsWinLose[player2]);
            AssertRating(15.010, 7.455, newRatingsWinLose[player3]);
            AssertRating(15.010, 7.455, newRatingsWinLose[player4]);

            AssertMatchQuality(0.012, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void OneOnSevenSimpleTest(SkillCalculator calculator)
        {
            player1 = new Player(1);

            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, gameInfo.DefaultRating);

            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);
            player5 = new Player(5);
            player6 = new Player(6);
            player7 = new Player(7);
            player8 = new Player(8);

            team2 = new Team()
                        .AddPlayer(player2, gameInfo.DefaultRating)
                        .AddPlayer(player3, gameInfo.DefaultRating)
                        .AddPlayer(player4, gameInfo.DefaultRating)
                        .AddPlayer(player5, gameInfo.DefaultRating)
                        .AddPlayer(player6, gameInfo.DefaultRating)
                        .AddPlayer(player7, gameInfo.DefaultRating)
                        .AddPlayer(player8, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(40.582, 7.917, newRatingsWinLose[player1]);

            // Losers
            AssertRating(9.418, 7.917, newRatingsWinLose[player2]);
            AssertRating(9.418, 7.917, newRatingsWinLose[player3]);
            AssertRating(9.418, 7.917, newRatingsWinLose[player4]);
            AssertRating(9.418, 7.917, newRatingsWinLose[player5]);
            AssertRating(9.418, 7.917, newRatingsWinLose[player6]);
            AssertRating(9.418, 7.917, newRatingsWinLose[player7]);
            AssertRating(9.418, 7.917, newRatingsWinLose[player8]);

            AssertMatchQuality(0.000, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void ThreeOnTwoTests(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);

            team1 = new Team()
                        .AddPlayer(player1, new Rating(28, 7))
                        .AddPlayer(player2, new Rating(27, 6))
                        .AddPlayer(player3, new Rating(26, 5));


            player4 = new Player(4);
            player5 = new Player(5);

            team2 = new Team()
                        .AddPlayer(player4, new Rating(30, 4))
                        .AddPlayer(player5, new Rating(31, 3));

            gameInfo = GameInfo.DefaultGameInfo;

            teams = Teams.Concat(team1, team2);
            newRatingsWinLoseExpected = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);

            // Winners
            AssertRating(28.658, 6.770, newRatingsWinLoseExpected[player1]);
            AssertRating(27.484, 5.856, newRatingsWinLoseExpected[player2]);
            AssertRating(26.336, 4.917, newRatingsWinLoseExpected[player3]);

            // Losers
            AssertRating(29.785, 3.958, newRatingsWinLoseExpected[player4]);
            AssertRating(30.879, 2.983, newRatingsWinLoseExpected[player5]);

            newRatingsWinLoseUpset = calculator.CalculateNewRatings(gameInfo, Teams.Concat(team1, team2), 2, 1);

            // Winners
            AssertRating(32.012, 3.877, newRatingsWinLoseUpset[player4]);
            AssertRating(32.132, 2.949, newRatingsWinLoseUpset[player5]);

            // Losers
            AssertRating(21.840, 6.314, newRatingsWinLoseUpset[player1]);
            AssertRating(22.474, 5.575, newRatingsWinLoseUpset[player2]);
            AssertRating(22.857, 4.757, newRatingsWinLoseUpset[player3]);

            AssertMatchQuality(0.254, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        //------------------------------------------------------------------------------
        // Multiple Teams Tests
        //------------------------------------------------------------------------------        

        private static void TwoOnFourOnTwoWinDraw(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team()
                .AddPlayer(player1, new Rating(40,4))
                .AddPlayer(player2, new Rating(45,3));

            player3 = new Player(3);
            player4 = new Player(4);
            player5 = new Player(5);
            player6 = new Player(6);

            team2 = new Team()
                        .AddPlayer(player3, new Rating(20, 7))
                        .AddPlayer(player4, new Rating(19, 6))
                        .AddPlayer(player5, new Rating(30, 9))
                        .AddPlayer(player6, new Rating(10, 4));

            player7 = new Player(7);
            player8 = new Player(8);

            team3 = new Team()                        
                        .AddPlayer(player7, new Rating(50,5))
                        .AddPlayer(player8, new Rating(30,2));


            teams = Teams.Concat(team1, team2, team3);
            newRatingsWinLose = calculator.CalculateNewRatings(gameInfo, teams, 1, 2, 2);

            // Winners
            AssertRating(40.877, 3.840, newRatingsWinLose[player1]);
            AssertRating(45.493, 2.934, newRatingsWinLose[player2]);
            AssertRating(19.609, 6.396, newRatingsWinLose[player3]);
            AssertRating(18.712, 5.625, newRatingsWinLose[player4]);
            AssertRating(29.353, 7.673, newRatingsWinLose[player5]);
            AssertRating(9.872, 3.891, newRatingsWinLose[player6]);
            AssertRating(48.830, 4.590, newRatingsWinLose[player7]);
            AssertRating(29.813, 1.976, newRatingsWinLose[player8]);
            
            AssertMatchQuality(0.367, calculator.CalculateMatchQuality(gameInfo, teams));
        }
        
        private static void ThreeTeamsOfOneNotDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            team3 = new Team(player3, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2, team3);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 2, 3);

            player1NewRating = newRatings[player1];
            AssertRating(31.675352419172107, 6.6559853776206905, player1NewRating);

            player2NewRating = newRatings[player2];
            AssertRating(25.000000000003912, 6.2078966412243233, player2NewRating);

            player3NewRating = newRatings[player3];
            AssertRating(18.324647580823971, 6.6559853776218318, player3NewRating);

            AssertMatchQuality(0.200, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void ThreeTeamsOfOneDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            team3 = new Team(player3, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2, team3);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 1, 1);

            player1NewRating = newRatings[player1];
            AssertRating(25.000, 5.698, player1NewRating);

            player2NewRating = newRatings[player2];
            AssertRating(25.000, 5.695, player2NewRating);

            player3NewRating = newRatings[player3];
            AssertRating(25.000, 5.698, player3NewRating);

            AssertMatchQuality(0.200, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void FourTeamsOfOneNotDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            team3 = new Team(player3, gameInfo.DefaultRating);
            team4 = new Team(player4, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2, team3, team4);

            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 2, 3, 4);

            player1NewRating = newRatings[player1];
            AssertRating(33.206680965631264, 6.3481091698077057, player1NewRating);            

            player2NewRating = newRatings[player2];
            AssertRating(27.401454693843323, 5.7871629348447584, player2NewRating);            

            player3NewRating = newRatings[player3];
            AssertRating(22.598545306188374, 5.7871629348413451, player3NewRating);            

            player4NewRating = newRatings[player4];
            AssertRating(16.793319034361271, 6.3481091698144967, player4NewRating);

            AssertMatchQuality(0.089, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void FiveTeamsOfOneNotDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);
            player5 = new Player(5);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            team3 = new Team(player3, gameInfo.DefaultRating);
            team4 = new Team(player4, gameInfo.DefaultRating);
            team5 = new Team(player5, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2, team3, team4, team5);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 2, 3, 4, 5);

            player1NewRating = newRatings[player1];
            AssertRating(34.363135705841188, 6.1361528798112692, player1NewRating);            

            player2NewRating = newRatings[player2];
            AssertRating(29.058448805636779, 5.5358352402833413, player2NewRating);            

            player3NewRating = newRatings[player3];
            AssertRating(25.000000000031758, 5.4200805474429847, player3NewRating);
            
            player4NewRating = newRatings[player4];
            AssertRating(20.941551194426314, 5.5358352402709672, player4NewRating);
            
            player5NewRating = newRatings[player5];
            AssertRating(15.636864294158848, 6.136152879829349, player5NewRating);

            AssertMatchQuality(0.040, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void EightTeamsOfOneDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);
            player5 = new Player(5);
            player6 = new Player(6);
            player7 = new Player(7);
            player8 = new Player(8);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            team3 = new Team(player3, gameInfo.DefaultRating);
            team4 = new Team(player4, gameInfo.DefaultRating);
            team5 = new Team(player5, gameInfo.DefaultRating);
            team6 = new Team(player6, gameInfo.DefaultRating);
            team7 = new Team(player7, gameInfo.DefaultRating);
            team8 = new Team(player8, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2, team3, team4, team5, team6, team7, team8);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 1, 1, 1, 1, 1, 1, 1);

            player1NewRating = newRatings[player1];
            AssertRating(25.000, 4.592, player1NewRating);

            player2NewRating = newRatings[player2];
            AssertRating(25.000, 4.583, player2NewRating);

            player3NewRating = newRatings[player3];
            AssertRating(25.000, 4.576, player3NewRating);

            player4NewRating = newRatings[player4];
            AssertRating(25.000, 4.573, player4NewRating);

            player5NewRating = newRatings[player5];
            AssertRating(25.000, 4.573, player5NewRating);

            player6NewRating = newRatings[player6];
            AssertRating(25.000, 4.576, player6NewRating);

            player7NewRating = newRatings[player7];
            AssertRating(25.000, 4.583, player7NewRating);

            player8NewRating = newRatings[player8];
            AssertRating(25.000, 4.592, player8NewRating);

            AssertMatchQuality(0.004, calculator.CalculateMatchQuality(gameInfo, teams));
        }

        private static void EightTeamsOfOneUpset(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);
            player5 = new Player(5);
            player6 = new Player(6);
            player7 = new Player(7);
            player8 = new Player(8);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, new Rating(10, 8));
            team2 = new Team(player2, new Rating(15, 7));
            team3 = new Team(player3, new Rating(20, 6));
            team4 = new Team(player4, new Rating(25, 5));
            team5 = new Team(player5, new Rating(30, 4));
            team6 = new Team(player6, new Rating(35, 3));
            team7 = new Team(player7, new Rating(40, 2));
            team8 = new Team(player8, new Rating(45, 1));

            teams = Teams.Concat(team1, team2, team3, team4, team5, team6, team7, team8);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 2, 3, 4, 5, 6, 7, 8);

            player1NewRating = newRatings[player1];
            AssertRating(35.135, 4.506, player1NewRating);

            player2NewRating = newRatings[player2];
            AssertRating(32.585, 4.037, player2NewRating);

            player3NewRating = newRatings[player3];
            AssertRating(31.329, 3.756, player3NewRating);

            player4NewRating = newRatings[player4];
            AssertRating(30.984, 3.453, player4NewRating);

            player5NewRating = newRatings[player5];
            AssertRating(31.751, 3.064, player5NewRating);

            player6NewRating = newRatings[player6];
            AssertRating(34.051, 2.541, player6NewRating);

            player7NewRating = newRatings[player7];
            AssertRating(38.263, 1.849, player7NewRating);

            player8NewRating = newRatings[player8];
            AssertRating(44.118, 0.983, player8NewRating);

            AssertMatchQuality(0.000, calculator.CalculateMatchQuality(gameInfo,teams));
        }

        private static void SixteenTeamsOfOneNotDrawn(SkillCalculator calculator)
        {
            player1 = new Player(1);
            player2 = new Player(2);
            player3 = new Player(3);
            player4 = new Player(4);
            player5 = new Player(5);
            player6 = new Player(6);
            player7 = new Player(7);
            player8 = new Player(8);
            player9 = new Player(9);
            player10 = new Player(10);
            player11 = new Player(11);
            player12 = new Player(12);
            player13 = new Player(13);
            player14 = new Player(14);
            player15 = new Player(15);
            player16 = new Player(16);
            gameInfo = GameInfo.DefaultGameInfo;

            team1 = new Team(player1, gameInfo.DefaultRating);
            team2 = new Team(player2, gameInfo.DefaultRating);
            team3 = new Team(player3, gameInfo.DefaultRating);
            team4 = new Team(player4, gameInfo.DefaultRating);
            team5 = new Team(player5, gameInfo.DefaultRating);
            team6 = new Team(player6, gameInfo.DefaultRating);
            team7 = new Team(player7, gameInfo.DefaultRating);
            team8 = new Team(player8, gameInfo.DefaultRating);
            team9 = new Team(player9, gameInfo.DefaultRating);
            team10 = new Team(player10, gameInfo.DefaultRating);
            team11 = new Team(player11, gameInfo.DefaultRating);
            team12 = new Team(player12, gameInfo.DefaultRating);
            team13 = new Team(player13, gameInfo.DefaultRating);
            team14 = new Team(player14, gameInfo.DefaultRating);
            team15 = new Team(player15, gameInfo.DefaultRating);
            team16 = new Team(player16, gameInfo.DefaultRating);

            newRatings = 
                calculator.CalculateNewRatings(
                    gameInfo, 
                    Teams.Concat(
                        team1, team2, team3, team4, team5,
                        team6, team7, team8, team9, team10,
                        team11, team12, team13, team14, team15,
                        team16), 
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

            player1NewRating = newRatings[player1];
            AssertRating(40.53945776946920, 5.27581643889050, player1NewRating);
 
            player2NewRating = newRatings[player2];
            AssertRating(36.80951229454210, 4.71121217610266, player2NewRating);
            
            player3NewRating = newRatings[player3];
            AssertRating(34.34726355544460, 4.52440328139991, player3NewRating);
            
            player4NewRating = newRatings[player4];
            AssertRating(32.33614722608720, 4.43258628279632, player4NewRating);
            
            player5NewRating = newRatings[player5];
            AssertRating(30.55048814671730, 4.38010805034365, player5NewRating);
            
            player6NewRating = newRatings[player6];
            AssertRating(28.89277312234790, 4.34859291776483, player6NewRating);
            
            player7NewRating = newRatings[player7];
            AssertRating(27.30952161972210, 4.33037679041216, player7NewRating);
            
            player8NewRating = newRatings[player8];
            AssertRating(25.76571046519540, 4.32197078088701, player8NewRating);
 
            player9NewRating = newRatings[player9];
            AssertRating(24.23428953480470, 4.32197078088703, player9NewRating);
            
            player10NewRating = newRatings[player10];
            AssertRating(22.69047838027800, 4.33037679041219, player10NewRating);
            
            player11NewRating = newRatings[player11];
            AssertRating(21.10722687765220, 4.34859291776488, player11NewRating);
            
            player12NewRating = newRatings[player12];
            AssertRating(19.44951185328290, 4.38010805034375, player12NewRating);
            
            player13NewRating = newRatings[player13];
            AssertRating(17.66385277391300, 4.43258628279643, player13NewRating);
            
            player14NewRating = newRatings[player14];
            AssertRating(15.65273644455550, 4.52440328139996, player14NewRating);
            
            player15NewRating = newRatings[player15];
            AssertRating(13.19048770545810, 4.71121217610273, player15NewRating);
            
            player16NewRating = newRatings[player16];
            AssertRating(9.46054223053080, 5.27581643889032, player16NewRating);
        }

        //------------------------------------------------------------------------------
        // Partial Play Tests
        //------------------------------------------------------------------------------        

        private static void OneOnTwoBalancedPartialPlay(SkillCalculator calculator)
        {
            gameInfo = GameInfo.DefaultGameInfo;

            p1 = new Player(1);
            team1 = new Team(p1, gameInfo.DefaultRating);

            p2 = new Player(2, 0.0);
            p3 = new Player(3, 1.00);

            team2 = new Team()
                        .AddPlayer(p2, gameInfo.DefaultRating)
                        .AddPlayer(p3, gameInfo.DefaultRating);

            teams = Teams.Concat(team1, team2);
            newRatings = calculator.CalculateNewRatings(gameInfo, teams, 1, 2);
            matchQuality = calculator.CalculateMatchQuality(gameInfo, teams);
            
        }

        //------------------------------------------------------------------------------
        // Helpers
        //------------------------------------------------------------------------------        
        
        private static void AssertRating(double expectedMean, double expectedStandardDeviation, Rating actual)
        {
            assertEquals(expectedMean, actual.Mean, ErrorTolerance);
            assertEquals(expectedStandardDeviation, actual.StandardDeviation, ErrorTolerance);
        }

        private static void AssertMatchQuality(double expectedMatchQuality, double actualMatchQuality)
        {
            assertEquals(expectedMatchQuality, actualMatchQuality, 0.0005);
        }
    }
}