package clearcontrol.core.math.argmax.methods;

import static java.lang.Math.max;
import static java.lang.Math.min;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;

/**
 * Clamping ArgMax finder.
 * 
 * Clamping argmax finder. Wraps a 1D argmax finder and enforces that the argmax
 * be within [min(X),max(X)]
 *
 * @author royer
 */
public class ClampingArgMaxFinder implements ArgMaxFinder1DInterface
{

  private ArgMaxFinder1DInterface mDelegatedArgMaxFinder1D;

  /**
   * Instantiates a clamping argmax finder.
   * 
   * @param pArgMaxFinder1DInterface
   *          delegated argmax finder
   */
  public ClampingArgMaxFinder(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
  {
    super();
    mDelegatedArgMaxFinder1D = pArgMaxFinder1DInterface;
  }

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    Double lArgmax = mDelegatedArgMaxFinder1D.argmax(pX, pY);

    if (lArgmax == null)
      return null;

    lArgmax = min(lArgmax, pX[pX.length - 1]);
    lArgmax = max(lArgmax, pX[0]);

    return lArgmax;
  }

  @Override
  public String toString()
  {
    return String.format("ClampingArgMaxFinder [%s]",
                         mDelegatedArgMaxFinder1D);
  }

}
