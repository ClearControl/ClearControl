package clearcontrol.core.math.argmax.fitprob;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Random;

import clearcontrol.core.math.argmax.methods.GaussianFitArgMaxFinder;
import clearcontrol.core.math.argmax.methods.ParabolaFitArgMaxFinder;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

/**
 * Randomized data Gaussian Fitter
 *
 * @author royer
 */
public class RandomizedDataGaussianFitter
{
  private static final int cMaxIterationsForRandomizedDataFitting =
                                                                  512;

  GaussianFitArgMaxFinder mGaussianFitArgMaxFinder =
                                                   new GaussianFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting);
  ParabolaFitArgMaxFinder mParabolaFitArgMaxFinder =
                                                   new ParabolaFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting
                                                                               / 2);

  private Random mRandom = new Random(System.nanoTime());
  private double[] mX;
  private double[] mY;
  private UnivariateDifferentiableFunction mUnivariateDifferentiableFunction;

  /**
   * Instantiates a randomized data Gaussian fitter
   */
  public RandomizedDataGaussianFitter()
  {
  }

  /**
   * Instantiates a randomized data gaussian fitter with a given (X,Y) pair.
   * 
   * @param pX
   *          x data
   * @param pY
   *          y data
   */
  public RandomizedDataGaussianFitter(double[] pX, double[] pY)
  {
    mX = pX;
    mY = pY;
  }

  /**
   * Computes RMSD for random data on a given X
   * 
   * @param pX
   *          x data to use
   * @return RMSD for a given X and random Y
   */
  public Double computeRMSDForRandomData(double[] pX)
  {
    double[] lRandomY = generateRandomVector(mRandom,
                                             new double[pX.length]);
    return computeRMSD(pX, lRandomY);
  }

  /**
   * Computes the RMSD for the (X,Y) pair (given at construction time)
   * 
   * @return RMSD
   * @throws Exception
   *           thrown if exception occurs during concurrent execution
   */
  public Double computeRMSD() throws Exception
  {
    return computeRMSD(mX, mY);
  }

  /**
   * Returns the fitted function
   * 
   * @return fitted function
   */
  public UnivariateDifferentiableFunction getFunction()
  {
    return mUnivariateDifferentiableFunction;
  }

  private Double computeRMSD(double[] pX, double[] pY)
  {
    Double lRMSD = fitGaussian(pX, pY);
    if (lRMSD == null)
      lRMSD = fitparabola(pX, pY);
    return lRMSD;
  }

  private Double fitparabola(double[] pX, double[] pY)
  {

    try
    {
      double[] lFit = mParabolaFitArgMaxFinder.fit(pX, pY);
      if (lFit == null)
        return null;
      setFunction(mParabolaFitArgMaxFinder.getFunction());
      double lRMSD = mParabolaFitArgMaxFinder.getRMSD();

      double[] lCoefficients =
                             mParabolaFitArgMaxFinder.getFunction()
                                                     .getCoefficients();

      if (lCoefficients.length == 1)
        return null;
      if (lCoefficients.length == 3)
      {
        double a = lCoefficients[2];
        if (a > 0)
          return null;
      }

      return lRMSD;
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      return null;
    } /**/
  }

  private Double fitGaussian(double[] pX, double[] pY)
  {
    try
    {
      double[] lFit = mGaussianFitArgMaxFinder.fit(pX, pY);
      if (lFit == null)
        return null;
      setFunction(mGaussianFitArgMaxFinder.getFunction());

      double lRMSD = mGaussianFitArgMaxFinder.getRMSD();
      return lRMSD;
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      return null;
    }
  }

  private void setFunction(UnivariateDifferentiableFunction pUnivariateDifferentiableFunction)
  {
    mUnivariateDifferentiableFunction =
                                      pUnivariateDifferentiableFunction;
  }

  /**
   * Generates a random vector of doubles between 0 and 1.
   * 
   * @param pRandom
   *          random object
   * @param pArray
   *          array to store random doubles
   * @return given array
   */
  public static double[] generateRandomVector(Random pRandom,
                                              double[] pArray)
  {
    for (int i = pArray.length - 1; i > 0; i--)
    {
      pArray[i] = pRandom.nextDouble();
    }

    normalizeInPlace(pArray);

    return pArray;
  }

  /**
   * Returns a normalized copy of the given array.
   * 
   * @param pY
   *          y data
   * @return normalized copy
   */
  public static double[] normalizeCopy(double[] pY)
  {
    double[] lNormY = new double[pY.length];
    double lMin = Double.POSITIVE_INFINITY;
    double lMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < pY.length; i++)
    {
      lMin = min(lMin, pY[i]);/**/
      lMax = max(lMax, pY[i]);/**/
    }

    for (int i = 0; i < pY.length; i++)
    {
      final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
      lNormY[i] = lScaledValue;
    }
    return lNormY;
  }

  /**
   * Returns a normalized copy of the given array.
   * 
   * @param pY
   *          y data
   * @return normalized in place
   */
  public static double[] normalizeInPlace(double[] pY)
  {
    double lMin = Double.POSITIVE_INFINITY;
    double lMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < pY.length; i++)
    {
      lMin = min(lMin, pY[i]);/**/
      lMax = max(lMax, pY[i]);/**/
    }

    for (int i = 0; i < pY.length; i++)
    {
      final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
      pY[i] = lScaledValue;
    }
    return pY;
  }

}
