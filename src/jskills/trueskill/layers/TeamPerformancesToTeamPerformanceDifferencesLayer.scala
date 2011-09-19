package jskills.trueskill.layers

import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianWeightedSumFactor

class TeamPerformancesToTeamPerformanceDifferencesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[Variable[GaussianDistribution], GaussianWeightedSumFactor, Variable[GaussianDistribution]](parentGraph) {

  override def buildLayer() {
    for (i <- 0 until inputVariablesGroups.size - 1) {
      val strongerTeam = inputVariablesGroups(i)(0)
      val weakerTeam = inputVariablesGroups(i + 1)(0)

      val currentDifference = createOutputVariable()
      addLayerFactor(createTeamPerformanceToDifferenceFactor(
        strongerTeam, weakerTeam, currentDifference))

      // REVIEW: Does it make sense to have groups of one?
      addOutputVariable(currentDifference)
    }
  }

  private def createTeamPerformanceToDifferenceFactor(
    strongerTeam: Variable[GaussianDistribution],
    weakerTeam: Variable[GaussianDistribution],
    output: Variable[GaussianDistribution]): GaussianWeightedSumFactor = {
    val teams = List(strongerTeam, weakerTeam)
     new GaussianWeightedSumFactor(output, teams, Array(1.0, -1.0))
  }

  private def createOutputVariable(): Variable[GaussianDistribution] = {
     new Variable[GaussianDistribution](
      GaussianDistribution.UNIFORM, "Team performance difference")
  }
}