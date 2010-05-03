package jskills.factorgraphs;

import static java.lang.Math.max;

import java.util.Collection;

public class ScheduleSequence<TValue, TSchedule extends Schedule<TValue>>
        extends Schedule<TValue> {
    private final Collection<TSchedule> schedules;

    public ScheduleSequence(String name, Collection<TSchedule> schedules) {
        super(name);
        this.schedules = schedules;
    }

    @Override
    public double visit(int depth, int maxDepth) {
        double maxDelta = 0;

        for (TSchedule schedule : schedules)
            maxDelta = max(schedule.visit(depth + 1, maxDepth), maxDelta);

        return maxDelta;
    }
}
