package clearcontrol.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Interface implemented by functions for hich an inverse can be (sometimes)
 * defined.
 *
 * @param <T>
 *          function type
 * @author royer
 */
public interface InvertibleFunction<T extends UnivariateFunction>
                                   extends UnivariateFunction
{
  /**
   * Computes and returns the inverse function if it exists.
   * 
   * @return inverse function.
   */
  T inverse();

  /**
   * Returns true if the ib=nverse function exists, false otherwise.
   * 
   * @return true if inverse exists, false otherwise.
   */
  boolean hasInverse();
}
