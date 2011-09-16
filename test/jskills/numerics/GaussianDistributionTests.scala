package jskills.numerics;

import org.junit.Test;
import org.junit.Assert._;
import java.lang.Math.sqrt;
import MathUtils.square

class GaussianDistributionTests {
  val ErrorTolerance = 0.000001;

  @Test
  def CumulativeToTests() {
    // Verified with WolframAlpha
    // (e.g.
    // http://www.wolframalpha.com/input/?i=CDF%5BNormalDistribution%5B0%2C1%5D%2C+0.5%5D
    // )
    assertEquals(0.691462, GaussianDistribution.cumulativeTo(0.5), ErrorTolerance);
  }

  @Test
  def AtTests() {
    // Verified with WolframAlpha
    // (e.g.
    // http://www.wolframalpha.com/input/?i=PDF%5BNormalDistribution%5B0%2C1%5D%2C+0.5%5D
    // )
    assertEquals(0.352065, GaussianDistribution.at(0.5), ErrorTolerance);
  }

  @Test
  def MultiplicationTests() {
    // I verified this against the formula at
    // http://www.tina-vision.net/tina-knoppix/tina-memo/2003-003.pdf
    val standardNormal = new GaussianDistribution(0, 1);
    val shiftedGaussian = new GaussianDistribution(2, 3);

    val product = GaussianDistribution.prod(
      standardNormal, shiftedGaussian);

    assertEquals(0.2, product.getMean(), ErrorTolerance);
    assertEquals(3.0 / sqrt(10), product.getStandardDeviation(),
      ErrorTolerance);

    val m4s5 = new GaussianDistribution(4, 5);
    val m6s7 = new GaussianDistribution(6, 7);

    val product2 = GaussianDistribution.prod(m4s5, m6s7);

    val expectedMean = (4 * square(7) + 6 * square(5)) / (square(5) + square(7));
    assertEquals(expectedMean, product2.getMean(), ErrorTolerance);

    val expectedSigma = sqrt(((square(5) * square(7)) / (square(5) + square(7))));
    assertEquals(expectedSigma, product2.getStandardDeviation(),
      ErrorTolerance);
  }

  @Test def DivisionTests() {
    // Since the multiplication was worked out by hand, we use the same
    // numbers but work backwards
    val product = new GaussianDistribution(0.2,
      3.0 / sqrt(10));
    val standardNormal = new GaussianDistribution(0, 1);

    val productDividedByStandardNormal = GaussianDistribution.divide(product, standardNormal);
    assertEquals(2.0, productDividedByStandardNormal.getMean(),
      ErrorTolerance);
    assertEquals(3.0,
      productDividedByStandardNormal.getStandardDeviation(),
      ErrorTolerance);

    val product2 = new GaussianDistribution(
      (4 * square(7) + 6 * square(5)) / (square(5) + square(7)),
      sqrt(((square(5) * square(7)) / (square(5) + square(7)))));
    val m4s5 = new GaussianDistribution(4, 5);
    val product2DividedByM4S5 = GaussianDistribution.divide(product2, m4s5);
    assertEquals(6.0, product2DividedByM4S5.getMean(), ErrorTolerance);
    assertEquals(7.0, product2DividedByM4S5.getStandardDeviation(),
      ErrorTolerance);
  }

  @Test def LogProductNormalizationTests() {
    // Verified with Ralf Herbrich's F# implementation
    val standardNormal = new GaussianDistribution(0, 1);
    val lpn = GaussianDistribution.logProductNormalization(
      standardNormal, standardNormal);
    assertEquals(-1.2655121234846454, lpn, ErrorTolerance);

    val m1s2 = new GaussianDistribution(1, 2);
    val m3s4 = new GaussianDistribution(3, 4);
    val lpn2 = GaussianDistribution.logProductNormalization(m1s2, m3s4);
    assertEquals(-2.5168046699816684, lpn2, ErrorTolerance);
  }

  @Test def LogRatioNormalizationTests() {
    // Verified with Ralf Herbrich's F# implementation
    val m1s2 = new GaussianDistribution(1, 2);
    val m3s4 = new GaussianDistribution(3, 4);
    val lrn = GaussianDistribution.logRatioNormalization(m1s2, m3s4);
    assertEquals(2.6157405972171204, lrn, ErrorTolerance);
  }

  @Test def AbsoluteDifferenceTests() {
    // Verified with Ralf Herbrich's F# implementation
    val standardNormal = new GaussianDistribution(0, 1);
    val absDiff = GaussianDistribution.absoluteDifference(standardNormal, standardNormal);
    assertEquals(0.0, absDiff, ErrorTolerance);

    val m1s2 = new GaussianDistribution(1, 2);
    val m3s4 = new GaussianDistribution(3, 4);
    val absDiff2 = GaussianDistribution.absoluteDifference(m1s2, m3s4);
    assertEquals(0.4330127018922193, absDiff2, ErrorTolerance);
  }
}