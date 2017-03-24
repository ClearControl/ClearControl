package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to query max laser power.
 *
 * @author royer
 */
public class GetMaxPowerAdapter extends OmicronAdapter<Number>
                                implements
                                SerialTextDeviceAdapter<Number>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolOmicron.cGetMaxPowerCommand.getBytes();
  }

  @Override
  public Number parseValue(final byte[] pMessage)
  {
    // System.out.println(new String(pMessage));
    final String[] lSplittedMessage =
                                    ProtocolOmicron.splitMessage(ProtocolOmicron.cGetMaxPowerReplyPrefix,
                                                                 pMessage);
    final String lMaxPowerString = lSplittedMessage[0];
    final int lMaxPower = Integer.parseInt(lMaxPowerString);
    return (double) lMaxPower;
  }

}
