package jskills.trueskill.layers

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

import collection.mutable.LinkedList
import scala.collection.mutable.ListBuffer

// We intentionally have no Posterior schedule since the only purpose here is to 
class PlayerPriorValuesToSkillsLayer(parentGraph: TrueSkillFactorGraph, teams: Seq[_ <: ITeam])
  extends TrueSkillFactorGraphLayer[DefaultVariable[GaussianDistribution], GaussianPriorFactor, KeyedVariable[IPlayer, GaussianDistribution]](parentGraph) {

  override def buildLayer() {
    for (currentTeam <- teams) {
      val currentTeamSkills = ListBuffer.empty[KeyedVariable[IPlayer, GaussianDistribution]]

      for (currentTeamPlayer <- currentTeam) {
        val playerSkill = createSkillOutputVariable(currentTeamPlayer._1)
        addLayerFactor(createPriorFactor(currentTeamPlayer._1, currentTeamPlayer._2, playerSkill))
        currentTeamSkills += playerSkill
      }

      addOutputVariableGroup(currentTeamSkills.toList)
    }
  }

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (
      new ScheduleStep[GaussianDistribution]("Prior to Skill Step", _, 0))
    return scheduleSequence(schedules, "All priors")
  }

  private def createPriorFactor(player: IPlayer, priorRating: Rating, skillsVariable: Variable[GaussianDistribution]): GaussianPriorFactor =
    new GaussianPriorFactor(priorRating.mean,
      MathUtils.square(priorRating.standardDeviation) + MathUtils.square(parentGraph.gameInfo.dynamicsFactor),
      skillsVariable)

  private def createSkillOutputVariable(key: IPlayer): KeyedVariable[IPlayer, GaussianDistribution] =
    KeyedVariable[IPlayer, GaussianDistribution](key, GaussianDistribution.UNIFORM, "%s's skill", key.toString())
}