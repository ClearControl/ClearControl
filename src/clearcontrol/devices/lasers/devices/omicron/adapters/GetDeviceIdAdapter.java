package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to query device id.
 *
 * @author royer
 */
public class GetDeviceIdAdapter extends OmicronAdapter<Integer>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolOmicron.cGetFirmwareCommand.getBytes();
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    /*System.out.println(GetDeviceIdAdapter.class.getSimpleName() + ": message received: "
    										+ new String(pMessage));/**/

    final String[] lSplittedMessage =
                                    ProtocolOmicron.splitMessage(ProtocolOmicron.cGetFirmwareReplyPrefix,
                                                                 pMessage);
    final String lDeviceIdString = lSplittedMessage[1];
    final int lDeviceId = Integer.parseInt(lDeviceIdString);
    return lDeviceId;
  }

}
