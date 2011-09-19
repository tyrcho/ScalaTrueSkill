package jskills.trueskill

import collection.mutable.Map
import jskills.GameInfo
import jskills.IPlayer
import jskills.ITeam
import jskills.Rating
import jskills.factorgraphs.Factor
import jskills.factorgraphs.FactorGraph
import jskills.factorgraphs.FactorGraphLayerBase
import jskills.factorgraphs.FactorList
import jskills.factorgraphs.KeyedVariable
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleSequence
import jskills.numerics.GaussianDistribution
import jskills.trueskill.layers.IteratedTeamDifferencesInnerLayer
import jskills.trueskill.layers.PlayerPerformancesToTeamPerformancesLayer
import jskills.trueskill.layers.PlayerPriorValuesToSkillsLayer
import jskills.trueskill.layers.PlayerSkillsToPerformancesLayer
import jskills.trueskill.layers.TeamDifferencesComparisonLayer
import jskills.trueskill.layers.TeamPerformancesToTeamPerformanceDifferencesLayer

import scala.collection.mutable.ListBuffer

class TrueSkillFactorGraph(
  val gameInfo: GameInfo, teams: Seq[_ <: ITeam], teamRanks: Seq[Int])
  extends FactorGraph[TrueSkillFactorGraph] {
  val priorLayer = new PlayerPriorValuesToSkillsLayer(this, teams)
  val layers = List(priorLayer,
    new PlayerSkillsToPerformancesLayer(this),
    new PlayerPerformancesToTeamPerformancesLayer(this),
    new IteratedTeamDifferencesInnerLayer(
      this,
      new TeamPerformancesToTeamPerformanceDifferencesLayer(this),
      new TeamDifferencesComparisonLayer(this, teamRanks)))

  def BuildGraph() {
    var lastOutput: Any = null
    for (currentLayer <- layers) {
      if (lastOutput != null) {
        currentLayer.setRawInputVariablesGroups(lastOutput)
      }
      currentLayer.buildLayer()
      lastOutput = currentLayer.getOutputVariablesGroups()
    }
  }

  def runSchedule() {
    val fullSchedule = createFullSchedule()
    fullSchedule.visit()
  }

  //  def GetProbabilityOfRanking(): Double = {
  //    val factorList = new FactorList[GaussianDistribution]()
  //
  //    for (currentLayer <- layers) {
  //      for (currentFactor <- currentLayer.getUntypedFactors()) {
  //        factorList.addFactor(currentFactor)
  //      }
  //    }
  //
  //    val logZ = factorList.getLogNormalization()
  //    return Math.exp(logZ)
  //  }

  private def createFullSchedule(): Schedule[GaussianDistribution] = {
    val fullSchedule = ListBuffer.empty[Schedule[GaussianDistribution]]

    layers map (_.createPriorSchedule()) filter (_ != null) foreach (fullSchedule += _)
    layers.reverse map (_.createPosteriorSchedule()) filter (_ != null) foreach (fullSchedule += _)

    return new ScheduleSequence[GaussianDistribution]("Full schedule", fullSchedule)
  }

  def getUpdatedRatings(): Map[IPlayer, Rating] = {
    val result = Map.empty[IPlayer, Rating]
    for (currentTeam <- priorLayer.getOutputVariablesGroups()) {
      for (currentPlayer <- currentTeam) {
        result.put(currentPlayer.key, new Rating(currentPlayer.value.mean, currentPlayer.value.standardDeviation))
      }
    }

    return result
  }
}