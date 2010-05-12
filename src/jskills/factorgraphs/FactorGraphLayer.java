package jskills.factorgraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FactorGraphLayer
<TParentFactorGraph extends FactorGraph<TParentFactorGraph>, 
TValue, 
TBaseVariable extends Variable<TValue>, 
TInputVariable extends Variable<TValue>, 
TFactor extends Factor<TValue>, 
TOutputVariable extends Variable<TValue>> 
    extends FactorGraphLayerBase<TValue> {

    private final List<TFactor> _LocalFactors = new ArrayList<TFactor>();
    private final List<List<TOutputVariable>> _OutputVariablesGroups = new ArrayList<List<TOutputVariable>>();
    private List<List<TInputVariable>> _InputVariablesGroups = new ArrayList<List<TInputVariable>>();

    protected FactorGraphLayer(TParentFactorGraph parentGraph)
    {
        ParentFactorGraph = parentGraph;
    }

    protected List<List<TInputVariable>> getInputVariablesGroups() {
        return _InputVariablesGroups;
    }

    // HACK

    public TParentFactorGraph ParentFactorGraph;
    public TParentFactorGraph getParentFactorGraph() { return ParentFactorGraph; }
    private void setParentFactorGraph( TParentFactorGraph parent ) { ParentFactorGraph = parent; }
    
    public List<List<TOutputVariable>> getOutputVariablesGroups(){
        return _OutputVariablesGroups;
    }

    public List<TFactor> getLocalFactors() {
        return _LocalFactors;
    }

    @Override
    public Collection<Factor<TValue>> getUntypedFactors() {
        return (Collection<Factor<TValue>>) _LocalFactors;
    }

    @Override
    public void SetRawInputVariablesGroups(Object value)
    {
        List<List<TInputVariable>> newList = (List<List<TInputVariable>>)value;
        _InputVariablesGroups = newList;
    }

    @Override
    public Object GetRawOutputVariablesGroups()
    {
        return _OutputVariablesGroups;
    }

    protected Schedule<TValue> ScheduleSequence(
        Collection<Schedule<TValue>> itemsToSequence,
        String nameFormat,
        Object... args)
    {
        String formattedName = String.format(nameFormat, args);
        return new ScheduleSequence<TValue>(formattedName, itemsToSequence);
    }

    protected void AddLayerFactor(TFactor factor)
    {
        _LocalFactors.add(factor);
    }
}