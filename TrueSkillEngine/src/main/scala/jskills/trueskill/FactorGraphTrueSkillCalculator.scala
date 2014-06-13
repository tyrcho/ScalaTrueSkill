package jskills.trueskill

import jskills.numerics.MathUtils._
import jskills.GameInfo
import jskills.Guard
import jskills.IPlayer
import jskills.ITeam
import jskills.RankSorter
import jskills.Rating
import jskills.SkillCalculator
import jskills.numerics.Range
import org.ejml.simple.SimpleMatrix

import jskills.SupportedOptions
import jskills.PartialPlay
import collection.mutable.Map
import scala.collection.mutable.ListBuffer

/**
 * Calculates TrueSkill using a full factor graph.
 */
class FactorGraphTrueSkillCalculator
  extends SkillCalculator(Seq(SupportedOptions.PartialPlay, SupportedOptions.PartialUpdate),
    Range.atLeast(2),
    Range.atLeast(1)) {

  override def calculateNewRatings(gameInfo: GameInfo,
    teams: Seq[_ <: ITeam], teamRanks: Seq[Int]): Map[IPlayer, Rating] = {
    Guard.argumentNotNull(gameInfo, "gameInfo")
    validateTeamCountAndPlayersCountPerTeam(teams)

    val teamsl = RankSorter.sort(teams, teamRanks)

    val factorGraph = new TrueSkillFactorGraph(gameInfo, teamsl, teamRanks)
    factorGraph.BuildGraph()
    factorGraph.runSchedule()

    factorGraph.getUpdatedRatings()
  }

  override def calculateMatchQuality(gameInfo: GameInfo,
    teams: Seq[_ <: ITeam]): Double = {
    // We need to create the A matrix which is the player team assigments.
    val teamAssignmentsList = teams
    val skillsMatrix = getPlayerCovarianceMatrix(teamAssignmentsList)
    val meanVector = getPlayerMeansVector(teamAssignmentsList)
    val meanVectorTranspose = meanVector.transpose()

    val playerTeamAssignmentsMatrix = createTeamPerformanceToDifferenceFactor(
      teamAssignmentsList, meanVector.numRows())
    val playerTeamAssignmentsMatrixTranspose = playerTeamAssignmentsMatrix.transpose()

    val betaSquared = square(gameInfo.beta)

    val start = meanVectorTranspose.mult(playerTeamAssignmentsMatrix)
    val aTa = playerTeamAssignmentsMatrixTranspose.mult(playerTeamAssignmentsMatrix).scale(betaSquared)
    val aTSA = playerTeamAssignmentsMatrixTranspose.mult(skillsMatrix).mult(playerTeamAssignmentsMatrix)
    val middle = aTa.plus(aTSA)

    val middleInverse = middle.invert()

    val end = playerTeamAssignmentsMatrixTranspose.mult(meanVector)

    val expPartMatrix = start.mult(middleInverse).mult(end).scale(-0.5)
    val expPart = expPartMatrix.determinant()

    val sqrtPartNumerator = aTa.determinant()
    val sqrtPartDenominator = middle.determinant()
    val sqrtPart = sqrtPartNumerator / sqrtPartDenominator

    Math.exp(expPart) * Math.sqrt(sqrtPart)
  }

  def getPlayerMeansVector(teamAssignmentsList: Seq[_ <: ITeam]): SimpleMatrix = {
    // A simple list of all the player means.
    val temp = getPlayerMeanRatingValues(teamAssignmentsList)
    val tempa = temp.toArray
    new SimpleMatrix(Array(tempa)).transpose()
  }

  /**
   * This is a square matrix whose diagonal values represent the variance
   * (square of standard deviation) of all players.
   */
  private def getPlayerCovarianceMatrix(teamAssignmentsList: Seq[_ <: ITeam]): SimpleMatrix = {
    val temp = getPlayerVarianceRatingValues(teamAssignmentsList)
    SimpleMatrix.diag(temp: _*).transpose()
  }

  /**
   * TODO Make array? Helper function that gets a list of values for all
   * player ratings
   */
  private def getPlayerMeanRatingValues(teamAssignmentsList: Seq[_ <: ITeam]): Seq[Double] =
    teamAssignmentsList map (_.values map (_.mean)) flatten

  /**
   * TODO Make array? Helper function that gets a list of values for all
   * player ratings
   */
  private def getPlayerVarianceRatingValues(teamAssignmentsList: Seq[_ <: ITeam]): Seq[Double] =
    teamAssignmentsList map (_.values map (_.getVariance())) flatten

  /**
   * The team assignment matrix is often referred to as the "A" matrix. It's a
   * matrix whose rows represent the players and the columns represent teams.
   * At Matrix[row, column] represents that player[row] is on team[col]
   * Positive values represent an assignment and a negative value means that
   * we subtract the value of the next team since we're dealing with pairs.
   * This means that this matrix always has teams - 1 columns. The only other
   * tricky thing is that values represent the play percentage.
   * [p]
   * For example, consider a 3 team game where team1 is just player1, team 2
   * is player 2 and player 3, and team3 is just player 4. Furthermore, player
   * 2 and player 3 on team 2 played 25% and 75% of the time (e.g. partial
   * play), the A matrix would be:
   * [p]
   *
   * [pre]
   * A = this 4x2 matrix:
   * |  1.00  0.00 |
   * | -0.25  0.25 |
   * | -0.75  0.75 |
   * |  0.00 -1.00 |
   * [/pre]
   */
  private def createTeamPerformanceToDifferenceFactor(teamAssignmentsList: Seq[ITeam], totalPlayers: Int): SimpleMatrix = {
    val playerAssignments = ListBuffer.empty[ListBuffer[Double]]
    var totalPreviousPlayers = 0

    for (i <- 0 until teamAssignmentsList.size - 1) {
      val currentTeam = teamAssignmentsList(i)

      // Need to add in 0's for all the previous players, since they're
      // not
      // on this team
      val currentRowValues = ListBuffer.fill(totalPreviousPlayers)(0.0)
      playerAssignments += currentRowValues

      currentTeam.keys foreach {
        currentRowValues += PartialPlay.getPartialPlayPercentage(_)
      }
      // indicates the player is on the team
      totalPreviousPlayers += currentTeam.size

      val nextTeam = teamAssignmentsList(i + 1)
      for (nextTeamPlayer <- nextTeam.keySet) {
        // Add a -1 * playing time to represent the difference
        currentRowValues += -1 * PartialPlay.getPartialPlayPercentage(nextTeamPlayer)
      }
    }

    val playerTeamAssignmentsMatrix = new SimpleMatrix(totalPlayers, teamAssignmentsList.size - 1)
    for (i <- 0 until playerAssignments.size)
      for (j <- 0 until playerAssignments(i).size)
        playerTeamAssignmentsMatrix.set(j, i, playerAssignments(i)(j))

    playerTeamAssignmentsMatrix
  }
}