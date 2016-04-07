package rtlib.core.device;

import rtlib.core.variable.Variable;

public interface SwitchingDeviceInterface
{
	int getNumberOfSwitches();

	Variable<Boolean> getSwitchingVariable(int pSwitchIndex);
}
