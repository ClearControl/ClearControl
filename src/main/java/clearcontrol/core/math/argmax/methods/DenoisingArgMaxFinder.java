package clearcontrol.core.math.argmax.methods;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;

/**
 * Denoising argmax finder. It wraps an argmax finder and cleanses up the input
 * data (X,Y) by smoothing Y.
 * 
 * 
 *
 * @author royer
 */
public class DenoisingArgMaxFinder implements ArgMaxFinder1DInterface
{

  private final ArgMaxFinder1DInterface mDelegatedArgMaxFinder1D;

  /**
   * Instantiates a denoising argmax finder given a delegated argmax finder.
   * 
   * @param pArgMaxFinder1D
   *          delegated 1D argmax finder
   */
  public DenoisingArgMaxFinder(ArgMaxFinder1DInterface pArgMaxFinder1D)
  {
    super();
    mDelegatedArgMaxFinder1D = pArgMaxFinder1D;
  }

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    final int lLength = pY.length;

    final double[] lY = new double[lLength];

    if (pY[0] > pY[1])
      lY[0] = pY[1];
    else
      lY[0] = pY[0];

    for (int i = 1; i < lLength - 1; i++)
    {
      if (pY[i] > pY[i - 1] && pY[i] > pY[i + 1])
        lY[i] = 0.5 * (pY[i - 1] + pY[i + 1]);
      else
        lY[i] = pY[i];
    }
    if (pY[lLength - 2] < pY[lLength - 1])
      lY[lLength - 1] = pY[lLength - 2];
    else
      lY[lLength - 1] = pY[lLength - 1];

    /*System.out.println("_____________________");
    for (final double y : lY)
    	System.out.println(y); /**/

    final Double lArgmax = mDelegatedArgMaxFinder1D.argmax(pX, lY);

    return lArgmax;
  }

  @Override
  public String toString()
  {
    return String.format("DenoisingArgMaxFinder [%s]",
                         mDelegatedArgMaxFinder1D);
  }

}
