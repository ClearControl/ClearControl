package rtlib.microscope.lsm.component.detection;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.Variable;

public interface DetectionArmInterface extends
																			OpenCloseDeviceInterface
{
	public Variable<Double> getZVariable();

	@SuppressWarnings("rawtypes")
	public Variable<UnivariateAffineComposableFunction> getZFunction();

	public void resetFunctions();

	void update();
}
