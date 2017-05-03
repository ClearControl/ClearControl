package clearcontrol.devices.optomech.opticalswitch.devices.arduino.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.optomech.opticalswitch.devices.arduino.ArduinoOpticalSwitchDevice;

/**
 * Arduino Optical switch position adapter
 *
 * @author royer
 */
public class ArduinoOpticalSwitchPositionAdapter extends
                                                 SerialDeviceAdapterAdapter<Long>
                                                 implements
                                                 SerialTextDeviceAdapter<Long>
{

  /**
   * Instantiates an Arduino optical switch position adapter
   * 
   * @param pArduinoOpticalSwitchDevice
   *          arduino optical switch device
   */
  public ArduinoOpticalSwitchPositionAdapter(final ArduinoOpticalSwitchDevice pArduinoOpticalSwitchDevice)
  {

  }

  @Override
  public byte[] getSetValueCommandMessage(Long pOldValue,
                                          Long pNewValue)
  {
    String lMessage = String.format("%d\n", pNewValue);
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
  public Character getGetValueReturnMessageTerminationCharacter()
  {
    return '\n';
  }

  @Override
  public Character getSetValueReturnMessageTerminationCharacter()
  {
    return '\n';
  }

}
