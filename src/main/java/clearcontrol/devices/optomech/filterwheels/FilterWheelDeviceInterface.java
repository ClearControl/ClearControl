package clearcontrol.devices.optomech.filterwheels;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.position.PositionDeviceInterface;
import clearcontrol.core.variable.Variable;

/**
 * Interface implemented by all filterwheel devices
 *
 * @author royer
 */
public interface FilterWheelDeviceInterface extends
                                            NameableInterface,
                                            OpenCloseDeviceInterface,
                                            PositionDeviceInterface
{

  /**
   * Convenience method for retreiving the speed.
   * 
   * @return speed
   */
  int getSpeed();

  /**
   * Convenience method for setting the speed.
   * 
   * @param pSpeed
   */
  void setSpeed(int pSpeed);

  /**
   * Returns the speed variable
   * 
   * @return speed variable
   */
  Variable<Integer> getSpeedVariable();

}
