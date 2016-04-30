package clearcontrol.device.switches;

import clearcontrol.core.variable.Variable;

public interface SwitchingDeviceInterface 
{
	int getNumberOfSwitches();

	Variable<Boolean> getSwitchVariable(int pSwitchIndex);

	String getSwitchName(int pSwitchIndex);
}
