package jskills.trueskill.layers

import java.util.ArrayList
import java.util.Collection
import java.util.List
import java.util.Map.Entry

import jskills.IPlayer
import jskills.ITeam
import jskills.Rating
import jskills.factorgraphs.DefaultVariable
import jskills.factorgraphs.KeyedVariable
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleStep
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.numerics.MathUtils
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianPriorFactor
import collection.JavaConversions._

// We intentionally have no Posterior schedule since the only purpose here is to 
class PlayerPriorValuesToSkillsLayer(parentGraph: TrueSkillFactorGraph, teams: Collection[_ <: ITeam])
  extends TrueSkillFactorGraphLayer[DefaultVariable[GaussianDistribution], GaussianPriorFactor, KeyedVariable[IPlayer, GaussianDistribution]](parentGraph) {

  override def BuildLayer() {
    for (currentTeam <- teams) {
      val currentTeamSkills = new ArrayList[KeyedVariable[IPlayer, GaussianDistribution]]()

      for (
        currentTeamPlayer <- currentTeam
          .entrySet()
      ) {
        val playerSkill = CreateSkillOutputVariable(currentTeamPlayer.getKey())
        AddLayerFactor(CreatePriorFactor(currentTeamPlayer.getKey(), currentTeamPlayer.getValue(), playerSkill))
        currentTeamSkills.add(playerSkill)
      }

      addOutputVariableGroup(currentTeamSkills)
    }
  }

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = new ArrayList[Schedule[GaussianDistribution]]()
    for (prior <- localFactors) {
      schedules.add(new ScheduleStep[GaussianDistribution](
        "Prior to Skill Step", prior, 0))
    }
    return ScheduleSequence(schedules, "All priors")
  }

  private def CreatePriorFactor(player: IPlayer, priorRating: Rating, skillsVariable: Variable[GaussianDistribution]): GaussianPriorFactor =
    new GaussianPriorFactor(priorRating.mean,
      MathUtils.square(priorRating.standardDeviation) + MathUtils.square(parentGraph.gameInfo.dynamicsFactor),
      skillsVariable)

  private def CreateSkillOutputVariable(key: IPlayer): KeyedVariable[IPlayer, GaussianDistribution] =
    KeyedVariable[IPlayer, GaussianDistribution](key, GaussianDistribution.UNIFORM, "%s's skill", key.toString())
}