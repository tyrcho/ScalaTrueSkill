package jskills.trueskill.layers

import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.trueskill.DrawMargin
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianFactor
import jskills.trueskill.factors.GaussianGreaterThanFactor
import jskills.trueskill.factors.GaussianWithinFactor

class TeamDifferencesComparisonLayer(parentGraph: TrueSkillFactorGraph, teamRanks: Seq[Int])
  extends TrueSkillFactorGraphLayer[Variable[GaussianDistribution], GaussianFactor, Variable[GaussianDistribution]](parentGraph) {
  val gameInfo = parentGraph.gameInfo
  val epsilon = DrawMargin.getDrawMarginFromDrawProbability(gameInfo.drawProbability, gameInfo.beta)

  override def buildLayer() {
    for (i <- 0 until inputVariablesGroups.size) {
      val isDraw = (teamRanks(i) == teamRanks(i + 1))
      val teamDifference = inputVariablesGroups(i)(0)
      val factor = if (isDraw)
        new GaussianWithinFactor(epsilon, teamDifference).asInstanceOf[GaussianFactor]
      else new GaussianGreaterThanFactor(epsilon, teamDifference)
      addLayerFactor(factor)
    }
  }
}