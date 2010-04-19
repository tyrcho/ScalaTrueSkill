package jskills.numerics;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static jskills.numerics.MathUtils.square;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class GaussianDistributionTests {
    private final double ErrorTolerance = 0.000001;

    @Test
    public void MultiplicationTests() {
        // I verified this against the formula at
        // http://www.tina-vision.net/tina-knoppix/tina-memo/2003-003.pdf
        GaussianDistribution standardNormal = new GaussianDistribution(0, 1);
        GaussianDistribution shiftedGaussian = new GaussianDistribution(2, 3);

        GaussianDistribution product = GaussianDistribution.mult(standardNormal, shiftedGaussian);

        assertEquals(0.2, product.getMean(), ErrorTolerance);
        assertEquals(3.0 / sqrt(10), product.getStandardDeviation(), ErrorTolerance);

        GaussianDistribution m4s5 = new GaussianDistribution(4, 5);
        GaussianDistribution m6s7 = new GaussianDistribution(6, 7);

        GaussianDistribution product2 = GaussianDistribution.mult(m4s5, m6s7);

        double expectedMean = (4 * square(7) + 6 * square(5)) / (square(5) + square(7));
        assertEquals(expectedMean, product2.getMean(), ErrorTolerance);

        double expectedSigma = sqrt(((square(5) * square(7)) / (square(5) + square(7))));
        assertEquals(expectedSigma, product2.getStandardDeviation(), ErrorTolerance);
    }

    @Test
    public void DivisionTests() {
        // Since the multiplication was worked out by hand, we use the same
        // numbers but work backwards
        GaussianDistribution product = new GaussianDistribution(0.2, 3.0 / sqrt(10));
        GaussianDistribution standardNormal = new GaussianDistribution(0, 1);

        GaussianDistribution productDividedByStandardNormal = GaussianDistribution.divide(product, standardNormal);
        assertEquals(2.0, productDividedByStandardNormal.getMean(), ErrorTolerance);
        assertEquals(3.0, productDividedByStandardNormal.getStandardDeviation(), ErrorTolerance);

        GaussianDistribution product2 = new GaussianDistribution((4 * square(7) + 6 * square(5)) / (square(5) + square(7)), sqrt(((square(5) * square(7)) / (square(5) + square(7)))));
        GaussianDistribution m4s5 = new GaussianDistribution(4, 5);
        GaussianDistribution product2DividedByM4S5 = GaussianDistribution.divide(product2, m4s5);
        assertEquals(6.0, product2DividedByM4S5.getMean(), ErrorTolerance);
        assertEquals(7.0, product2DividedByM4S5.getStandardDeviation(), ErrorTolerance);
    }

    @Test
    public void LogProductNormalizationTests() {
        GaussianDistribution m4s5 = new GaussianDistribution(4, 5);
        GaussianDistribution m6s7 = new GaussianDistribution(6, 7);

        GaussianDistribution product2 = GaussianDistribution.mult(m4s5, m6s7);
        double normConstant = 1.0 / (sqrt(2 * PI) * product2.getStandardDeviation());
        double lpn = GaussianDistribution.LogProductNormalization(m4s5, m6s7);

    }
}