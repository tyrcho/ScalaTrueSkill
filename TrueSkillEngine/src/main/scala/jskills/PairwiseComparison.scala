package jskills

/**
 * Represents a comparison between two players.
 * <p>
 * The actual values for the enum were chosen so that the also correspond to the
 * multiplier for updates to means.
 */
abstract class PairwiseComparison(val multiplier: Int) {
  def opposite: PairwiseComparison
}

object PairwiseComparison {
  case object WIN extends PairwiseComparison(1) { def opposite = LOSE }
  case object DRAW extends PairwiseComparison(0) { def opposite = DRAW }
  case object LOSE extends PairwiseComparison(-1) { def opposite = WIN }

  def fromMultiplier(multiplier: Int): PairwiseComparison = {
    multiplier match {
      case 1 => WIN
      case 0 => DRAW
      case -1 => LOSE
    }
  }
}