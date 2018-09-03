package clearcontrol.core.device.switches.gui;

import clearcontrol.core.device.switches.SwitchingDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;

/**
 * Switching device panel
 *
 * @author royer
 */
public class SwitchingDevicePanel extends OnOffArrayPane
{

  /**
   * Instantiates a switching device panel for a given switching device.
   * 
   * @param pSwitchingDevice
   *          switching device
   */
  public SwitchingDevicePanel(SwitchingDeviceInterface pSwitchingDevice)
  {
    super();

    int lNumberOfSwitches = pSwitchingDevice.getNumberOfSwitches();

    for (int i = 0; i < lNumberOfSwitches; i++)
    {
      String lName = pSwitchingDevice.getSwitchName(i);
      Variable<Boolean> lSwitchVariable =
                                        pSwitchingDevice.getSwitchVariable(i);

      addSwitch(lName, lSwitchVariable);
    }
  }

  /**
   * Instantiates a switching device panel for a given switching device. This
   * constructor offers the opportunity to override the switches' name.
   * 
   * @param pSwitchingDevice
   *          switching device
   * @param pSubstituteNames
   *          switches names
   */
  public SwitchingDevicePanel(SwitchingDeviceInterface pSwitchingDevice,
                              String... pSubstituteNames)
  {
    super();

    int lNumberOfSwitches = pSwitchingDevice.getNumberOfSwitches();

    for (int i = 0; i < lNumberOfSwitches; i++)
    {
      String lName = pSubstituteNames[i];
      Variable<Boolean> lSwitchVariable =
                                        pSwitchingDevice.getSwitchVariable(i);

      addSwitch(lName, lSwitchVariable);
    }
  }

}
