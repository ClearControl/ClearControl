package clearcontrol.hardware.optomech.opticalswitch.devices.arduino.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.hardware.optomech.opticalswitch.devices.arduino.ArduinoOpticalSwitchDevice;

public class ArduinoOpticalSwitchPositionAdapter extends
                                                 SerialDeviceAdapterAdapter<Long>
                                                 implements
                                                 SerialTextDeviceAdapter<Long>
{

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
