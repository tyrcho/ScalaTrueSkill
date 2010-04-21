package jskills;

/**
 * Represents a comparison between two players.
 * <p>
 * The actual values for the enum were chosen so that the also correspond to the
 * multiplier for updates to means.
 */
public enum PairwiseComparison{
    WIN(1),
    DRAW(0),
    LOSE(-1);
    
    public final int multiplier;
    
    private PairwiseComparison(int multiplier) { this.multiplier = multiplier; }
}