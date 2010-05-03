package jskills.factorgraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO Still necessary?
//public abstract class FactorGraphLayerBase<TValue>
//{
//    public abstract IEnumerable<Factor<TValue>> UntypedFactors { get; }
//    public abstract void BuildLayer();
//
//    public virtual Schedule<TValue> CreatePriorSchedule()
//    {
//        return null;
//    }
//
//    public virtual Schedule<TValue> CreatePosteriorSchedule()
//    {
//        return null;
//    }
//
//    // HACK
//
//    public abstract void SetRawInputVariablesGroups(Object value);
//    public abstract Object GetRawOutputVariablesGroups();
//}

public abstract class FactorGraphLayer<TValue, TParentGraph 
    extends FactorGraph<TParentGraph, TValue, Variable<TValue>>> {

    private final List<Factor<TValue>> localFactors = new ArrayList<Factor<TValue>>();
    private final List<List<Variable<TValue>>> outputVariablesGroups = new ArrayList<List<Variable<TValue>>>();
    private List<List<Variable<TValue>>> inputVariablesGroups = new ArrayList<List<Variable<TValue>>>();
    private final TParentGraph parentFactorGraph;
    
    
    protected FactorGraphLayer(TParentGraph parentGraph) {
        parentFactorGraph = parentGraph;
    }

    protected List<List<Variable<TValue>>> getInputVariablesGroups() {
        return inputVariablesGroups;
    }

    public TParentGraph getParentFactorGraph() { return parentFactorGraph; }

    // TODO Make copy?
    public List<List<Variable<TValue>>> getOutputVariablesGroups() {
        return outputVariablesGroups;
    }

    // TODO Make copy?
    public List<Factor<TValue>> getLocalFactors() {
        return localFactors;
    }

    // TODO Stop using raw stuff?
    public void SetRawInputVariablesGroups(Object value) {
        List<List<Variable<TValue>>> newList = (List<List<Variable<TValue>>>) value;
        inputVariablesGroups = newList;
    }

    // TODO Stop using raw stuff?
    public Object GetRawOutputVariablesGroups() {
        return outputVariablesGroups;
    }

    protected Schedule<TValue> ScheduleSequence(
        Collection<Schedule<TValue>> itemsToSequence,
        String nameFormat,
        Object... args) {
        String formattedName = String.format(nameFormat, args);
        return new ScheduleSequence<TValue, Schedule<TValue>>(formattedName, itemsToSequence);
    }

    protected void AddLayerFactor(Factor<TValue> factor) {
        localFactors.add(factor);
    }
}