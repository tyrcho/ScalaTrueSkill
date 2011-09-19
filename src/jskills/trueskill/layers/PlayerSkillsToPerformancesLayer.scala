package jskills.trueskill.layers

import jskills.IPlayer
import jskills.factorgraphs.KeyedVariable
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleStep
import jskills.numerics.GaussianDistribution
import jskills.numerics.MathUtils
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianLikelihoodFactor


import scala.collection.mutable.ListBuffer

class PlayerSkillsToPerformancesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[KeyedVariable[IPlayer, GaussianDistribution], GaussianLikelihoodFactor, KeyedVariable[IPlayer, GaussianDistribution]](parentGraph) {

  override def buildLayer() {
    for (currentTeam <- inputVariablesGroups) {
      val currentTeamPlayerPerformances = ListBuffer.empty[KeyedVariable[IPlayer, GaussianDistribution]]

      for (playerSkillVariable <- currentTeam) {
        val playerPerformance = createOutputVariable(playerSkillVariable.key)
        addLayerFactor(createLikelihood(playerSkillVariable, playerPerformance))
        currentTeamPlayerPerformances += playerPerformance
      }
      addOutputVariableGroup(currentTeamPlayerPerformances)
    }
  }

  private def createLikelihood(
    playerSkill: KeyedVariable[IPlayer, GaussianDistribution],
    playerPerformance: KeyedVariable[IPlayer, GaussianDistribution]): GaussianLikelihoodFactor =
    new GaussianLikelihoodFactor(MathUtils.square(parentGraph.gameInfo.beta), playerPerformance, playerSkill)

  private def createOutputVariable(key: IPlayer): KeyedVariable[IPlayer, GaussianDistribution] =
    KeyedVariable[IPlayer, GaussianDistribution](key, GaussianDistribution.UNIFORM, "%s's performance", key)

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (new ScheduleStep[GaussianDistribution]("Skill to Perf step", _, 0))
    return scheduleSequence(schedules, "All skill to performance sending")
  }

  override def createPosteriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (new ScheduleStep[GaussianDistribution]("Skill to Perf step", _, 1))
    return scheduleSequence(schedules, "All skill to performance sending")
  }
}