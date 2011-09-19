package jskills.factorgraphs
import scala.collection.mutable.ListBuffer

/**
 * Helper class for computing the factor graph's normalization constant.
 */
class FactorList[TValue] {
  val factors = ListBuffer.empty[Factor[TValue]]

  //  def getLogNormalization(): Double = {
  //    // TODO can these 3 loops be rolled into 1?
  //    factors foreach (_.resetMarginals)
  //
  //    var sumLogZ = 0.0
  //    for (f <- factors) {
  //      for (j <- 0 until f.getNumberOfMessages())
  //        sumLogZ += f.sendMessage(j)
  //    }
  //
  //    var sumLogS = 0.0
  //    //    for (f <- factors) sumLogS += f.getLogNormalization()
  //
  //     0.0 //sumLogZ + sumLogS
  //  }

  def size(): Int = factors.size

  def addFactor(factor: Factor[TValue]): Factor[TValue] = {
    factors += factor
     factor
  }
}