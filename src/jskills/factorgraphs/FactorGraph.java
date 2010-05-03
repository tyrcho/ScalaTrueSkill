package jskills.factorgraphs;

import lombok.Getter;

public abstract class FactorGraph<TSelf extends FactorGraph<TSelf, TValue, TVariable>, TValue, TVariable extends Variable<TValue>> {
    @Getter protected VariableFactory<TValue> variableFactory;
    
    // TODO Add constructor?
}