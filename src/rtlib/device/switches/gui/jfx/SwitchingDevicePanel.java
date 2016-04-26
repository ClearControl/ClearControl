package rtlib.device.switches.gui.jfx;

import rtlib.core.variable.Variable;
import rtlib.device.switches.SwitchingDeviceInterface;
import rtlib.gui.jfx.onoff.OnOffArrayPane;

public class SwitchingDevicePanel extends OnOffArrayPane
{

	public SwitchingDevicePanel(SwitchingDeviceInterface pSwitchingDeviceInterface)
	{
		super();

		int lNumberOfSwitches = pSwitchingDeviceInterface.getNumberOfSwitches();

		for (int i = 0; i < lNumberOfSwitches; i++)
		{
			String lName = pSwitchingDeviceInterface.getSwitchName(i);
			Variable<Boolean> lSwitchVariable = pSwitchingDeviceInterface.getSwitchVariable(i);

			addSwitch(lName, lSwitchVariable);
		}
	}

}
