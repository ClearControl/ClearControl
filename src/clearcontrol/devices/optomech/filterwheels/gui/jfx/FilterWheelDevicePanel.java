package clearcontrol.devices.optomech.filterwheels.gui.jfx;

import clearcontrol.core.device.position.gui.PositionDevicePanel;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;

/**
 * GUI Panel for filter wheel devices
 *
 * @author royer
 */
public class FilterWheelDevicePanel extends PositionDevicePanel
{

  /**
   * Instanciates a filter wheel device panel
   * 
   * @param pFilterWheelDeviceInterface
   *          device panel
   */
  public FilterWheelDevicePanel(FilterWheelDeviceInterface pFilterWheelDeviceInterface)
  {
    super(pFilterWheelDeviceInterface);
  }

}
