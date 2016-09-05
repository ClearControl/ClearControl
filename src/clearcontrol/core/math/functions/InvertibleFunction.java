package clearcontrol.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface InvertibleFunction<T extends UnivariateFunction> extends
																																	UnivariateFunction
{
	T inverse();
}
