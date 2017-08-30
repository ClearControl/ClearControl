package clearcontrol.core.math.argmax.methods;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.ComputeFitError;
import clearcontrol.core.math.argmax.Fitting1DBase;
import clearcontrol.core.math.argmax.Fitting1DInterface;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 * Parabola fit argmax finder.
 * 
 * Fits a parabola and returns the center as argmax.
 *
 * @author royer
 */
public class ParabolaFitArgMaxFinder extends Fitting1DBase implements
                                     ArgMaxFinder1DInterface,
                                     Fitting1DInterface
{

  private PolynomialCurveFitter mPolynomialCurveFitter;
  private PolynomialFunction mPolynomialFunction;

  /**
   * Parabola fit argmax finder.
   */
  public ParabolaFitArgMaxFinder()
  {
    this(1024);
  }

  /**
   * Instantiates a parabola fit argmax finder with a given maximal number of
   * iterations.
   * 
   * @param pMaxIterations
   *          max iterations
   */
  public ParabolaFitArgMaxFinder(int pMaxIterations)
  {
    mPolynomialCurveFitter =
                           PolynomialCurveFitter.create(2)
                                                .withMaxIterations(pMaxIterations);
  }

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    if (mPolynomialFunction == null)
      fit(pX, pY);

    double[] lCoefficients = mPolynomialFunction.getCoefficients();
    mPolynomialFunction = null;

    if (lCoefficients.length == 3)
    {
      double a = lCoefficients[2];
      double b = lCoefficients[1];

      double lArgMax = -b / (2 * a);

      return lArgMax;
    }
    else if (lCoefficients.length == 2)
    {
      double b = lCoefficients[1];

      if (b > 0)
        return pX[pX.length - 1];
      else
        return pX[0];

    }
    else if (lCoefficients.length == 1)
    {
      return null;
    }

    return null;
  }

  @Override
  public double[] fit(double[] pX, double[] pY)
  {
    WeightedObservedPoints lObservedPoints =
                                           new WeightedObservedPoints();

    for (int i = 0; i < pX.length; i++)
      lObservedPoints.add(pX[i], pY[i]);

    try
    {
      double[] lFitInfo =
                        mPolynomialCurveFitter.fit(lObservedPoints.toList());

      mPolynomialFunction = new PolynomialFunction(lFitInfo);

      double[] lFittedY = new double[pY.length];

      for (int i = 0; i < pX.length; i++)
        lFittedY[i] = mPolynomialFunction.value(pX[i]);

      mRMSD = ComputeFitError.rmsd(pY, lFittedY);

      return lFittedY;
    }
    catch (Throwable e)
    {
      // e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns last polynomial function fit.
   * 
   * @return last polynomial fit
   */
  public PolynomialFunction getFunction()
  {
    return mPolynomialFunction;
  }

  @Override
  public String toString()
  {
    return String.format("ParabolaFitArgMaxFinder [mPolynomialFunction=%s]",
                         mPolynomialFunction);
  }

}
