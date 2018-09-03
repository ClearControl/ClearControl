package clearcontrol.devices.lasers.devices.cobolt.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

/**
 * Serial device adapter to query current laser power.
 *
 * @author royer
 */
public class GetCurrentPowerAdapter extends CoboltAdapter<Number>
                                    implements
                                    SerialTextDeviceAdapter<Number>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolCobolt.cReadOutputPowerCommand.getBytes();
  }

  @Override
  public Number parseValue(final byte[] pMessage)
  {
    return 1000 * ProtocolCobolt.parseFloat(pMessage);
  }

}
