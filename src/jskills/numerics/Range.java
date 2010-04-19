package jskills.numerics;

import lombok.Data;

/**
 * A very limited implementation of an immutable range of integers, including
 * endpoints. There is no such thing as an empty range.
 * <p>
 * The whole purpose of this class is to make the code for the
 * SkillCalculator(s) look a little cleaner
 * <p>
 * Derived classes can't use this class's static ctors they way they could in
 * C#, so I'm going to eschew the relative type safety afforded by Moser's
 * scheme and make this class final. A Range is a Range is a Range.
 */
@Data public final class Range { 

	private final int min;
	private final int max;

	public Range(int min, int max) {
        if (min > max) throw new IllegalArgumentException();

        this.min = min;
        this.max = max;
    }

    public boolean isInRange(int value) {
        return (min <= value) && (value <= max);
    }

    // REVIEW: It's probably bad form to have access statics via a derived
    // class, but the syntax looks better :-)

    // It's bad form in Java to the point where it won't compile. Using statics
    // through derived classes gets you a warning, but accessing generic types
    // (T) won't compile.  

    public static Range inclusive(int min, int max) {
        return new Range(min, max);
    }

    public static Range exactly(int value) {
        return new Range(value, value);
    }

    public static Range atLeast(int minimumValue) {
        return new Range(minimumValue, Integer.MAX_VALUE);
    }
}