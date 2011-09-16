package jskills.trueskill;

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
import jskills.trueskill.layers.TeamPerformancesToTeamPerformanceDifferencesLayer;
import scala.reflect.BeanProperty
import collection.JavaConversions._

class TrueSkillFactorGraph(
  @BeanProperty val gameInfo: GameInfo, teams: Collection[_ <: ITeam], teamRanks: Array[Int])
  extends FactorGraph[TrueSkillFactorGraph] {
  val _Layers = new ArrayList[FactorGraphLayerBase[GaussianDistribution]]();
  val _PriorLayer = new PlayerPriorValuesToSkillsLayer(this, teams);

  _Layers.add(_PriorLayer);
  _Layers.add(new PlayerSkillsToPerformancesLayer(this));
  _Layers.add(new PlayerPerformancesToTeamPerformancesLayer(this));
  _Layers.add(new IteratedTeamDifferencesInnerLayer(
    this,
    new TeamPerformancesToTeamPerformanceDifferencesLayer(this),
    new TeamDifferencesComparisonLayer(this, teamRanks)));

  def BuildGraph() {
    var lastOutput: Any = null
    for (currentLayer <- _Layers) {
      if (lastOutput != null) {
        currentLayer.SetRawInputVariablesGroups(lastOutput);
      }
      currentLayer.BuildLayer();
      lastOutput = currentLayer.GetRawOutputVariablesGroups();
    }
  }

  def RunSchedule() {
    val fullSchedule = CreateFullSchedule();
    fullSchedule.visit();
  }

  def GetProbabilityOfRanking(): Double = {
    val factorList = new FactorList[GaussianDistribution]();

    for (currentLayer <- _Layers) {
      for (currentFactor <- currentLayer.getUntypedFactors()) {
        factorList.addFactor(currentFactor);
      }
    }

    val logZ = factorList.getLogNormalization();
    return Math.exp(logZ);
  }

  private def CreateFullSchedule(): Schedule[GaussianDistribution] = {
    val fullSchedule = new ArrayList[Schedule[GaussianDistribution]]();

    for (currentLayer <- _Layers) {
      val currentPriorSchedule = currentLayer.createPriorSchedule();
      if (currentPriorSchedule != null) {
        fullSchedule.add(currentPriorSchedule);
      }
    }

    // Getting as a list to use reverse()
    val allLayers = new ArrayList[FactorGraphLayerBase[GaussianDistribution]](_Layers);
    Collections.reverse(allLayers);

    for (currentLayer <- allLayers) {
      val currentPosteriorSchedule = currentLayer.createPosteriorSchedule();
      if (currentPosteriorSchedule != null) {
        fullSchedule.add(currentPosteriorSchedule);
      }
    }

    return new ScheduleSequence[GaussianDistribution]("Full schedule", fullSchedule);
  }

  def GetUpdatedRatings(): Map[IPlayer, Rating] = {
    val result = new HashMap[IPlayer, Rating]();
    for (currentTeam <- _PriorLayer.getOutputVariablesGroups()) {
      for (currentPlayer <- currentTeam) {
        result.put(currentPlayer.getKey(), new Rating(currentPlayer.getValue().getMean(), currentPlayer.getValue().getStandardDeviation()));
      }
    }

    return result;
  }
}