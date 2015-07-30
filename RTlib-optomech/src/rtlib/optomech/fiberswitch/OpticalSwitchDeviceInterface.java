package rtlib.optomech.fiberswitch;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;

public interface OpticalSwitchDeviceInterface	extends
											OpenCloseDeviceInterface
{

	DoubleVariable getPositionVariable();

	int getPosition();

	void setPosition(int pPosition);

	int[] getValidPositions();

}
