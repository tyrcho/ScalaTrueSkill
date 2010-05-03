package jskills.factorgraphs;

import lombok.Getter;

public class KeyedVariable<TKey, TValue> extends Variable<TValue> {

    @Getter private final TKey key;

    public KeyedVariable(TKey key, String name, TValue prior) {
        super(name, prior);
        this.key = key;
    }
}