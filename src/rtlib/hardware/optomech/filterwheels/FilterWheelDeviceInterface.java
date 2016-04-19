package rtlib.hardware.optomech.filterwheels;

import rtlib.core.variable.Variable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.position.PositionDeviceInterface;

public interface FilterWheelDeviceInterface	extends
																						NameableInterface,
																						OpenCloseDeviceInterface,
																						PositionDeviceInterface
{

	Variable<Integer> getSpeedVariable();

	int getSpeed();

	void setSpeed(int pSpeed);

}
