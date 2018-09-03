package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to set target laser power.
 *
 * @author royer
 */
public class GetSetTargetPowerAdapter extends OmicronAdapter<Number>
                                      implements
                                      SerialTextDeviceAdapter<Number>
{

  private double mMaxPowerInMilliWatt;

  /**
   * Sets max power in milliwatts
   * 
   * @param pMaxPowerInMilliWatt
   *          max power in milliwatt
   * 
   */
  public void setMaxPowerInMilliWatt(final double pMaxPowerInMilliWatt)
  {
    mMaxPowerInMilliWatt = pMaxPowerInMilliWatt;
  }

  @Override
  public byte[] getGetValueCommandMessage()
  {
    // System.out.println("GET: sent: "+new
    // String(ProtocolXX.cGetPowerLevelCommand));
    return ProtocolOmicron.cGetPowerLevelCommand.getBytes();
  }

  @Override
  public Double parseValue(final byte[] pMessage)
  {
    // System.out.println("GET: received: "+new String(pMessage));
    final String[] lSplittedMessage =
                                    ProtocolOmicron.splitMessage(ProtocolOmicron.cGetPowerLevelReplyPrefix,
                                                                 pMessage);
    final String lSpecPowerString = lSplittedMessage[0];
    final int lCurrentPowerInBinaryUnits =
                                         Integer.parseInt(lSpecPowerString,
                                                          16);
    final double lTargetPowerInPercent =
                                       (double) lCurrentPowerInBinaryUnits
                                         / (4096 - 1);

    return lTargetPowerInPercent * mMaxPowerInMilliWatt;
  }

  @Override
  public byte[] getSetValueCommandMessage(final Number pOldPowerInMilliWatt,
                                          final Number pNewPowerInMilliWatt)
  {
    final double lPowerInPercent = pNewPowerInMilliWatt.doubleValue()
                                   / mMaxPowerInMilliWatt;
    // System.out.format("SET: power %g (percent) \n",lPowerInPercent);
    final int lPower = (int) Math.round(lPowerInPercent * (4096 - 1));
    // System.out.format("SET: power %d (percent*(4096-1)) \n",lPower);
    final String lHexPowerString =
                                 ProtocolOmicron.toHexadecimalString(lPower,
                                                                     3);
    // System.out.format("SET: power %s (percent*(4096-1) HEX)
    // \n",lHexPowerString);
    final String lSetTargetPowerCommandString =
                                              String.format(ProtocolOmicron.cSetPowerLevelCommand,
                                                            lHexPowerString);

    final byte[] lSetTargetPowerCommandBytes =
                                             lSetTargetPowerCommandString.getBytes();
    // System.out.println("SET: sent: "+new
    // String(lSetTargetPowerCommandBytes));
    return lSetTargetPowerCommandBytes;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    // System.out.println("SET: received: "+new String(pMessage));
    return super.checkAcknowledgementSetValueReturnMessage(pMessage);
  }

}
