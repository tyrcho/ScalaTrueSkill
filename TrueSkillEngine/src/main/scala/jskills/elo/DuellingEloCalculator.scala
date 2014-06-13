package jskills.elo

import jskills.GameInfo
import jskills.Player
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.Player
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.MathUtils
import scala.math._

class DuellingEloCalculator(twoPlayerEloCalculator: TwoPlayerEloCalculator)
  extends SkillCalculator(Seq(), 2 to Int.MaxValue, 1 to Int.MaxValue) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[Map[Player, Rating]],
    teamRanks: Seq[Int]): Map[Player, Rating] = {
    // On page 6 of the TrueSkill paper, the authors write:
    /*
		 * "When we had to process a team game or a game with more than two
		 * teams we used the so-called *duelling* heuristic: For each player,
		 * compute the ?'s in comparison to all other players based on the team
		 * outcome of the player and every other player and perform an update
		 * with the average of the ?'s."
		 */
    // This implements that algorithm.

    validateTeamCountAndPlayersCountPerTeam(teams)
    val teamsList = RankSorter.sort(teams, teamRanks)
    val tr = teamRanks.sortBy(i => i)

    var deltas = Map.empty[Player, Map[Player, Double]]
    for {
      ixCurrentTeam <- 0 until teamsList.length
      ixOtherTeam <- 0 until teamsList.length
      if (ixOtherTeam != ixCurrentTeam)
      currentTeam = teamsList(ixCurrentTeam)
      otherTeam = teamsList(ixOtherTeam)
      // Remember that bigger numbers mean worse rank (e.g.
      // other-current is what we want)
          val comparison = PairwiseComparison.fromMultiplier(Math.signum(tr(ixOtherTeam) - tr(ixCurrentTeam)).toInt)
      (ck, cv) <- currentTeam
      (ok, ov) <- otherTeam
    } {
      deltas = updateDuels(gameInfo, deltas, ck, cv, ok, ov, comparison)
    }

    (for {
      currentTeam <- teamsList
      (ck, cv) <- currentTeam
      aa = deltas(ck).values
      currentPlayerAverageDuellingDelta = MathUtils.mean(aa)
    } yield ck -> new EloRating(cv.mean + currentPlayerAverageDuellingDelta)).toMap
  }

  private def updateDuels(
    gameInfo: GameInfo,
    duels: Map[Player, Map[Player, Double]],
    player1: Player,
    player1Rating: Rating,
    player2: Player,
    player2Rating: Rating,
    weakToStrongComparison: PairwiseComparison) = {

    val t1 = List(Map(player2 -> player2Rating), Map(player1 -> player1Rating))

    val duelOutcomes = weakToStrongComparison match {
      case PairwiseComparison.WIN => twoPlayerEloCalculator.calculateNewRatings(gameInfo, t1, Seq(1, 2))
      case PairwiseComparison.LOSE => twoPlayerEloCalculator.calculateNewRatings(gameInfo, t1, Seq(2, 1))
      case PairwiseComparison.DRAW => twoPlayerEloCalculator.calculateNewRatings(gameInfo, t1, Seq(1, 1))
    }

    val duels1 = updateDuelInfo(duels, player1, player1Rating, duelOutcomes(player1), player2)
    updateDuelInfo(duels1, player2, player2Rating, duelOutcomes(player2), player1)
  }

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[Map[Player, Rating]]): Double = {
    // HACK! Need a better algorithm, this is just to have something there
    // and it isn't good
    var minQuality = 1.0

    for (ixCurrentTeam <- 0 until teams.length) {
      val currentTeamAverageRating = new EloRating(Rating.calcMeanMean(teams(ixCurrentTeam).values.toSeq))
      val currentTeam = Map(Player(ixCurrentTeam) -> currentTeamAverageRating)

      for (ixOtherTeam <- ixCurrentTeam + 1 until teams.length) {
        val otherTeamAverageRating = new EloRating(Rating.calcMeanMean(teams(ixOtherTeam).values.toSeq))
        val otherTeam = Map(Player(ixOtherTeam) -> otherTeamAverageRating)

        minQuality = Math.min(minQuality, twoPlayerEloCalculator.calculateMatchQuality(gameInfo, List(currentTeam, otherTeam)))
      }
    }

    minQuality
  }

  def updateDuelInfo(
    duels: Map[Player, Map[Player, Double]],
    self: Player,
    selfBeforeRating: Rating,
    selfAfterRating: Rating,
    opponent: Player): Map[Player, Map[Player, Double]] = {
    val selfToOpponentDuelDeltas = duels.getOrElse(self, Map.empty) + (opponent -> (selfAfterRating.mean - selfBeforeRating.mean))
    duels + (self -> selfToOpponentDuelDeltas)
  }
}