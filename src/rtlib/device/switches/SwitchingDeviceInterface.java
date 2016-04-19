package rtlib.device.switches;

import rtlib.core.variable.Variable;

public interface SwitchingDeviceInterface 
{
	int getNumberOfSwitches();

	Variable<Boolean> getSwitchVariable(int pSwitchIndex);

	String getSwitchName(int pSwitchIndex);
}
