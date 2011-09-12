package jskills.trueskill.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jskills.factorgraphs.Factor;
import jskills.factorgraphs.Schedule;
import jskills.factorgraphs.ScheduleLoop;
import jskills.factorgraphs.ScheduleSequence;
import jskills.factorgraphs.ScheduleStep;
import jskills.factorgraphs.Variable;
import jskills.numerics.GaussianDistribution;
import jskills.trueskill.TrueSkillFactorGraph;
import jskills.trueskill.factors.GaussianWeightedSumFactor;

// The whole purpose of this is to do a loop on the bottom
class IteratedTeamDifferencesInnerLayer(parentGraph: TrueSkillFactorGraph,
  teamPerformancesToTeamPerformanceDifferencesLayer: TeamPerformancesToTeamPerformanceDifferencesLayer,
  teamDifferencesComparisonLayer: TeamDifferencesComparisonLayer)
  extends TrueSkillFactorGraphLayer[Variable[GaussianDistribution], GaussianWeightedSumFactor, Variable[GaussianDistribution]](parentGraph) {

  override def getUntypedFactors(): Collection[Factor[GaussianDistribution]] = {
    val factors = new ArrayList[Factor[GaussianDistribution]]() {
      val serialVersionUID = 6370771040490033445L; {
        addAll(teamPerformancesToTeamPerformanceDifferencesLayer.getUntypedFactors());
        addAll(teamDifferencesComparisonLayer.getUntypedFactors());
      }
    };

    return factors;
  }

  override def BuildLayer() {
    teamPerformancesToTeamPerformanceDifferencesLayer.SetRawInputVariablesGroups(getInputVariablesGroups());
    teamPerformancesToTeamPerformanceDifferencesLayer.BuildLayer();

    teamDifferencesComparisonLayer.SetRawInputVariablesGroups(
      teamPerformancesToTeamPerformanceDifferencesLayer.GetRawOutputVariablesGroups());
    teamDifferencesComparisonLayer.BuildLayer();
  }

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    var loop = (getInputVariablesGroups().size()) match {
      case 0 =>
        throw new IllegalArgumentException();
      case 1 =>
        throw new IllegalArgumentException();
      case 2 =>
        CreateTwoTeamInnerPriorLoopSchedule();
      case _ =>
        CreateMultipleTeamInnerPriorLoopSchedule();
    }

    // When dealing with differences, there are always (n-1) differences, so add in the 1
    val totalTeamDifferences = teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size();

    val schedules = new ArrayList[Schedule[GaussianDistribution]]();
    schedules.add(loop);
    schedules.add(new ScheduleStep[GaussianDistribution](
      "teamPerformanceToPerformanceDifferenceFactors[0] @ 1",
      teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(0), 1));
    schedules.add(new ScheduleStep[GaussianDistribution](
      format("teamPerformanceToPerformanceDifferenceFactors[teamTeamDifferences = %d - 1] @ 2",
        totalTeamDifferences),
      teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(totalTeamDifferences - 1), 2));

    return new ScheduleSequence[GaussianDistribution](
      "inner schedule", schedules);
  }

  private def CreateTwoTeamInnerPriorLoopSchedule(): Schedule[GaussianDistribution] = {
    var schedules = new ArrayList[Schedule[GaussianDistribution]]();
    schedules.add(new ScheduleStep[GaussianDistribution](
      "send team perf to perf differences",
      teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(0),
      0));
    schedules.add(new ScheduleStep[GaussianDistribution](
      "send to greater than or within factor",
      teamDifferencesComparisonLayer.getLocalFactors().get(0),
      0));
    return ScheduleSequence(schedules, "loop of just two teams inner sequence");
  }

  private def CreateMultipleTeamInnerPriorLoopSchedule(): Schedule[GaussianDistribution] = {
    val totalTeamDifferences = teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size();

    val forwardScheduleList = new ArrayList[Schedule[GaussianDistribution]]();

    for (i <- 0 until totalTeamDifferences - 1) {
      val schedules = new ArrayList[Schedule[GaussianDistribution]]();
      schedules.add(new ScheduleStep[GaussianDistribution](
        format("team perf to perf diff %d", i),
        teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(i), 0));
      schedules.add(new ScheduleStep[GaussianDistribution](
        format("greater than or within result factor %d", i),
        teamDifferencesComparisonLayer.getLocalFactors().get(i), 0));
      schedules.add(new ScheduleStep[GaussianDistribution](
        format("team perf to perf diff factors [%d], 2",
          i),
        teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(i), 2));
      val currentForwardSchedulePiece =
        ScheduleSequence(schedules, "current forward schedule piece %d", i.asInstanceOf[AnyRef]);

      forwardScheduleList.add(currentForwardSchedulePiece);
    }

    val forwardSchedule = new ScheduleSequence[GaussianDistribution](
      "forward schedule",
      forwardScheduleList);

    val backwardScheduleList = new ArrayList[Schedule[GaussianDistribution]]();

    for (i <- 0 until totalTeamDifferences - 1) {
      val schedules = new ArrayList[Schedule[GaussianDistribution]]();
      schedules.add(new ScheduleStep[GaussianDistribution](
        format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 0",
          i),
        teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(
          totalTeamDifferences - 1 - i), 0));
      schedules.add(new ScheduleStep[GaussianDistribution](
        format("greaterThanOrWithinResultFactors[totalTeamDifferences - 1 - %d] @ 0",
          i),
        teamDifferencesComparisonLayer.getLocalFactors().get(totalTeamDifferences - 1 - i), 0));
      schedules.add(new ScheduleStep[GaussianDistribution](
        format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 1",
          i),
        teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(
          totalTeamDifferences - 1 - i), 1));

      val currentBackwardSchedulePiece = new ScheduleSequence[GaussianDistribution](
        "current backward schedule piece", schedules);
      backwardScheduleList.add(currentBackwardSchedulePiece);
    }

    val backwardSchedule = new ScheduleSequence[GaussianDistribution](
      "backward schedule",
      backwardScheduleList);

    val schedules = new ArrayList[Schedule[GaussianDistribution]]();
    schedules.add(forwardSchedule);
    schedules.add(backwardSchedule);
    val forwardBackwardScheduleToLoop = new ScheduleSequence[GaussianDistribution](
      "forward Backward Schedule To Loop", schedules);

    val initialMaxDelta = 0.0001;

    val loop = new ScheduleLoop[GaussianDistribution](
      format("loop with max delta of %f",
        initialMaxDelta),
      forwardBackwardScheduleToLoop,
      initialMaxDelta);

    return loop;
  }
}