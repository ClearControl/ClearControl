package rtlib.filterwheels;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;

public interface FilterWheelDeviceInterface	extends
																						VirtualDeviceInterface
{

	DoubleVariable getPositionVariable();

	DoubleVariable getSpeedVariable();

}
