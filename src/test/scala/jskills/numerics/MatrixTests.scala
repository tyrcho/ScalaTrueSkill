package jskills.numerics

import org.junit.Assert._

import org.ejml.data.SimpleMatrix
import org.junit.Test

class MatrixTests {
  val ERROR_TOLERANCE = 0.0000000000001

  @Test def TwoByTwoDeterminantTests() {
    val a = new SimpleMatrix(Array(Array[Double](1, 2), Array[Double](3, 4)))
    assertEquals(a.determinant(), -2., ERROR_TOLERANCE)

    val b = new SimpleMatrix(Array(Array[Double](3, 4), Array[Double](5, 6)))
    assertEquals(b.determinant(), -2., ERROR_TOLERANCE)

    val c = new SimpleMatrix(Array(Array[Double](1, 1), Array[Double](1, 1)))
    assertEquals(c.determinant(), 0., ERROR_TOLERANCE)

    val d = new SimpleMatrix(Array(Array[Double](12, 15), Array[Double](17, 21)))
    assertEquals(d.determinant(), 12 * 21 - 15 * 17, ERROR_TOLERANCE)
  }

  @Test def ThreeByThreeDeterminantTests() {
    val a = new SimpleMatrix(Array(Array[Double](1.0, 2, 3), Array[Double](4.0, 5, 6), Array[Double](7.0, 8, 9)))
    assertEquals(a.determinant(), 0., ERROR_TOLERANCE)

    val π = new SimpleMatrix(Array(Array[Double](3.0, 1, 4), Array[Double](1.0, 5, 9), Array[Double](2.0, 6, 5)))

    // Verified against http://www.wolframalpha.com/input/?i=determinant+%7B%7B3%2C1%2C4%7D%2C%7B1%2C5%2C9%7D%2C%7B2%2C6%2C5%7D%7D
    assertEquals(π.determinant(), -90, ERROR_TOLERANCE)
  }

  @Test def FourByFourDeterminantTests() {
    val a = new SimpleMatrix(Array(Array[Double](1, 2, 3, 4.0), Array[Double](5, 6.0, 7, 8), Array[Double](9.0, 10, 11, 12), Array[Double](13, 14.0, 15, 16)))

    assertEquals(a.determinant(), 0, ERROR_TOLERANCE)

    val π = new SimpleMatrix(Array(Array[Double](3.0, 1, 4, 1), Array[Double](5, 9.0, 2, 6), Array[Double](5.0, 3, 5, 8), Array[Double](9.0, 7, 9, 3)))

    // Verified against http://www.wolframalpha.com/input/?i=determinant+%7B+%7B3%2C1%2C4%2C1%7D%2C+%7B5%2C9%2C2%2C6%7D%2C+%7B5%2C3%2C5%2C8%7D%2C+%7B9%2C7%2C9%2C3%7D%7D
    assertEquals(π.determinant(), 98, ERROR_TOLERANCE)
  }

