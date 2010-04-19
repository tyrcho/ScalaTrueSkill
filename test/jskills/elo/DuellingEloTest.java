using Moserware.Skills;
using Moserware.Skills.Elo;
using NUnit.Framework;

namespace UnitTests.Elo
{
    [TestFixture]
    public class DuellingEloTest
    {
        private final double ErrorTolerance = 0.1;

        [Test]
        public void TwoOnTwoDuellingTest()
        {
            calculator = new DuellingEloCalculator(new GaussianEloCalculator());

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

            // TODO: Verify?
            AssertRating(37, newRatingsWinLose[player1]);
            AssertRating(37, newRatingsWinLose[player2]);
            AssertRating(13, newRatingsWinLose[player3]);
            AssertRating(13, newRatingsWinLose[player4]);

            quality = calculator.CalculateMatchQuality(gameInfo, teams);
            assertEquals(1.0, quality, 0.001);
        }

        private static void AssertRating(double expected, Rating actual)
        {
            assertEquals(expected, actual.Mean, ErrorTolerance);
        }
    }
}
