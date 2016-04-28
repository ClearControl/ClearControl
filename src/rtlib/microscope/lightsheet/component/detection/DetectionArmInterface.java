package rtlib.microscope.lightsheet.component.detection;

import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.Variable;
import rtlib.core.variable.bounded.BoundedVariable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;

public interface DetectionArmInterface extends
																			NameableInterface,
																			OpenCloseDeviceInterface
{
	public BoundedVariable<Number> getZVariable();

	public Variable<UnivariateAffineFunction> getZFunction();

	public void resetFunctions();
	
	public void resetBounds();

	void update();
}
