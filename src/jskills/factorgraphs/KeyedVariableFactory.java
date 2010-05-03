package jskills.factorgraphs;

public class KeyedVariableFactory<TKey, TValue> extends VariableFactory<TValue> {

    public KeyedVariableFactory(Func<TValue> variablePriorInitializer) {
        super(variablePriorInitializer);
    }
    
    public KeyedVariable<TKey, TValue> createKeyedVariable(TKey key, String nameFormat, Object... args) {
        return new KeyedVariable<TKey, TValue>(key, 
                                               String.format(nameFormat, args), 
                                               variablePriorInitializer.eval());
    }
}
