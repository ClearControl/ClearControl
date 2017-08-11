package clearcontrol.core.math.argmax.methods;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;

/**
 * Mode argmax finder.
 * 
 * Returns the mode of the (X,Y) data interpreted as a distribution.
 *
 * @author royer
 */
public class ModeArgMaxFinder implements ArgMaxFinder1DInterface
{

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    double lArgMax = 0;
    double lMaxY = Double.NEGATIVE_INFINITY;

    final int lLength = pY.length;
    for (int i = 0; i < lLength; i++)
    {
      final double lY = pY[i];
      if (lY > lMaxY)
      {
        lArgMax = pX[i];
        lMaxY = lY;
      }
    }
    return lArgMax;
  }

  @Override
  public String toString()
  {
    return String.format("ModeArgMaxFinder []");
  }

}
