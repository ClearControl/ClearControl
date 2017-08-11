package clearcontrol.core.math.argmax;

import static java.lang.Math.pow;

/**
 * Utility class to compute the fit error.
 *
 * @author royer
 */
public class ComputeFitError
{
  /**
   * Computes the RMSD between Y and Y' (fitted Y)
   * 
   * @param pY
   *          y data
   * @param pFittedY
   *          y fit data
   * @return RMSD
   */
  public static final double rmsd(double[] pY, double[] pFittedY)
  {
    double lAverageError = 0;
    for (int i = 0; i < pY.length; i++)
    {
      double lError = pow(pY[i] - pFittedY[i], 2);
      lAverageError += lError;
    }

    lAverageError = lAverageError / pY.length;

    return pow(lAverageError, 0.5);
  }
}
