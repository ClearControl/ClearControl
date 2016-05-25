package clearcontrol.microscope.lightsheet.component.detection;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public interface DetectionArmInterface extends
																			NameableInterface,
																			OpenCloseDeviceInterface,
																			HasChangeListenerInterface
{
	public BoundedVariable<Number> getZVariable();

	public Variable<UnivariateAffineFunction> getZFunction();

	public void resetFunctions();
	
	public void resetBounds();

	void update();
}
