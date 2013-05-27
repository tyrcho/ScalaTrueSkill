package jskills.trueskill.layers

import jskills.Player
import jskills.factorgraphs.KeyedVariable
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleStep
import jskills.numerics.GaussianDistribution
import jskills.numerics.MathUtils
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianLikelihoodFactor


import scala.collection.mutable.ListBuffer

class PlayerSkillsToPerformancesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[KeyedVariable[Player, GaussianDistribution], GaussianLikelihoodFactor, KeyedVariable[Player, GaussianDistribution]](parentGraph) {

  override def buildLayer() {
    for (currentTeam <- inputVariablesGroups) {
      val currentTeamPlayerPerformances = ListBuffer.empty[KeyedVariable[Player, GaussianDistribution]]

      for (playerSkillVariable <- currentTeam) {
        val playerPerformance = createOutputVariable(playerSkillVariable.key)
        addLayerFactor(createLikelihood(playerSkillVariable, playerPerformance))
        currentTeamPlayerPerformances += playerPerformance
      }
      addOutputVariableGroup(currentTeamPlayerPerformances)
    }
  }

  private def createLikelihood(
    playerSkill: KeyedVariable[Player, GaussianDistribution],
    playerPerformance: KeyedVariable[Player, GaussianDistribution]): GaussianLikelihoodFactor =
    new GaussianLikelihoodFactor(MathUtils.square(parentGraph.gameInfo.beta), playerPerformance, playerSkill)

  private def createOutputVariable(key: Player): KeyedVariable[Player, GaussianDistribution] =
    KeyedVariable[Player, GaussianDistribution](key, GaussianDistribution.UNIFORM, "%s's performance", key)

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (new ScheduleStep[GaussianDistribution]("Skill to Perf step", _, 0))
     scheduleSequence(schedules, "All skill to performance sending")
  }

  override def createPosteriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (new ScheduleStep[GaussianDistribution]("Skill to Perf step", _, 1))
     scheduleSequence(schedules, "All skill to performance sending")
  }
}