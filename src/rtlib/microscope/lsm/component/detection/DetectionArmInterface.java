package rtlib.microscope.lsm.component.detection;

import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.Variable;
import rtlib.device.openclose.OpenCloseDeviceInterface;

public interface DetectionArmInterface extends
																			OpenCloseDeviceInterface
{
	public Variable<Double> getZVariable();

	@SuppressWarnings("rawtypes")
	public Variable<UnivariateAffineComposableFunction> getZFunction();

	public void resetFunctions();

	void update();
}
