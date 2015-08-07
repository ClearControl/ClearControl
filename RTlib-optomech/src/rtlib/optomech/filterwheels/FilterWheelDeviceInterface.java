package rtlib.optomech.filterwheels;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.PositionDeviceInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;

public interface FilterWheelDeviceInterface	extends
											OpenCloseDeviceInterface,
											PositionDeviceInterface
{

	DoubleVariable getSpeedVariable();

	int getSpeed();

	void setSpeed(int pSpeed);

}
