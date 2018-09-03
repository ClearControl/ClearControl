package clearcontrol.core.math.argmax.methods;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.ComputeFitError;
import clearcontrol.core.math.argmax.Fitting1DBase;
import clearcontrol.core.math.argmax.Fitting1DInterface;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 * Gaussian fit argmax finder.
 * 
 * Fits a Gaussian and returns the Gaussian mean center as argmax.
 *
 * @author royer
 */
public class GaussianFitArgMaxFinder extends Fitting1DBase implements
                                     ArgMaxFinder1DInterface,
                                     Fitting1DInterface
{

  private double mLastMean;
  private GaussianCurveFitter mGaussianCurveFitter;
  protected Gaussian mGaussian;

  /**
   * Instantiates an argmax finder.
   * 
   */
  public GaussianFitArgMaxFinder()
  {
    this(1024);
  }

  /**
   * Instantiates an argmax finder with a given number of iterations.
   * 
   * @param pMaxIterations
   *          max iterations
   */
  public GaussianFitArgMaxFinder(int pMaxIterations)
  {
    super();
    mGaussianCurveFitter =
                         GaussianCurveFitter.create()
                                            .withMaxIterations(pMaxIterations);
  }

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    if (mGaussian == null)
      if (fit(pX, pY) == null)
        return null;

    mGaussian = null;
    return mLastMean;
  }

  @Override
  public double[] fit(double[] pX, double[] pY)
  {
    WeightedObservedPoints lObservedPoints =
                                           new WeightedObservedPoints();

    for (int i = 0; i < pX.length; i++)
      lObservedPoints.add(pX[i], pY[i]);

    mGaussian = null;

    try
    {
      double[] lFitInfo =
                        mGaussianCurveFitter.fit(lObservedPoints.toList());
      // System.out.println(Arrays.toString(lFitInfo));

      double lNorm = lFitInfo[0];
      double lMean = lFitInfo[1];
      double lSigma = lFitInfo[2];

      mLastMean = lMean;

      mGaussian = new Gaussian(lNorm, lMean, lSigma);

      double[] lFittedY = new double[pY.length];

      for (int i = 0; i < pX.length; i++)
        lFittedY[i] = mGaussian.value(pX[i]);

      mRMSD = ComputeFitError.rmsd(pY, lFittedY);

      return lFittedY;
    }
    catch (Throwable e)
    {
      return null;
    }

  }

  /**
   * Returns Gaussian function for last fit.
   * 
   * @return last Gaussian fit function
   */
  public Gaussian getFunction()
  {
    return mGaussian;
  }

  @Override
  public String toString()
  {
    return String.format("GaussianFitArgMaxFinder [mLastMean=%s, mGaussian=%s]",
                         mLastMean,
                         mGaussian);
  }

}
