package clearcontrol.devices.lasers.devices.cobolt.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

/**
 * Serial device adapter to query target laser power.
 *
 * @author royer
 */
public class GetSetTargetPowerAdapter extends CoboltAdapter<Number>
                                      implements
                                      SerialTextDeviceAdapter<Number>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolCobolt.cGetSetOutputPowerCommand.getBytes();
  }

  @Override
  public Number parseValue(final byte[] pMessage)
  {
    final double lTargetPowerInMilliWatt = 1000
                                           * ProtocolCobolt.parseFloat(pMessage);
    return lTargetPowerInMilliWatt;
  }

  @Override
  public byte[] getSetValueCommandMessage(final Number pOldPowerInMilliWatt,
                                          final Number pNewPowerInMilliWatt)
  {
    final double lPowerInWatt = pNewPowerInMilliWatt.doubleValue()
                                * 0.001;
    final String lSetTargetPowerCommandString =
                                              String.format(ProtocolCobolt.cSetOutputPowerCommand,
                                                            lPowerInWatt);
    final byte[] lSetTargetPowerCommandBytes =
                                             lSetTargetPowerCommandString.getBytes();
    return lSetTargetPowerCommandBytes;
  }

}
