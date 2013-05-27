package jskills.factorgraphs
import scala.collection.mutable.ListBuffer

/**
 * Helper class for computing the factor graph's normalization constant.
 */
class FactorList[TValue] {
  val factors = ListBuffer.empty[Factor[TValue]]
  val size: Int = factors.size

  def addFactor(factor: Factor[TValue]): Factor[TValue] = {
    factors += factor
    factor
  }
}