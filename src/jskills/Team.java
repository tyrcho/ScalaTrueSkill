package jskills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for working with a single team.
 */
public class Team<TPlayer> {

    private final Map<TPlayer, Rating> playerRatings = new HashMap<TPlayer, Rating>();

    /** Constructs a new team. **/
    public Team() {
    }

    /**
     * Constructs a Team and populates it with the specified player.
     * 
     * @param player
     *            The player to add.
     * @param rating
     *            The rating of the player.
     */
    public Team(TPlayer player, Rating rating) {
        addPlayer(player, rating);
    }

    /**
     * Adds the player to the team.
     * 
     * @param player
     *            The player to add.
     * @param rating
     *            The rating of the player
     * @returns The instance of the team (for chaining convenience).
     */
    public Team<TPlayer> addPlayer(TPlayer player, Rating rating) {
        playerRatings.put(player, rating);
        return this;
    }

    /**
     * Returns the {@link Team} as a simple mapping from the {@link Player}s to
     * their {@link Rating}.
     * 
     * @returns The {@link Team} as a simple mapping from Player to Rating.
     */
    public Map<TPlayer, Rating> asMap() {
        return playerRatings;
    }

    /**
     * Concatenates multiple teams into a list of teams.
     * <p>
     * I'm not happy with how this has turned out, but I'll just leave a TODO
     * for now.
     * 
     * @param teams
     *            The teams to concatenate together.
     * @returns A sequence of teams.
     */
    public static Iterable<Map<?, Rating>> concat(Team<?>[] teams) {
        List<Map<?, Rating>> teamslist = new ArrayList<Map<?, Rating>>();
        for (Team<?> team : teams) {
            teamslist.add(team.asMap());
        }
        return teamslist;
    }
}
