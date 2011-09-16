package jskills.trueskill.layers

import java.util.ArrayList
import java.util.List

import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianWeightedSumFactor

class TeamPerformancesToTeamPerformanceDifferencesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[Variable[GaussianDistribution], GaussianWeightedSumFactor, Variable[GaussianDistribution]](parentGraph) {

  override def BuildLayer() {
    for (i <- 0 until inputVariablesGroups.size() - 1) {
      val strongerTeam = inputVariablesGroups.get(i).get(0)
      val weakerTeam = inputVariablesGroups.get(i + 1).get(0)

      val currentDifference = createOutputVariable()
      AddLayerFactor(CreateTeamPerformanceToDifferenceFactor(
        strongerTeam, weakerTeam, currentDifference))

      // REVIEW: Does it make sense to have groups of one?
      addOutputVariable(currentDifference)
    }
  }

  private def CreateTeamPerformanceToDifferenceFactor(
    strongerTeam: Variable[GaussianDistribution],
    weakerTeam: Variable[GaussianDistribution],
    output: Variable[GaussianDistribution]): GaussianWeightedSumFactor = {
    val teams = new ArrayList[Variable[GaussianDistribution]]() {
      {
        add(strongerTeam)
        add(weakerTeam)
      }
    }
    return new GaussianWeightedSumFactor(output, teams, Array[Double](1.0, -1.0))
  }

  private def createOutputVariable(): Variable[GaussianDistribution] = {
    return new Variable[GaussianDistribution](
      GaussianDistribution.UNIFORM, "Team performance difference")
  }
}