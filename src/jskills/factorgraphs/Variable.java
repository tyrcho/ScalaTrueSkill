package jskills.factorgraphs;

import lombok.Getter;
import lombok.Setter;

public class Variable<TValue>
{
    private final String name;
    private final TValue prior;
    @Getter @Setter private TValue value;

    public Variable(String name, TValue prior) {
        this.name = "Variable[" + name + "]";
        this.prior = prior;
        resetToPrior();
    }

    public void resetToPrior() { value = prior; }

    @Override public String toString() { return name; }
}