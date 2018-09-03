package clearcontrol.core.math.argmax.fitprob.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

import clearcontrol.core.math.argmax.fitprob.GaussianFitQualityEstimator;
import clearcontrol.core.math.argmax.fitprob.RandomizedDataGaussianFitter;
import clearcontrol.core.math.argmax.test.ArgMaxTestsUtils;
import clearcontrol.core.units.OrderOfMagnitude;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.junit.Test;

/**
 * Fit quality estimator tests
 *
 * @author royer
 */
public class FitQualityEstimatorTests
{

  /**
   * Basic tests
   */
  @Test
  public void basicTest()
  {
    final GaussianFitQualityEstimator lFitQualityEstimator =
                                                           new GaussianFitQualityEstimator();

    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4, 5, 6 };
      final double[] lY = new double[]
      { 0, 2, 2, 7, 6, 1, 0 };

      final Double lPvalue = lFitQualityEstimator.probability(lX, lY);
      // System.out.format("p=%g \n", lPvalue);
      assertEquals(0.977873, lPvalue, 0.05);

    }

    {
      final double[] lX = new double[]
      { -2.0, -1.0, 0.0, 1.0, 2.0 };
      final double[] lY = new double[]
      { 3.71E-05, 3.80E-05, 3.86E-05, 3.86E-05, 3.79E-05 };

      final Double lPvalue = lFitQualityEstimator.probability(lX, lY);
      // System.out.format("p=%g \n", lPvalue);
      assertEquals(0.887846, lPvalue, 0.05);
    }

  }

  /**
   * Performance demo
   */
  @Test
  public void performancesTest()
  {
    final GaussianFitQualityEstimator lFitQualityEstimator =
                                                           new GaussianFitQualityEstimator();

    Median lMedian = new Median();
    double[] lMedianData = new double[100];

    for (int i = 0; i < lMedianData.length; i++)
    {
      final double[] lX = new double[]
      { 0, 1, 2, 3, 4, 5, 6 };
      final double[] lY = new double[]
      { 0, 2, 2, 7, 6, 1, 0 };

      final long lStart = System.nanoTime();
      final Double lPvalue = lFitQualityEstimator.probability(lX, lY);
      final long lStop = System.nanoTime();
      final double lElapsed = OrderOfMagnitude.nano2milli(
                                                          (1.0 * lStop
                                                           - lStart)
                                                          / 1);

      System.out.format("%g ms elapsed to find: p=%g \n",
                        lElapsed,
                        lPvalue);/**/

      lMedianData[i] = lElapsed;
      assertTrue(lPvalue >= 0.97);

    }

    double lMedianElapsedTime = lMedian.evaluate(lMedianData);

    assertTrue(lMedianElapsedTime <= 10);

    {
      final double[] lX = new double[]
      { -2.0, -1.0, 0.0, 1.0, 2.0 };
      final double[] lY = new double[]
      { 0.2, 0.4, 0.1, 0.2, 0.1 };

      double lPvalue = 0;
      final int lNumberOfIterations = 100;
      final long lStart = System.nanoTime();
      for (int i = 0; i < lNumberOfIterations; i++)
        lPvalue = lFitQualityEstimator.probability(lX, lY);
      final long lStop = System.nanoTime();
      final double lElapsed =
                            OrderOfMagnitude.nano2milli((1.0 * lStop
                                                         - 1.0
                                                           * lStart)
                                                        / lNumberOfIterations);

      // System.out.format("%g ms per estimation. \n", lElapsed);
      // System.out.println(lPvalue);

      assertEquals(0.6780, lPvalue, 0.05);
      assertTrue(lElapsed < 2);
    }

  }

  /**
   * Random data test
   */
  @Test
  public void randomDataTest()
  {
    final GaussianFitQualityEstimator lFitQualityEstimator =
                                                           new GaussianFitQualityEstimator();
    final double[] lX = new double[]
    { 0, 1, 2, 3, 4, 5, 6 };
    final double[] lY = new double[lX.length];
    final Random lRandom = new Random(System.nanoTime());

    for (int i = 0; i < 1024; i++)
    {

      RandomizedDataGaussianFitter.generateRandomVector(lRandom, lY);

      final Double lPvalue = lFitQualityEstimator.probability(lX, lY);

      assertTrue(lPvalue != null && lPvalue >= 0 && lPvalue <= 1);

      /*System.out.format(" p=%g \n", lPvalue);/**/

    }

  }

  /**
   * Benchmark test
   * 
   * @throws IOException
   *           N/A
   * @throws URISyntaxException
   *           N/A
   */
  @Test
  public void benchmark() throws IOException, URISyntaxException
  {
    final GaussianFitQualityEstimator lFitQualityEstimator =
                                                           new GaussianFitQualityEstimator();

    System.out.println("nofit:");
    run(lFitQualityEstimator,
        FitQualityEstimatorTests.class,
        "./benchmark/nofit.txt",
        9,
        true,
        0.80);

    System.out.println("fit:");
    run(lFitQualityEstimator,
        FitQualityEstimatorTests.class,
        "./benchmark/fit.txt",
        15,
        false,
        0.80);

  }

  private void run(GaussianFitQualityEstimator lGaussianFitEstimator,
                   Class<?> lContextClass,
                   String lRessource,
                   int lNumberOfDatasets,
                   boolean pBelow,
                   double pThreshold) throws IOException,
                                      URISyntaxException
  {
    for (int i = 0; i < lNumberOfDatasets; i++)
    {
      final TDoubleArrayList lY =
                                ArgMaxTestsUtils.loadData(lContextClass,
                                                          lRessource,
                                                          i);
      final TDoubleArrayList lX = new TDoubleArrayList();
      for (int j = 0; j < lY.size(); j++)
        lX.add(j);

      final Double lProbability =
                                lGaussianFitEstimator.probability(lX.toArray(),
                                                                  lY.toArray());
      final Double lRMSD = lGaussianFitEstimator.getRMSD();

      final double[] lFittedY =
                              lGaussianFitEstimator.getFit(lX.toArray(),
                                                           lY.toArray());

      System.out.println("__________________________________________________________________________");
      System.out.println("lX=" + Arrays.toString(lX.toArray()));
      System.out.println("lY=" + Arrays.toString(lY.toArray()));
      System.out.println("lFittedY=" + Arrays.toString(lFittedY));
      System.out.format("p=%g, rmsd=%g \n", lProbability, lRMSD);
      System.out.println("rmsd=" + lGaussianFitEstimator.getRMSD());

      if (pBelow)
        assertTrue(lProbability <= pThreshold);
      else
        assertTrue(lProbability >= pThreshold);

      /*
      Double lNRMSD = lGaussianFitEstimator.nrmsd(lX.toArray(),
      																							lY.toArray());/**/

      // System.out.println("lNRMSD=" + lNRMSD);
    }
  }
}
