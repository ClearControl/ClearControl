package rtlib.microscope.lsm.component.detection;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.ObjectVariable;

public interface DetectionArmInterface extends
																			OpenCloseDeviceInterface
{
	public ObjectVariable<Double> getZVariable();

	@SuppressWarnings("rawtypes")
	public ObjectVariable<UnivariateAffineComposableFunction> getZFunction();

	public void resetFunctions();

	void update();
}
