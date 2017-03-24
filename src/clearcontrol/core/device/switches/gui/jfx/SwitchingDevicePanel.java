package clearcontrol.core.device.switches.gui.jfx;

import clearcontrol.core.device.switches.SwitchingDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;

public class SwitchingDevicePanel extends OnOffArrayPane
{

  public SwitchingDevicePanel(SwitchingDeviceInterface pSwitchingDeviceInterface)
  {
    super();

    int lNumberOfSwitches =
                          pSwitchingDeviceInterface.getNumberOfSwitches();

    for (int i = 0; i < lNumberOfSwitches; i++)
    {
      String lName = pSwitchingDeviceInterface.getSwitchName(i);
      Variable<Boolean> lSwitchVariable =
                                        pSwitchingDeviceInterface.getSwitchVariable(i);

      addSwitch(lName, lSwitchVariable);
    }
  }

}
