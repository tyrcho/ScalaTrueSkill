using System.Collections.Generic;
using Moserware.Skills.TrueSkill;

namespace Moserware.Skills
{
    /**
     * Calculates a TrueSkill rating using <see cref="FactorGraphTrueSkillCalculator"/>.
     */
    public static class TrueSkillCalculator
    {
        // Keep a singleton around
        private static final SkillCalculator _Calculator
            = new FactorGraphTrueSkillCalculator();

        /**
         * Calculates new ratings based on the prior ratings and team ranks.
         */
         * @param gameInfo Parameters for the game.
         * @param teams A mapping of team players and their ratings.
         * @param teamRanks The ranks of the teams where 1 is first place. For a tie, repeat the number (e.g. 1, 2, 2)
         * @returns All the players and their new ratings.
        public static IDictionary<TPlayer, Rating> CalculateNewRatings<TPlayer>(GameInfo gameInfo,
                                                                                IEnumerable
                                                                                    <IDictionary<TPlayer, Rating>> teams,
                                                                                params int[] teamRanks)
        {
            // Just punt the work to the full implementation
            return _Calculator.CalculateNewRatings(gameInfo, teams, teamRanks);
        }

        /**
         * Calculates the match quality as the likelihood of all teams drawing.
         */
         * <typeparam name="TPlayer">The underlying type of the player.</typeparam>
         * @param gameInfo Parameters for the game.
         * @param teams A mapping of team players and their ratings.
         * @returns The match quality as a percentage (between 0.0 and 1.0).
        public static double CalculateMatchQuality<TPlayer>(GameInfo gameInfo,
                                                            IEnumerable<IDictionary<TPlayer, Rating>> teams)
        {
            // Just punt the work to the full implementation
            return _Calculator.CalculateMatchQuality(gameInfo, teams);
        }
    }
}