package rtlib.filterwheels;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;

public interface FilterWheelDeviceInterface	extends
																						OpenCloseDeviceInterface
{

	DoubleVariable getPositionVariable();

	DoubleVariable getSpeedVariable();

	int getPosition();

	void setPosition(int pPosition);

	int getSpeed();

	void setSpeed(int pSpeed);

}
