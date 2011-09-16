package jskills.trueskill

import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.HashMap
import java.util.List
import java.util.Map
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

import collection.JavaConversions._

class TrueSkillFactorGraph(
  val gameInfo: GameInfo, teams: Collection[_ <: ITeam], teamRanks: Array[Int])
  extends FactorGraph[TrueSkillFactorGraph] {
  val layers = new ArrayList[FactorGraphLayerBase[GaussianDistribution]]()
  val priorLayer = new PlayerPriorValuesToSkillsLayer(this, teams)

  layers.add(priorLayer)
  layers.add(new PlayerSkillsToPerformancesLayer(this))
  layers.add(new PlayerPerformancesToTeamPerformancesLayer(this))
  layers.add(new IteratedTeamDifferencesInnerLayer(
    this,
    new TeamPerformancesToTeamPerformanceDifferencesLayer(this),
    new TeamDifferencesComparisonLayer(this, teamRanks)))

  def BuildGraph() {
    var lastOutput: Any = null
    for (currentLayer <- layers) {
      if (lastOutput != null) {
        currentLayer.SetRawInputVariablesGroups(lastOutput)
      }
      currentLayer.BuildLayer()
      lastOutput = currentLayer.getOutputVariablesGroups()
    }
  }

  def RunSchedule() {
    val fullSchedule = CreateFullSchedule()
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

  private def CreateFullSchedule(): Schedule[GaussianDistribution] = {
    val fullSchedule = new ArrayList[Schedule[GaussianDistribution]]()

    layers map (_.createPriorSchedule()) filter (_ != null) foreach (fullSchedule.add(_))
    layers.reverse map (_.createPosteriorSchedule()) filter (_ != null) foreach (fullSchedule.add(_))

    return new ScheduleSequence[GaussianDistribution]("Full schedule", fullSchedule)
  }

  def GetUpdatedRatings(): Map[IPlayer, Rating] = {
    val result = new HashMap[IPlayer, Rating]()
    for (currentTeam <- priorLayer.getOutputVariablesGroups()) {
      for (currentPlayer <- currentTeam) {
        result.put(currentPlayer.key, new Rating(currentPlayer.value.mean, currentPlayer.value.standardDeviation))
      }
    }

    return result
  }
}