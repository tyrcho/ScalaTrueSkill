package jskills.elo

import collection.mutable.Map
import jskills.GameInfo
import jskills.IPlayer
import jskills.ITeam
import jskills.PairwiseComparison
import jskills.Player
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.Team
import jskills.numerics.MathUtils
import jskills.numerics.Range

class DuellingEloCalculator(twoPlayerEloCalculator: TwoPlayerEloCalculator)
  extends SkillCalculator(Seq(), Range.atLeast(2), Range.atLeast(1)) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[_ <: ITeam],
    teamRanks: Seq[Int]): Map[IPlayer, Rating] = {
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

    val deltas = Map.empty[IPlayer, Map[IPlayer, Double]]

    for (ixCurrentTeam <- 0 until teamsList.length) {
      for (ixOtherTeam <- 0 until teamsList.length) {
        if (ixOtherTeam != ixCurrentTeam) {

          val currentTeam = teamsList(ixCurrentTeam)
          val otherTeam = teamsList(ixOtherTeam)

          // Remember that bigger numbers mean worse rank (e.g.
          // other-current is what we want)
          val comparison = PairwiseComparison.fromMultiplier(Math.signum(tr(ixOtherTeam) - tr(ixCurrentTeam)))

          for ((ck, cv) <- currentTeam)
            for ((ok, ov) <- otherTeam)
              updateDuels(gameInfo, deltas, ck, cv, ok, ov, comparison)
        }
      }
    }

    val result = Map.empty[IPlayer, Rating]

    for (currentTeam <- teamsList) {
      for ((ck, cv) <- currentTeam) {
        val aa = deltas(ck).values
        val currentPlayerAverageDuellingDelta = MathUtils.mean(aa)
        result.put(ck, new EloRating(cv.mean + currentPlayerAverageDuellingDelta))
      }
    }

     result
  }

  private def updateDuels(
    gameInfo: GameInfo,
    duels: Map[IPlayer, Map[IPlayer, Double]],
    player1: IPlayer,
    player1Rating: Rating,
    player2: IPlayer,
    player2Rating: Rating,
    weakToStrongComparison: PairwiseComparison) {

    val t1 = List(Team(player2, player2Rating), Team(player1, player1Rating))

    val duelOutcomes = weakToStrongComparison match {
      case PairwiseComparison.WIN => twoPlayerEloCalculator.calculateNewRatings(gameInfo, t1, Seq(1, 2))
      case PairwiseComparison.LOSE => twoPlayerEloCalculator.calculateNewRatings(gameInfo, t1, Seq(2, 1))
      case PairwiseComparison.DRAW => twoPlayerEloCalculator.calculateNewRatings(gameInfo, t1, Seq(1, 1))
    }

    updateDuelInfo(duels, player1, player1Rating, duelOutcomes(player1), player2)
    updateDuelInfo(duels, player2, player2Rating, duelOutcomes(player2), player1)
  }

  override def calculateMatchQuality(gameInfo: GameInfo, teams: Seq[_ <: ITeam]): Double = {
    // HACK! Need a better algorithm, this is just to have something there
    // and it isn't good
    var minQuality = 1.0

    for (ixCurrentTeam <- 0 until teams.length) {
      val currentTeamAverageRating = new EloRating(Rating.calcMeanMean(teams(ixCurrentTeam).values.toSeq))
      val currentTeam = Team(new Player[Integer](ixCurrentTeam), currentTeamAverageRating)

      for (ixOtherTeam <- ixCurrentTeam + 1 until teams.length) {
        val otherTeamAverageRating = new EloRating(Rating.calcMeanMean(teams(ixOtherTeam).values.toSeq))
        val otherTeam = Team(new Player[Integer](ixOtherTeam), otherTeamAverageRating)

        minQuality = Math.min(minQuality, twoPlayerEloCalculator.calculateMatchQuality(gameInfo, List(currentTeam, otherTeam)))
      }
    }

     minQuality
  }

  def updateDuelInfo(
    duels: Map[IPlayer, Map[IPlayer, Double]],
    self: IPlayer,
    selfBeforeRating: Rating,
    selfAfterRating: Rating,
    opponent: IPlayer) {
    val selfToOpponentDuelDeltas = duels.getOrElseUpdate(self, Map.empty)
    selfToOpponentDuelDeltas.put(opponent, selfAfterRating.mean - selfBeforeRating.mean)
  }
}