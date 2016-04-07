package rtlib.optomech.filterwheels;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.PositionDeviceInterface;
import rtlib.core.variable.ObjectVariable;

public interface FilterWheelDeviceInterface	extends
																						OpenCloseDeviceInterface,
																						PositionDeviceInterface
{

	ObjectVariable<Integer> getSpeedVariable();

	int getSpeed();

	void setSpeed(int pSpeed);

}
