package rtlib.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateAffineComposableFunction	extends
													UnivariateFunction, FunctionDomain
{
	
	double getConstant();

	double getSlope();
	
	public void composeWith(UnivariateAffineFunction pFunction);

	public void setIdentity();

}
