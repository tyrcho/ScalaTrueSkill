package jskills

/**
 * Verifies argument contracts.
 * <p>
 * These are used until I figure out how to do this better in Java
 */
object Guard {
  def argumentNotNull(value: Any, parameterName: String) {
    if (value == null) throw new NullPointerException(parameterName)
  }

  def argumentIsValidIndex(index: Int, count: Int, parameterName: String) {
    if ((index < 0) || (index >= count)) 
      throw new IndexOutOfBoundsException(parameterName)
  }

  def argumentInRangeInclusive(value: Double, min: Double, max: Double, parameterName: String) {
    if ((value < min) || (value > max)) 
      throw new IndexOutOfBoundsException(parameterName)
  }
}