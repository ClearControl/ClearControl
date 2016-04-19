package rtlib.microscope.lsm.component.detection;

import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.Variable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;

public interface DetectionArmInterface extends
																			NameableInterface,
																			OpenCloseDeviceInterface
{
	public Variable<Number> getZVariable();

	public Variable<UnivariateAffineComposableFunction> getZFunction();

	public void resetFunctions();

	void update();
}
