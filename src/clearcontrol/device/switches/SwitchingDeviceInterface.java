package clearcontrol.device.switches;

import clearcontrol.core.variable.Variable;

public interface SwitchingDeviceInterface
{
	int getNumberOfSwitches();

	default void setSwitch(int pSwitchIndex, boolean pSwitchState)
	{
		getSwitchVariable(pSwitchIndex).set(pSwitchState);
	};

	Variable<Boolean> getSwitchVariable(int pSwitchIndex);

	String getSwitchName(int pSwitchIndex);
}
