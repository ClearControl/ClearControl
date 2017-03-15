package clearcontrol.hardware.optomech.filterwheels;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.position.PositionDeviceBase;

public abstract class FilterWheelDeviceBase extends PositionDeviceBase
                                            implements
                                            FilterWheelDeviceInterface
{
  protected Variable<Integer> mFilterSpeedVariable = null;

  public FilterWheelDeviceBase(String pDeviceName,
                               int[] pValidPositions)
  {
    super(pDeviceName, pValidPositions);
    mFilterSpeedVariable =
                         new Variable<Integer>("FilterWheelSpeed", 0);
  }

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
