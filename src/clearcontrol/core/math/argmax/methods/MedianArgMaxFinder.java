package clearcontrol.core.math.argmax.methods;

import static java.lang.Math.round;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;

/**
 * Median argmax finder.
 * 
 * Returns the median of the (X,Y) interpreted as distribution (or 50%
 * percentile) as argmax.
 *
 * @author royer
 */
public class MedianArgMaxFinder implements ArgMaxFinder1DInterface
{

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    double lHalfSum = 0;

    final int lLength = pY.length;
    for (int i = 0; i < lLength; i++)
    {
      final double lY = pY[i];
      lHalfSum += lY;
    }

    lHalfSum = 0.5 * lHalfSum;

    double lRunningSum = 0;
    for (int i = 0; i < lLength - 1; i++)
    {
      final double lY = pY[i];

      if (lRunningSum <= lHalfSum && lRunningSum + lY >= lHalfSum)
      {
        final double xa = pX[i];
        final double xb = pX[i + 1];

        double lArgmax = xa * ((lHalfSum - lRunningSum) / lY)
                         + xb * ((lRunningSum + lY - lHalfSum) / lY);

        return lArgmax;
      }

      lRunningSum += lY;
    }

    return pX[(int) round(0.5 * (pX.length - 1))];
  }

  @Override
  public String toString()
  {
    return String.format("MedianArgMaxFinder []");
  }

}
