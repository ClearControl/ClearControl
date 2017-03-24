package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to set laser operating mode.
 *
 * @author royer
 */
public class SetOperatingModeAdapter extends OmicronAdapter<Integer>
                                     implements
                                     SerialTextDeviceAdapter<Integer>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return null;
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    return null;
  }

  @Override
  public byte[] getSetValueCommandMessage(final Integer pOldValue,
                                          final Integer pNewValue)
  {
    final int lOperatingMode = pNewValue;
    final String lHexOperatingModeString =
                                         ProtocolOmicron.toHexadecimalString(lOperatingMode,
                                                                             1);
    final String lSetOperatingModeCommandString =
                                                String.format(ProtocolOmicron.cSetOperatingModeCommand,
                                                              lHexOperatingModeString);

    final byte[] lSetOperatingModeCommandBytes =
                                               lSetOperatingModeCommandString.getBytes();

    return lSetOperatingModeCommandBytes;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    return super.checkAcknowledgementSetValueReturnMessage(pMessage);
  }

}
