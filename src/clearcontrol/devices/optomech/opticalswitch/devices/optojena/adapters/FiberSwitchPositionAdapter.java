package clearcontrol.devices.optomech.opticalswitch.devices.optojena.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;

/**
 * Fiber switch position adapter
 *
 * @author royer
 */
public class FiberSwitchPositionAdapter extends
                                        SerialDeviceAdapterAdapter<Integer>
                                        implements
                                        SerialTextDeviceAdapter<Integer>
{

  /**
   * Instantiates an OptoJena fiber switch position adapter
   * 
   * @param pOptoJenaFiberSwitchDevice
   *          OptoJena fiber switch device
   */
  public FiberSwitchPositionAdapter(final OptoJenaFiberSwitchDevice pOptoJenaFiberSwitchDevice)
  {

  }

  @Override
  public byte[] getSetValueCommandMessage(Integer pOldValue,
                                          Integer pNewValue)
  {
    String lMessage = String.format("ch%d\r\n", pNewValue + 1);
    return lMessage.getBytes();
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return 10;
  }

  @Override
  public boolean hasResponseForSet()
  {
    return false;
  }

  @Override
  public Character getSetValueReturnMessageTerminationCharacter()
  {
    return '\n';
  }

  @Override
  public Character getGetValueReturnMessageTerminationCharacter()
  {
    return '\n';
  };

}
