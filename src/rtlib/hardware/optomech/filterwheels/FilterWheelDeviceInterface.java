package rtlib.hardware.optomech.filterwheels;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.PositionDeviceInterface;
import rtlib.core.variable.Variable;

public interface FilterWheelDeviceInterface	extends
																						OpenCloseDeviceInterface,
																						PositionDeviceInterface
{

	Variable<Integer> getSpeedVariable();

	int getSpeed();

	void setSpeed(int pSpeed);

}
