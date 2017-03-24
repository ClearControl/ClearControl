package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to query number of working hours.
 *
 * @author royer
 */
public class GetWorkingHoursAdapter extends OmicronAdapter<Integer>
                                    implements
                                    SerialTextDeviceAdapter<Integer>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolOmicron.cGetWorkingHoursCommand.getBytes();
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    final String[] lSplittedMessage =
                                    ProtocolOmicron.splitMessage(ProtocolOmicron.cGetWorkingHoursReplyPrefix,
                                                                 pMessage);
    final String lMaxPowerString = lSplittedMessage[0];
    final int lMaxPower = Integer.parseInt(lMaxPowerString);
    return lMaxPower;
  }

}
