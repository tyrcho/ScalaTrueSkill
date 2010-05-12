package jskills.factorgraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FactorGraphLayer<TParentFactorGraph extends FactorGraph<TParentFactorGraph>, TValue> 
    extends FactorGraphLayerBase<TValue> {

    protected final TParentFactorGraph parentFactorGraph;
    protected final List<Factor<TValue>> localFactors = new ArrayList<Factor<TValue>>();
    protected List<List<Variable<TValue>>> inputVariablesGroups = new ArrayList<List<Variable<TValue>>>();
    protected final List<List<Variable<TValue>>> outputVariablesGroups = new ArrayList<List<Variable<TValue>>>();
    
    protected FactorGraphLayer(TParentFactorGraph parentGraph) {
        parentFactorGraph = parentGraph;
    }

    public TParentFactorGraph getParentGraph() { return parentFactorGraph; }

    // TODO Make copy?
    public List<Factor<TValue>> getLocalFactors() {
        return localFactors;
    }

    // TODO Make copy?
    public List<List<Variable<TValue>>> getInputVariablesGroups() {
        return inputVariablesGroups;
    }

    // TODO Make copy?
    public List<List<Variable<TValue>>> getOutputVariablesGroups() {
        return outputVariablesGroups;
    }

    // TODO Stop using raw stuff?
    public void SetRawInputVariablesGroups(List<List<Variable<TValue>>> value) {
        inputVariablesGroups = new ArrayList<List<Variable<TValue>>>(value);
    }

    protected Schedule<TValue> ScheduleSequence(
            Collection<Schedule<TValue>> itemsToSequence, 
            String nameFormat,
            Object... args) {

        String formattedName = String.format(nameFormat, args);
        return new ScheduleSequence<TValue>(formattedName, itemsToSequence);
    }

    protected void AddLayerFactor(Factor<TValue> factor) {
        localFactors.add(factor);
    }
    
    // TODO Abstractify these?
    public Schedule<TValue> CreatePriorSchedule() { return null; }

    public Schedule<TValue> CreatePosteriorSchedule() { return null; }
    
    public void buildLayer() {}
}