package clearcontrol.core.math.vectors;

/**
 * Utility methods to treat arrays as vectors
 */
public class VectorArrays
{

  /**
   * Normalizes array
   * 
   * @param pVector
   */
  public static void normalize(double[] pVector)
  {
    if (pVector.length == 0)
    {
      throw new IllegalArgumentException("Cannot normalise an empty vector! Returning null.");
    }
    else
    {
      double norm = 0.0;
      for (int i = 0; i < pVector.length; i++)
      {
        norm += pVector[i] * pVector[i];
      }
      norm = Math.sqrt(norm);
      if (norm > 0)
        for (int i = 0; i < pVector.length; i++)
        {
          pVector[i] = pVector[i] / norm;
        }

    }
  }
}
