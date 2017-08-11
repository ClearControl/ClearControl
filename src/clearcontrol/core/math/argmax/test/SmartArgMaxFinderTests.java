package clearcontrol.core.math.argmax.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import clearcontrol.core.math.argmax.SmartArgMaxFinder;
import clearcontrol.core.units.OrderOfMagnitude;

import org.junit.Test;

/**
 * Smart argmax finder tests
 *
 * @author royer
 */
public class SmartArgMaxFinderTests
{

  /**
   * Basic test
   */
  @Test
  public void basicTest()
  {
    final SmartArgMaxFinder lSmartArgMaxFinder =
                                               new SmartArgMaxFinder();

    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4 };
      final double[] lY = new double[]
      { 1, 2, 3, 4, 5 };

      final Double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

      final double[] lFittedY = lSmartArgMaxFinder.fit(lX, lY);

      System.out.println(Arrays.toString(lX));
      System.out.println(Arrays.toString(lY));
      System.out.println(Arrays.toString(lFittedY));

      System.out.println(lArgmax);

      assertEquals(4, lArgmax, 0.01);
    }

    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4, 5, 6 };
      final double[] lY = new double[]
      { 0, 2, 2, 7, 6, 1, 0 };

      final Double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

      final double[] lFittedY = lSmartArgMaxFinder.fit(lX, lY);

      System.out.println(Arrays.toString(lX));
      System.out.println(Arrays.toString(lY));
      System.out.println(Arrays.toString(lFittedY));

      System.out.println(lArgmax);

      assertEquals(3.28, lArgmax, 0.01);
    }

    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4, 5, 6 };
      final double[] lY = new double[]
      { 1.542E-4,
        1.547E-4,
        1.555E-4,
        1.557E-4,
        1.556E-4,
        1.545E-4,
        1.547E-4 };

      final Double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

      final double[] lFittedY = lSmartArgMaxFinder.fit(lX, lY);

      System.out.println(Arrays.toString(lX));
      System.out.println(Arrays.toString(lY));
      System.out.println(Arrays.toString(lFittedY));

      System.out.println(lArgmax);

      assertEquals(3.42, lArgmax, 0.02);
    }

    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4, 5, 6 };
      final double[] lY = new double[]
      { 1.504E-4,
        1.506E-4,
        1.517E-4,
        1.513E-4,
        1.519E-4,
        1.515E-4,
        1.498E-4 };

      final Double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

      final double[] lFittedY = lSmartArgMaxFinder.fit(lX, lY);

      System.out.println(Arrays.toString(lX));
      System.out.println(Arrays.toString(lY));
      System.out.println(Arrays.toString(lFittedY));

      System.out.println(lArgmax);

      assertEquals(4.31, lArgmax, 0.1);
    }

  }

  /**
   * Performance test
   */
  @Test
  public void performanceTest()
  {
    final SmartArgMaxFinder lSmartArgMaxFinder =
                                               new SmartArgMaxFinder();

    for (int j = 0; j < 10; j++)
    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4 };
      final double[] lY = new double[]
      { 0.11, 0.21, 0.3, 0.19, 0.09 };
      double[] lFittedY = null;
      Double lArgmax = null;

      final int lNumberOfIterations = 100;
      final long lStart = System.nanoTime();
      for (int i = 0; i < lNumberOfIterations; i++)
      {
        lArgmax = lSmartArgMaxFinder.argmax(lX, lY);
        lFittedY = lSmartArgMaxFinder.fit(lX, lY);
      }
      final long lStop = System.nanoTime();
      final double lElapsed =
                            OrderOfMagnitude.nano2milli((1.0 * lStop
                                                         - lStart)
                                                        / lNumberOfIterations);

      // System.out.format("%g ms per estimation. \n", lElapsed);

      // System.out.println(Arrays.toString(lX));
      // System.out.println(Arrays.toString(lY));
      // System.out.println(Arrays.toString(lFittedY));

      // System.out.println(lArgmax);

      if (j > 5)
        assertTrue(lElapsed < 3);

      assertTrue(lFittedY[2] > 0.30);

      assertEquals(2, lArgmax, 0.15);
    }
  }

  /**
   * Benchmark
   * 
   * @throws IOException
   *           N/A
   * @throws URISyntaxException
   *           N/A
   */
  @Test
  public void benchmark() throws IOException, URISyntaxException
  {

    final SmartArgMaxFinder lSmartArgMaxFinder =
                                               new SmartArgMaxFinder();
    final double lMaxError = ArgMaxTestsUtils.test(lSmartArgMaxFinder,
                                                   15);
    assertEquals(0, lMaxError, 1);

  }

  /**
   * Benchmark with fit estimation
   * 
   * @throws IOException
   *           N/A
   * @throws URISyntaxException
   *           N/A
   */
  @Test
  public void benchmarkWithFitEstimation() throws IOException,
                                           URISyntaxException
  {
    final SmartArgMaxFinder lSmartArgMaxFinder =
                                               new SmartArgMaxFinder();
    final double lMaxError = ArgMaxTestsUtils.test(lSmartArgMaxFinder,
                                                   8);
    assertEquals(0, lMaxError, 0.7);

  }

  /**
   * Regression test bug
   * 
   * @throws IOException
   *           N/A
   * @throws URISyntaxException
   *           N/A
   */
  @Test
  public void regressionTestBug() throws IOException,
                                  URISyntaxException
  {
    final double[] lX = new double[]
    { -4.000E+02,
      -3.600E+02,
      -3.200E+02,
      -2.800E+02,
      -2.400E+02,
      -2.000E+02,
      -1.600E+02,
      -1.200E+02,
      -8.000E+01,
      -4.000E+01,
      0.000E+00,
      4.000E+01,
      8.000E+01,
      1.200E+02,
      1.600E+02,
      2.000E+02,
      2.400E+02,
      2.800E+02,
      3.200E+02,
      3.600E+02,
      4.000E+02 };

    final double[] lY = new double[]
    { 3.173E-04,
      3.148E-04,
      3.138E-04,
      3.164E-04,
      3.188E-04,
      3.194E-04,
      3.199E-04,
      3.219E-04,
      3.233E-04,
      3.255E-04,
      3.269E-04,
      3.299E-04,
      3.297E-04,
      3.289E-04,
      3.298E-04,
      3.317E-04,
      3.316E-04,
      3.304E-04,
      3.316E-04,
      3.322E-04,
      3.321E-04 };

    final SmartArgMaxFinder lSmartArgMaxFinder =
                                               new SmartArgMaxFinder();

    final Double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

    assertTrue(lArgmax > 380);

  }

}
