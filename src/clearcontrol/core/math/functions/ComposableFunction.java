package clearcontrol.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface ComposableFunction<T extends UnivariateFunction>
                                   extends UnivariateFunction
{
  public void composeWith(T pFunction);
}
