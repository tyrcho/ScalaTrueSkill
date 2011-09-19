package jskills.numerics
/**
 * A very limited implementation of an immutable range of Integers, including
 * endpoints. There is no such thing as an empty range.
 * <p>
 * The whole purpose of this class is to make the code for the
 * SkillCalculator(s) look a little cleaner
 * <p>
 */
class Range(val min: Int, val max: Int) {
  def isInRange(value: Int): Boolean = (min <= value) && (value <= max)
  override def toString() = format("Range(min=%s, max=%s)", min, max)
}

object Range {
  def inclusive(min: Int, max: Int) = new Range(min, max)
  def exactly(value: Int) = new Range(value, value)
  def atLeast(minimumValue: Int) = new Range(minimumValue, Integer.MAX_VALUE)
}