package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Serial device adapter to query current laser power.
 *
 * @author royer
 */
public class GetCurrentPowerAdapter extends OmicronAdapter<Number>
                                    implements
                                    SerialTextDeviceAdapter<Number>,
                                    LoggingFeature
{
  private static final double cCurrentPowerFilteringAlpha = 0.1;

  private volatile double mCurrentPowerInMilliwatts;

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return ProtocolOmicron.cMeasureDiodePowerCommand.getBytes();
  }

  @Override
  public Number parseValue(final byte[] pMessage)
  {
    try
    {
      // final String[] lSplittedMessage =
      // ProtocolXX.splitMessage(pMessage);
      // final String lCurrentPowerString = lSplittedMessage[0];
      final String lCurrentPowerString = new String(pMessage);
      final double lCurrentPowerInMilliwatts =
                                             ProtocolOmicron.parseDouble(ProtocolOmicron.cMeasureDiodePowerReplyPrefix,
                                                                         lCurrentPowerString);

      mCurrentPowerInMilliwatts = (1 - cCurrentPowerFilteringAlpha)
                                  * mCurrentPowerInMilliwatts
                                  + cCurrentPowerFilteringAlpha
                                    * lCurrentPowerInMilliwatts;
    }
    catch (Throwable e)
    {
      severe("%s-%s: Problem while parsing current power level (received:'%s') \n",
             GetCurrentPowerAdapter.class.getSimpleName(),
             this.toString(),
             new String(pMessage));
    }

    return mCurrentPowerInMilliwatts;
  }

}
