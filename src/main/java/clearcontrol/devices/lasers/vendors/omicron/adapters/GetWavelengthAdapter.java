package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to query laser wavelength.
 *
 * @author royer
 */
public class GetWavelengthAdapter extends OmicronAdapter<Integer>
                                  implements
                                  SerialTextDeviceAdapter<Integer>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolOmicron.cGetSpecInfoCommand.getBytes();
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    final String[] lSplittedMessage =
                                    ProtocolOmicron.splitMessage(ProtocolOmicron.cGetSpecInfoReplyPrefix,
                                                                 pMessage);
    final String lWavelengthString = lSplittedMessage[0];
    final int lWavelengthInNanometer =
                                     Integer.parseInt(lWavelengthString);
    return lWavelengthInNanometer;
  }

}
