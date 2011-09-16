package jskills.trueskill.layers

import jskills.GameInfo
import jskills.factorgraphs.DefaultVariable
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.trueskill.DrawMargin
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianFactor
import jskills.trueskill.factors.GaussianGreaterThanFactor
import jskills.trueskill.factors.GaussianWithinFactor

class TeamDifferencesComparisonLayer(parentGraph: TrueSkillFactorGraph, teamRanks: Array[Int])
  extends TrueSkillFactorGraphLayer[Variable[GaussianDistribution], GaussianFactor, DefaultVariable[GaussianDistribution]](parentGraph) {
  val gameInfo = parentGraph.getGameInfo()
  val epsilon = DrawMargin.GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(), gameInfo.getBeta())

  override def BuildLayer() {
    for (i <- 0 until inputVariablesGroups.size()) {
      val isDraw = (teamRanks(i) == teamRanks(i + 1))
      val teamDifference = inputVariablesGroups.get(i).get(0)
      val factor = if (isDraw)
        new GaussianWithinFactor(epsilon, teamDifference).asInstanceOf[GaussianFactor]
      else new GaussianGreaterThanFactor(epsilon, teamDifference)
      AddLayerFactor(factor)
    }
  }
}