package rtlib.fiberswitch;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.longv.LongVariable;

public interface FiberSwitchDeviceInterface	extends
											OpenCloseDeviceInterface
{

	DoubleVariable getPositionVariable();

	int getPosition();

	void setSwitchPosition(int pPosition);

}
