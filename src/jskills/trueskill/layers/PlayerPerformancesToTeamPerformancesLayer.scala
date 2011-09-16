package jskills.trueskill.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jskills.IPlayer;
import jskills.PartialPlay;
import jskills.factorgraphs.KeyedVariable;
import jskills.factorgraphs.Schedule;
import jskills.factorgraphs.ScheduleStep;
import jskills.factorgraphs.Variable;
import jskills.numerics.GaussianDistribution;
import jskills.trueskill.TrueSkillFactorGraph;
import jskills.trueskill.factors.GaussianWeightedSumFactor;
import collection.JavaConversions._

class PlayerPerformancesToTeamPerformancesLayer(parentGraph: TrueSkillFactorGraph)
  extends TrueSkillFactorGraphLayer[KeyedVariable[IPlayer, GaussianDistribution], GaussianWeightedSumFactor, Variable[GaussianDistribution]](parentGraph) {

  override def BuildLayer() {
    for (currentTeam <- inputVariablesGroups) {
      val teamPerformance = CreateOutputVariable(currentTeam);
      AddLayerFactor(createPlayerToTeamSumFactor(currentTeam,
        teamPerformance));

      // REVIEW: Does it make sense to have groups of one?
      addOutputVariable(teamPerformance);
    }
  }

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = new ArrayList[Schedule[GaussianDistribution]]();
    for (weightedSumFactor <- localFactors) {
      schedules.add(new ScheduleStep[GaussianDistribution](
        "Perf to Team Perf Step", weightedSumFactor, 0));
    }
    return ScheduleSequence(schedules,
      "all player perf to team perf schedule");
  }

  def createPlayerToTeamSumFactor(teamMembers: List[KeyedVariable[IPlayer, GaussianDistribution]], sumVariable: Variable[GaussianDistribution]): GaussianWeightedSumFactor = {
    val weights = new Array[Double](teamMembers.size());
    for (i <- 0 until weights.length) {
      weights(i) = PartialPlay.getPartialPlayPercentage(teamMembers
        .get(i).getKey());
    }
    return new GaussianWeightedSumFactor(sumVariable, teamMembers, weights);
  }

  override def createPosteriorSchedule(): Schedule[GaussianDistribution] = {
    val schedules = new ArrayList[Schedule[GaussianDistribution]]();
    for (currentFactor <- localFactors) {
      // TODO is there an off by 1 error here?
      for (i <- 0 until currentFactor.getNumberOfMessages()) {
        schedules.add(new ScheduleStep[GaussianDistribution](
          "team sum perf @" + i, currentFactor, i));
      }
    }
    return ScheduleSequence(schedules, "all of the team's sum iterations");
  }

  private def CreateOutputVariable(team: List[KeyedVariable[IPlayer, GaussianDistribution]]): Variable[GaussianDistribution] = {
    val sb = new StringBuilder();
    for (teamMember <- team) {
      sb.append(teamMember.getKey().toString());
      sb.append(", ");
    }
    sb.delete(sb.length() - 2, sb.length());

    return new Variable[GaussianDistribution](GaussianDistribution.UNIFORM, "Team[%s]'s performance", sb.toString());
  }
}