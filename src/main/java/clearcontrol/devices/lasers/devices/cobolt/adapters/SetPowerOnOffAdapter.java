package clearcontrol.devices.lasers.devices.cobolt.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

/**
 * Serial device adapter to query on/off state.
 *
 * @author royer
 */
public class SetPowerOnOffAdapter extends CoboltAdapter<Boolean>
                                  implements
                                  SerialTextDeviceAdapter<Boolean>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return null;
  }

  @Override
  public Boolean parseValue(final byte[] pMessage)
  {
    return null;
  }

  @Override
  public byte[] getSetValueCommandMessage(final Boolean pOldValue,
                                          final Boolean pNewValue)
  {
    return pNewValue ? ProtocolCobolt.cSetLaserOnCommand.getBytes()
                     : ProtocolCobolt.cSetLaserOffCommand.getBytes();
  }

}
