package jskills.trueskill.layers

import jskills.factorgraphs.Factor
import jskills.factorgraphs.Schedule
import jskills.factorgraphs.ScheduleLoop
import jskills.factorgraphs.ScheduleSequence
import jskills.factorgraphs.ScheduleStep
import jskills.factorgraphs.Variable
import jskills.numerics.GaussianDistribution
import jskills.trueskill.TrueSkillFactorGraph
import jskills.trueskill.factors.GaussianWeightedSumFactor

import scala.collection.mutable.ListBuffer

// The whole purpose of this is to do a loop on the bottom
class IteratedTeamDifferencesInnerLayer(parentGraph: TrueSkillFactorGraph,
  teamPerformancesToTeamPerformanceDifferencesLayer: TeamPerformancesToTeamPerformanceDifferencesLayer,
  teamDifferencesComparisonLayer: TeamDifferencesComparisonLayer)
  extends TrueSkillFactorGraphLayer[Variable[GaussianDistribution], GaussianWeightedSumFactor, Variable[GaussianDistribution]](parentGraph) {

  override def getUntypedFactors(): Seq[Factor[GaussianDistribution]] =
    (teamPerformancesToTeamPerformanceDifferencesLayer.getUntypedFactors() ++
      teamDifferencesComparisonLayer.getUntypedFactors()).toSeq

  override def buildLayer() {
    teamPerformancesToTeamPerformanceDifferencesLayer.setRawInputVariablesGroups(inputVariablesGroups)
    teamPerformancesToTeamPerformanceDifferencesLayer.buildLayer()

    teamDifferencesComparisonLayer.setRawInputVariablesGroups(
      teamPerformancesToTeamPerformanceDifferencesLayer.getOutputVariablesGroups())
    teamDifferencesComparisonLayer.buildLayer()
  }

  override def createPriorSchedule(): Schedule[GaussianDistribution] = {
    var loop = (inputVariablesGroups.size) match {
      case 0 =>
        throw new IllegalArgumentException()
      case 1 =>
        throw new IllegalArgumentException()
      case 2 =>
        createTwoTeamInnerPriorLoopSchedule()
      case _ =>
        createMultipleTeamInnerPriorLoopSchedule()
    }

    // When dealing with differences, there are always (n-1) differences, so add in the 1
    val totalTeamDifferences = teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size

    val schedules = List(loop,
      new ScheduleStep[GaussianDistribution](
        "teamPerformanceToPerformanceDifferenceFactors[0] @ 1",
        teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(0), 1),
      new ScheduleStep[GaussianDistribution](
        format("teamPerformanceToPerformanceDifferenceFactors[teamTeamDifferences = %d - 1] @ 2",
          totalTeamDifferences),
        teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(totalTeamDifferences - 1), 2))

    return new ScheduleSequence[GaussianDistribution]("inner schedule", schedules)
  }

  private def createTwoTeamInnerPriorLoopSchedule(): Schedule[GaussianDistribution] = {
    var schedules = List(new ScheduleStep[GaussianDistribution](
      "send team perf to perf differences",
      teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(0),
      0),
      new ScheduleStep[GaussianDistribution](
        "send to greater than or within factor",
        teamDifferencesComparisonLayer.getLocalFactors()(0),
        0))
    return scheduleSequence(schedules, "loop of just two teams inner sequence")
  }

  private def createMultipleTeamInnerPriorLoopSchedule(): Schedule[GaussianDistribution] = {
    val totalTeamDifferences = teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size

    val forwardScheduleList = ListBuffer.empty[Schedule[GaussianDistribution]]

    for (i <- 0 until totalTeamDifferences - 1) {
      val schedules = List(
        new ScheduleStep[GaussianDistribution](
          format("team perf to perf diff %d", i),
          teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(i), 0),
        new ScheduleStep[GaussianDistribution](
          format("greater than or within result factor %d", i),
          teamDifferencesComparisonLayer.getLocalFactors()(i), 0),
        new ScheduleStep[GaussianDistribution](
          format("team perf to perf diff factors [%d], 2", i),
          teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(i), 2))
      val currentForwardSchedulePiece =
        scheduleSequence(schedules, "current forward schedule piece %s", i)

      forwardScheduleList += currentForwardSchedulePiece
    }

    val forwardSchedule = new ScheduleSequence[GaussianDistribution](
      "forward schedule",
      forwardScheduleList)

    val backwardScheduleList = ListBuffer.empty[Schedule[GaussianDistribution]]

    for (i <- 0 until totalTeamDifferences - 1) {
      val schedules = List(
        new ScheduleStep[GaussianDistribution](
          format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 0", i),
          teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(
            totalTeamDifferences - 1 - i), 0),
        new ScheduleStep[GaussianDistribution](
          format("greaterThanOrWithinResultFactors[totalTeamDifferences - 1 - %d] @ 0", i),
          teamDifferencesComparisonLayer.getLocalFactors()(totalTeamDifferences - 1 - i), 0),
        new ScheduleStep[GaussianDistribution](
          format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 1",
            i),
          teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors()(
            totalTeamDifferences - 1 - i), 1))

      val currentBackwardSchedulePiece = new ScheduleSequence[GaussianDistribution](
        "current backward schedule piece", schedules)
      backwardScheduleList += currentBackwardSchedulePiece
    }

    val backwardSchedule = new ScheduleSequence[GaussianDistribution](
      "backward schedule",
      backwardScheduleList)

    val schedules = List(forwardSchedule, backwardSchedule)
    val forwardBackwardScheduleToLoop = new ScheduleSequence[GaussianDistribution](
      "forward Backward Schedule To Loop", schedules)

    val initialMaxDelta = 0.0001

    val loop = new ScheduleLoop[GaussianDistribution](
      format("loop with max delta of %f",
        initialMaxDelta),
      forwardBackwardScheduleToLoop,
      initialMaxDelta)

    return loop
  }
}