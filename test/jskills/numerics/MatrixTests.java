using Moserware.Numerics;
using NUnit.Framework;

namespace UnitTests.Numerics
{
    [TestFixture]
    public class MatrixTests
    {
        @Test
        public void TwoByTwoDeterminantTests()
        {
            a = new SquareMatrix(1, 2,
                                     3, 4);
            assertEquals(-2, a.Determinant);

            b = new SquareMatrix(3, 4,
                                     5, 6);
            assertEquals(-2, b.Determinant);

            c = new SquareMatrix(1, 1,
                                     1, 1);
            assertEquals(0, c.Determinant);

            d = new SquareMatrix(12, 15,
                                     17, 21);
            assertEquals(12 * 21 - 15 * 17, d.Determinant);
        }

        @Test
        public void ThreeByThreeDeterminantTests()
        {
            a = new SquareMatrix(1, 2, 3,
                                     4, 5, 6,
                                     7, 8, 9);
            assertEquals(0, a.Determinant);

            π = new SquareMatrix(3, 1, 4,
                                     1, 5, 9,
                                     2, 6, 5);

            // Verified against http://www.wolframalpha.com/input/?i=determinant+%7B%7B3%2C1%2C4%7D%2C%7B1%2C5%2C9%7D%2C%7B2%2C6%2C5%7D%7D
            assertEquals(-90, π.Determinant);
        }

        @Test
        public void FourByFourDeterminantTests()
        {
            a = new SquareMatrix( 1,  2,  3,  4,
                                      5,  6,  7,  8,
                                      9, 10, 11, 12,
                                     13, 14, 15, 16);

            assertEquals(0, a.Determinant);

            π = new SquareMatrix(3, 1, 4, 1,
                                     5, 9, 2, 6,
                                     5, 3, 5, 8,
                                     9, 7, 9, 3);

            // Verified against http://www.wolframalpha.com/input/?i=determinant+%7B+%7B3%2C1%2C4%2C1%7D%2C+%7B5%2C9%2C2%2C6%7D%2C+%7B5%2C3%2C5%2C8%7D%2C+%7B9%2C7%2C9%2C3%7D%7D
            assertEquals(98, π.Determinant);
        }

        @Test
        public void EightByEightDeterminantTests()
        {
            a = new SquareMatrix( 1,   2,  3,  4,  5,  6,  7,  8,
                                      9,  10, 11, 12, 13, 14, 15, 16,
                                      17, 18, 19, 20, 21, 22, 23, 24, 
                                      25, 26, 27, 28, 29, 30, 31, 32,
                                      33, 34, 35, 36, 37, 38, 39, 40,
                                      41, 42, 32, 44, 45, 46, 47, 48,
                                      49, 50, 51, 52, 53, 54, 55, 56,
                                      57, 58, 59, 60, 61, 62, 63, 64);

            assertEquals(0, a.Determinant);

            π = new SquareMatrix(3, 1, 4, 1, 5, 9, 2, 6, 
                                     5, 3, 5, 8, 9, 7, 9, 3,
                                     2, 3, 8, 4, 6, 2, 6, 4, 
                                     3, 3, 8, 3, 2, 7, 9, 5,
                                     0, 2, 8, 8, 4, 1, 9, 7,
                                     1, 6, 9, 3, 9, 9, 3, 7,
                                     5, 1, 0, 5, 8, 2, 0, 9, 
                                     7, 4, 9, 4, 4, 5, 9, 2);

            // Verified against http://www.wolframalpha.com/input/?i=det+%7B%7B3%2C1%2C4%2C1%2C5%2C9%2C2%2C6%7D%2C%7B5%2C3%2C5%2C8%2C9%2C7%2C9%2C3%7D%2C%7B2%2C3%2C8%2C4%2C6%2C2%2C6%2C4%7D%2C%7B3%2C3%2C8%2C3%2C2%2C7%2C9%2C5%7D%2C%7B0%2C2%2C8%2C8%2C4%2C1%2C9%2C7%7D%2C%7B1%2C6%2C9%2C3%2C9%2C9%2C3%2C7%7D%2C%7B5%2C1%2C0%2C5%2C8%2C2%2C0%2C9%7D%2C%7B7%2C4%2C9%2C4%2C4%2C5%2C9%2C2%7D%7D
            assertEquals(1378143, π.Determinant);
        }

        @Test
        public void EqualsTest()
        {
            a = new SquareMatrix(1, 2,
                                     3, 4);

            b = new SquareMatrix(1, 2,
                                     3, 4);

            Assert.IsTrue(a == b);
            assertEquals(a, b);

            c = new Matrix(2, 3,
                               1, 2, 3,
                               4, 5, 6);

            d = new Matrix(2, 3,
                               1, 2, 3,
                               4, 5, 6);

            Assert.IsTrue(c == d);
            assertEquals(c, d);

            e = new Matrix(3, 2,
                               1, 4,
                               2, 5,
                               3, 6);

            f = e.Transpose;
            Assert.IsTrue(d == f);
            assertEquals(d, f);
            assertEquals(d.GetHashCode(), f.GetHashCode());

        }

        @Test
        public void AdjugateTests()
        {
            // From Wikipedia: http://en.wikipedia.org/wiki/Adjugate_matrix

            a = new SquareMatrix(1, 2,
                                     3, 4);

            b = new SquareMatrix( 4, -2,
                                     -3, 1);

            assertEquals(b, a.Adjugate);

            
            c = new SquareMatrix(-3,  2, -5,
                                     -1,  0, -2,
                                      3, -4,  1);

            d = new SquareMatrix(-8, 18, -4,
                                     -5, 12, -1,
                                      4, -6, 2);

            assertEquals(d, c.Adjugate);
        }

        @Test
        public void InverseTests()
        {
            // see http://www.mathwords.com/i/inverse_of_a_matrix.htm
            a = new SquareMatrix(4, 3,
                                     3, 2);

            b = new SquareMatrix(-2, 3,
                                      3, -4);

            aInverse = a.Inverse;
            assertEquals(b, aInverse);

            identity2x2 = new IdentityMatrix(2);

            aaInverse = a * aInverse;
            Assert.IsTrue(identity2x2 == aaInverse);

            c = new SquareMatrix(1, 2, 3,
                                     0, 4, 5,
                                     1, 0, 6);

            cInverse = c.Inverse;
            d = (1.0 / 22) * new SquareMatrix(24, -12, -2,
                                                   5,   3, -5,
                                                  -4,   2,  4);


            Assert.IsTrue(d == cInverse);
            identity3x3 = new IdentityMatrix(3);

            ccInverse = c * cInverse;
            Assert.IsTrue(identity3x3 == ccInverse);
        }
    }
}