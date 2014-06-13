package jskills.trueskill.layers

import jskills.Player
import jskills.PartialPlay
import jskills.factorgraphs.KeyedVariable
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleStep
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianWeightedSumFactor

class PlayerPerformancesToTeamPerformancesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[KeyedVariable[Player, GaussianDistribution], GaussianWeightedSumFactor, Variable[GaussianDistribution]](parentGraph) {

  override def buildLayer() {
    for (currentTeam <- inputVariablesGroups) {
      val teamPerformance = createOutputVariable(currentTeam)
      addLayerFactor(createPlayerToTeamSumFactor(currentTeam,
        teamPerformance))

      // REVIEW: Does it make sense to have groups of one?
      addOutputVariable(teamPerformance)
    }
  }

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (new ScheduleStep[GaussianDistribution]("Perf to Team Perf Step", _, 0))
     scheduleSequence(schedules, "all player perf to team perf schedule")
  }

  def createPlayerToTeamSumFactor(teamMembers: Seq[KeyedVariable[Player, GaussianDistribution]], sumVariable: Variable[GaussianDistribution]): GaussianWeightedSumFactor = {
    val weights = teamMembers map (m => PartialPlay.getPartialPlayPercentage(m.key))
    new GaussianWeightedSumFactor(sumVariable, teamMembers, weights.toArray)
  }

  override def createPosteriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = localFactors map (currentFactor =>
      0 until currentFactor.getNumberOfMessages() map (
        i => new ScheduleStep[GaussianDistribution]("team sum perf @" + i, currentFactor, i)))
    //    
    //    
    //      List.empty[Schedule[GaussianDistribution]]
    //    for (currentFactor <- localFactors) {
    //      // TODO is there an off by 1 error here?
    //      for (i <- 0 until currentFactor.getNumberOfMessages()) {
    //        schedules.add(new ScheduleStep[GaussianDistribution]("team sum perf @" + i, currentFactor, i))
    //      }
    //    }
    scheduleSequence(schedules.flatten, "all of the team's sum iterations")
  }

  private def createOutputVariable(team: Seq[KeyedVariable[Player, GaussianDistribution]]): Variable[GaussianDistribution] = {
    val sb = new StringBuilder()
    for (teamMember <- team) {
      sb.append(teamMember.key.toString())
      sb.append(", ")
    }
    sb.delete(sb.length() - 2, sb.length())

     Variable[GaussianDistribution](GaussianDistribution.UNIFORM, "Team[%s]'s performance", sb.toString())
  }
}