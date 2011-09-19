package jskills.trueskill.layers

import java.util.ArrayList
import java.util.Collection
import java.util.List

import jskills.IPlayer
import jskills.factorgraphs.KeyedVariable
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleStep
import jskills.numerics.GaussianDistribution
import jskills.numerics.MathUtils
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianLikelihoodFactor
import collection.JavaConversions._

class PlayerSkillsToPerformancesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[KeyedVariable[IPlayer, GaussianDistribution], GaussianLikelihoodFactor, KeyedVariable[IPlayer, GaussianDistribution]](parentGraph) {

  override def BuildLayer() {
    for (currentTeam <- inputVariablesGroups) {
      val currentTeamPlayerPerformances = new ArrayList[KeyedVariable[IPlayer, GaussianDistribution]]()

      for (playerSkillVariable <- currentTeam) {
        val playerPerformance = CreateOutputVariable(playerSkillVariable.key)
        AddLayerFactor(CreateLikelihood(playerSkillVariable, playerPerformance))
        currentTeamPlayerPerformances.add(playerPerformance)
      }
      addOutputVariableGroup(currentTeamPlayerPerformances)
    }
  }

  private def CreateLikelihood(
    playerSkill: KeyedVariable[IPlayer, GaussianDistribution],
    playerPerformance: KeyedVariable[IPlayer, GaussianDistribution]): GaussianLikelihoodFactor =
    new GaussianLikelihoodFactor(MathUtils.square(parentGraph.gameInfo.beta), playerPerformance, playerSkill)

  private def CreateOutputVariable(key: IPlayer): KeyedVariable[IPlayer, GaussianDistribution] =
    KeyedVariable[IPlayer, GaussianDistribution](key, GaussianDistribution.UNIFORM, "%s's performance", key)

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = new ArrayList[Schedule[GaussianDistribution]]()
    for (likelihood <- localFactors) {
      schedules.add(new ScheduleStep[GaussianDistribution](
        "Skill to Perf step", likelihood, 0))
    }
    return ScheduleSequence(schedules, "All skill to performance sending")
  }

  override def createPosteriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = new ArrayList[Schedule[GaussianDistribution]]()
    for (likelihood <- localFactors) {
      schedules.add(new ScheduleStep[GaussianDistribution]("Skill to Perf step", likelihood, 1))
    }
    return ScheduleSequence(schedules, "All skill to performance sending")
  }
}