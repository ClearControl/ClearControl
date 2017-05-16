package clearcontrol.devices.optomech.opticalswitch.devices.optojena;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.com.serial.SerialDevice;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.position.PositionDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.optomech.OptoMechDeviceInterface;
import clearcontrol.devices.optomech.opticalswitch.devices.optojena.adapters.FiberSwitchPositionAdapter;

/**
 * OptoJena fiber switch device
 *
 * @author royer
 */
public class OptoJenaFiberSwitchDevice extends SerialDevice implements
                                       PositionDeviceInterface,
                                       OptoMechDeviceInterface
{

  private Variable<Integer> mPositionVariable;
  private ConcurrentHashMap<Integer, String> mFilterPositionToNameMap;

  /**
   * OptoJena switch device
   * 
   * @param pDeviceIndex
   *          device index
   */
  public OptoJenaFiberSwitchDevice(final int pDeviceIndex)
  {
    this(MachineConfiguration.get().getSerialDevicePort(
                                                        "fiberswitch.optojena",
                                                        pDeviceIndex,
                                                        "NULL"));

    mPositionVariable = new Variable<Integer>("SwitchPosition", 0);
  }

  /**
   * OptoJena fiber switch device
   * 
   * @param pPortName
   *          port name
   */
  public OptoJenaFiberSwitchDevice(final String pPortName)
  {
    super("OptoJenaFiberSwitch", pPortName, 76800);

    final FiberSwitchPositionAdapter lFiberSwitchPosition =
                                                          new FiberSwitchPositionAdapter(this);

    mPositionVariable = addSerialVariable("OpticalSwitchPosition",
                                          lFiberSwitchPosition);

  }

  @Override
  public boolean open()
  {
    final boolean lIsOpened = super.open();
    setPosition(0);

    return lIsOpened;
  }

  @Override
  public Variable<Integer> getPositionVariable()
  {
    return mPositionVariable;
  }

  @Override
  public int getPosition()
  {
    return mPositionVariable.get();
  }

  @Override
  public void setPosition(int pPosition)
  {
    mPositionVariable.set(pPosition);
  }

  @Override
  public int[] getValidPositions()
  {
    return new int[]
    { 1, 2, 3, 4, 5, 6 };
  }

  @Override
  public void setPositionName(int pPositionIndex,
                              String pPositionName)
  {
    mFilterPositionToNameMap.put(pPositionIndex, pPositionName);
  }

  @Override
  public String getPositionName(int pPositionIndex)
  {
    return mFilterPositionToNameMap.get(mPositionVariable);
  }

}
