package clearcontrol.devices.optomech.opticalswitch.gui;

import clearcontrol.core.device.switches.gui.SwitchingDevicePanel;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;

/**
 * Optical switch device panel
 *
 * @author royer
 */
public class OpticalSwitchDevicePanel extends SwitchingDevicePanel
{

  /**
   * Instantiates an optical switch device panel
   * 
   * @param pOpticalSwitchDeviceInterface
   *          optical switch device
   */
  public OpticalSwitchDevicePanel(OpticalSwitchDeviceInterface pOpticalSwitchDeviceInterface)
  {
    super(pOpticalSwitchDeviceInterface);
  }

}
