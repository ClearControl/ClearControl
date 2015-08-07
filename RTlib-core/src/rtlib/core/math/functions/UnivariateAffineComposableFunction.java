package rtlib.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateAffineComposableFunction	extends
													UnivariateFunction
{
	public void composeWith(UnivariateAffineFunction pFunction);
}
