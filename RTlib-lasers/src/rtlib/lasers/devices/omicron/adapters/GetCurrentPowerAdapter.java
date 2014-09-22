package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetCurrentPowerAdapter extends OmicronAdapter implements
																													SerialTextDeviceAdapter
{
	private static final double cCurrentPowerFilteringAlpha = 0.1;

	private volatile double mCurrentPowerInMilliwatts;

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cMeasureDiodePowerCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		try
		{
			// final String[] lSplittedMessage = ProtocolXX.splitMessage(pMessage);
			// final String lCurrentPowerString = lSplittedMessage[0];
			final String lCurrentPowerString = new String(pMessage);
			final double lCurrentPowerInMilliwatts = ProtocolXX.parseDouble(ProtocolXX.cMeasureDiodePowerReplyPrefix,
																																			lCurrentPowerString);

			mCurrentPowerInMilliwatts = (1 - cCurrentPowerFilteringAlpha) * mCurrentPowerInMilliwatts
																	+ cCurrentPowerFilteringAlpha
																	* lCurrentPowerInMilliwatts;
		}
		catch (Throwable e)
		{
			System.err.printf("%s-%s: Problem while parsing current power level (received:'%s') \n",
												GetCurrentPowerAdapter.class.getSimpleName(),
												this.toString(),
												new String(pMessage));
		}

		return mCurrentPowerInMilliwatts;
	}

}
