package jskills.elo

import jskills.Rating

/**
 * An Elo rating represented by a single number (mean).
 */
class EloRating(rating: Double) extends Rating(rating, 0)