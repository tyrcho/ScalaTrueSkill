using System;
using System.Collections.Generic;

namespace Moserware.Skills
{    
    /**
     * Base class for all skill calculator implementations.
     */
    public abstract class SkillCalculator
    {
        [Flags]
        public enum SupportedOptions
        {
            None          = 0x00,
            PartialPlay   = 0x01,
            PartialUpdate = 0x02,
        }

        private final SupportedOptions _SupportedOptions;
        private final PlayersRange _PlayersPerTeamAllowed;
        private final TeamsRange _TotalTeamsAllowed;
        
        protected SkillCalculator(SupportedOptions supportedOptions, TeamsRange totalTeamsAllowed, PlayersRange playerPerTeamAllowed)
        {
            _SupportedOptions = supportedOptions;
            _TotalTeamsAllowed = totalTeamsAllowed;
            _PlayersPerTeamAllowed = playerPerTeamAllowed;
        }

        /**
         * Calculates new ratings based on the prior ratings and team ranks.
         */
         * <typeparam name="TPlayer">The underlying type of the player.</typeparam>
         * @param gameInfo Parameters for the game.
         * @param teams A mapping of team players and their ratings.
         * @param teamRanks The ranks of the teams where 1 is first place. For a tie, repeat the number (e.g. 1, 2, 2)
         * @returns All the players and their new ratings.
        public abstract IDictionary<TPlayer, Rating> CalculateNewRatings<TPlayer>(GameInfo gameInfo,
                                                                                  IEnumerable
                                                                                      <IDictionary<TPlayer, Rating>>
                                                                                      teams,
                                                                                  params int[] teamRanks);

        /**
         * Calculates the match quality as the likelihood of all teams drawing.
         */
         * <typeparam name="TPlayer">The underlying type of the player.</typeparam>
         * @param gameInfo Parameters for the game.
         * @param teams A mapping of team players and their ratings.
         * @returns The quality of the match between the teams as a percentage (0% = bad, 100% = well matched).
        public abstract double CalculateMatchQuality<TPlayer>(GameInfo gameInfo,
                                                              IEnumerable<IDictionary<TPlayer, Rating>> teams);

        public bool IsSupported(SupportedOptions option)
        {           
            return (_SupportedOptions & option) == option;             
        }

        /**
         * Helper function to square the <paramref name="value"/>.
         */        
         * @returns <param name="value"/> * <param name="value"/>
        protected static double Square(double value)
        {
            return value*value;
        }

        protected void ValidateTeamCountAndPlayersCountPerTeam<TPlayer>(IEnumerable<IDictionary<TPlayer, Rating>> teams)
        {
            ValidateTeamCountAndPlayersCountPerTeam(teams, _TotalTeamsAllowed, _PlayersPerTeamAllowed);
        }

        private static void ValidateTeamCountAndPlayersCountPerTeam<TPlayer>(
            IEnumerable<IDictionary<TPlayer, Rating>> teams, TeamsRange totalTeams, PlayersRange playersPerTeam)
        {
            Guard.ArgumentNotNull(teams, "teams");
            int countOfTeams = 0;
            foreach (var currentTeam in teams)
            {
                if (!playersPerTeam.IsInRange(currentTeam.Count))
                {
                    throw new ArgumentException();
                }
                countOfTeams++;
            }

            if (!totalTeams.IsInRange(countOfTeams))
            {
                throw new ArgumentException();
            }
        }
    }
}