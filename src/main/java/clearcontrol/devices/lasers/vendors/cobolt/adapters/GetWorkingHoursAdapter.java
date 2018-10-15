package clearcontrol.devices.lasers.devices.cobolt.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

/**
 * Serial device adapter to query number of working hours.
 *
 * @author royer
 */
public class GetWorkingHoursAdapter extends CoboltAdapter<Integer>
                                    implements
                                    SerialTextDeviceAdapter<Integer>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolCobolt.cGetWorkingHoursCommand.getBytes();
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    return (int) ProtocolCobolt.parseFloat(pMessage);
  }

}
