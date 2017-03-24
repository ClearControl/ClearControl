package clearcontrol.devices.optomech.filterwheels;

import clearcontrol.core.device.position.PositionDeviceBase;
import clearcontrol.core.variable.Variable;

/**
 * Base class for all filter wheel devices
 *
 * @author royer
 */
public abstract class FilterWheelDeviceBase extends PositionDeviceBase
                                            implements
                                            FilterWheelDeviceInterface
{
  protected Variable<Integer> mFilterSpeedVariable = null;

  /**
   * Instanciate a filterwheel device given a name and valid positions
   * 
   * @param pDeviceName
   *          device name
   * @param pValidPositions
   *          valid positions
   */
  public FilterWheelDeviceBase(String pDeviceName,
                               int[] pValidPositions)
  {
    super(pDeviceName, pValidPositions);
    mFilterSpeedVariable =
                         new Variable<Integer>("FilterWheelSpeed", 0);
  }

  /**
   * Instanciates a filterwheel device given a device name and device index.
   * 
   * @param pDeviceName
   *          device name
   * @param pDeviceIndex
   *          device index
   */
  public FilterWheelDeviceBase(String pDeviceName, int pDeviceIndex)
  {
    super("filterwheel", pDeviceName, pDeviceIndex);
    mFilterSpeedVariable =
                         new Variable<Integer>("FilterWheelSpeed", 0);
  }

  @Override
  public final Variable<Integer> getSpeedVariable()
  {
    return mFilterSpeedVariable;
  }

  @Override
  public int getSpeed()
  {
    return mFilterSpeedVariable.get();
  }

  @Override
  public void setSpeed(final int pSpeed)
  {
    mFilterSpeedVariable.set(pSpeed);
  }

}