  @Test def EightByEightDeterminantTests() {
    val a = new SimpleMatrix(Array(Array[Double](1, 2, 3, 4, 5, 6, 7, 8), Array[Double](9, 10, 11, 12, 13, 14, 15, 16), Array[Double](17, 18, 19, 20, 21, 22, 23, 24), Array[Double](25, 26, 27, 28, 29, 30, 31, 32), Array[Double](33, 34, 35, 36, 37, 38, 39, 40), Array[Double](41, 42, 32, 44, 45, 46, 47, 48), Array[Double](49, 50, 51, 52, 53, 54, 55, 56), Array[Double](57, 58, 59, 60, 61, 62, 63, 64)))

    assertEquals(a.determinant(), 0, ERROR_TOLERANCE)

    val π = new SimpleMatrix(Array(Array[Double](3, 1, 4, 1, 5, 9, 2, 6), Array[Double](5, 3, 5, 8, 9, 7, 9, 3), Array[Double](2, 3, 8, 4, 6, 2, 6, 4), Array[Double](3, 3, 8, 3, 2, 7, 9, 5), Array[Double](0, 2, 8, 8, 4, 1, 9, 7), Array[Double](1, 6, 9, 3, 9, 9, 3, 7), Array[Double](5, 1, 0, 5, 8, 2, 0, 9), Array[Double](7, 4, 9, 4, 4, 5, 9, 2)))

    // Verified against http://www.wolframalpha.com/input/?i=det+%7B%7B3%2C1%2C4%2C1%2C5%2C9%2C2%2C6%7D%2C%7B5%2C3%2C5%2C8%2C9%2C7%2C9%2C3%7D%2C%7B2%2C3%2C8%2C4%2C6%2C2%2C6%2C4%7D%2C%7B3%2C3%2C8%2C3%2C2%2C7%2C9%2C5%7D%2C%7B0%2C2%2C8%2C8%2C4%2C1%2C9%2C7%7D%2C%7B1%2C6%2C9%2C3%2C9%2C9%2C3%2C7%7D%2C%7B5%2C1%2C0%2C5%2C8%2C2%2C0%2C9%7D%2C%7B7%2C4%2C9%2C4%2C4%2C5%2C9%2C2%7D%7D
    // And Mathematica, but ejml introduces roundoff error by using LUDecomposition
    assertEquals(π.determinant(), 1378143, 1e4 * ERROR_TOLERANCE)
  }

  @Test def EqualsTest() {
    val a = new SimpleMatrix(Array(Array[Double](1, 2), Array[Double](
      3, 4)))

    val b = new SimpleMatrix(Array(Array[Double](1, 2), Array[Double](
      3, 4)))

    assertTrue(a.isIdentical(b, ERROR_TOLERANCE))

    val c = new SimpleMatrix(Array(Array[Double](1, 2, 3), Array[Double](1, 2, 3), Array[Double](4, 5, 6)))

    val d = new SimpleMatrix(Array(Array[Double](1, 2, 3), Array[Double](1, 2, 3), Array[Double](4, 5, 6)))

    assertTrue(c.isIdentical(d, ERROR_TOLERANCE))

    val e = new SimpleMatrix(Array(Array[Double](1, 1, 4), Array[Double](2, 2, 5), Array[Double](3, 3, 6)))

    val f = e.transpose()
    assertTrue(d.isIdentical(f, ERROR_TOLERANCE))
    // TODO Doesn't work yet - fix if anyone is hashing SimpleMatrices.
    // http://code.google.com/p/efficient-java-matrix-library/issues/detail?id=4
    // assertEquals(d.hashCode(), f.hashCode()) 
  }

  @Test def InverseTests() {
    // see http://www.mathwords.com/i/inverse_of_a_SimpleMatrix.htm
    val a = new SimpleMatrix(Array(Array[Double](4, 3), Array[Double](3, 2)))

    val b = new SimpleMatrix(Array(Array[Double](-2, 3), Array[Double](3, -4)))

    val aInverse = a.invert()
    assertTrue(b.isIdentical(aInverse, ERROR_TOLERANCE))

    val identity2x2 = SimpleMatrix.identity(2)

    val aaInverse = a.mult(aInverse)
    assertTrue(aaInverse.isIdentical(identity2x2, ERROR_TOLERANCE))

    val c = new SimpleMatrix(Array(Array[Double](1, 2, 3), Array[Double](0, 4, 5), Array[Double](1, 0, 6)))

    val cInverse = c.invert()
    val d = new SimpleMatrix(Array(Array[Double](24, -12, -2), Array[Double](5, 3, -5), Array[Double](-4, 2, 4))).scale(1.0 / 22.)

    assertTrue(d.isIdentical(cInverse, ERROR_TOLERANCE))
    val identity3x3 = SimpleMatrix.identity(3)

    val ccInverse = c.mult(cInverse)
    assertTrue(ccInverse.isIdentical(identity3x3, ERROR_TOLERANCE))
  }
}