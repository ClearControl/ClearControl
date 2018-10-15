package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to query spec laser power.
 *
 * @author royer
 */
public class GetSpecPowerAdapter extends OmicronAdapter<Number>
                                 implements
                                 SerialTextDeviceAdapter<Number>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolOmicron.cGetSpecInfoCommand.getBytes();
  }

  @Override
  public Number parseValue(final byte[] pMessage)
  {
    final String[] lSplittedMessage =
                                    ProtocolOmicron.splitMessage(ProtocolOmicron.cGetSpecInfoReplyPrefix,
                                                                 pMessage);
    final String lSpecPowerString = lSplittedMessage[1];
    final int lSpecPower = Integer.parseInt(lSpecPowerString);
    return (double) lSpecPower;
  }

}
