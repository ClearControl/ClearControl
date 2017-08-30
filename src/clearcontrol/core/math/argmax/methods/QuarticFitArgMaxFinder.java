package clearcontrol.core.math.argmax.methods;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.ComputeFitError;
import clearcontrol.core.math.argmax.Fitting1DBase;
import clearcontrol.core.math.argmax.Fitting1DInterface;
import clearcontrol.core.math.argmax.UnivariateFunctionArgMax;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 * Quartic fit argmax finder.
 * 
 * Fits a order 4 polynomial and returns the argmax of the interpolated
 * function.
 *
 * @author royer
 */
public class QuarticFitArgMaxFinder extends Fitting1DBase implements
                                    ArgMaxFinder1DInterface,
                                    Fitting1DInterface
{
  private static final int cNumberOfSamples = 1024;
  private PolynomialFunction mPolynomialFunction;
  private PolynomialCurveFitter mPolynomialCurveFitter =
                                                       PolynomialCurveFitter.create(4);;

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    if (mPolynomialFunction == null)
      fit(pX, pY);

    double lArgMax =
                   UnivariateFunctionArgMax.argmax(pX,
                                                   mPolynomialFunction,
                                                   cNumberOfSamples);

    mPolynomialFunction = null;
    return lArgMax;
  }

  @Override
  public double[] fit(double[] pX, double[] pY)
  {
    WeightedObservedPoints lObservedPoints =
                                           new WeightedObservedPoints();

    for (int i = 0; i < pX.length; i++)
      lObservedPoints.add(pX[i], pY[i]);

    double[] lLastResults =
                          mPolynomialCurveFitter.fit(lObservedPoints.toList());

    mPolynomialFunction = new PolynomialFunction(lLastResults);

    double[] lFittedY = new double[pY.length];

    for (int i = 0; i < pX.length; i++)
      lFittedY[i] = mPolynomialFunction.value(pX[i]);

    mRMSD = ComputeFitError.rmsd(pY, lFittedY);

    return lFittedY;
  }

  @Override
  public String toString()
  {
    return String.format("QuarticFitArgMaxFinder [mPolynomialFunction=%s]",
                         mPolynomialFunction);
  }

}
