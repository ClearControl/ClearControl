package rtlib.core.device;

import rtlib.core.variable.ObjectVariable;

public interface SwitchingDeviceInterface
{
	int getNumberOfSwitches();

	ObjectVariable<Boolean> getSwitchingVariable(int pSwitchIndex);
}
