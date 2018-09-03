package clearcontrol.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Interface implemented by all composable functions.
 *
 * @param <T>
 *          function type
 * @author royer
 */
public interface ComposableFunction<T extends UnivariateFunction>
                                   extends UnivariateFunction
{
  /**
   * Compose this function with the given function.
   * 
   * @param pFunction
   *          function to compose with
   */
  public void composeWith(T pFunction);
}
