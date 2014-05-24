package rtlib.serialdevice.laser.omicron.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

public class SetOperatingModeAdapter extends OmicronAdapter	implements
																														SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return null;
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return null;
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pValue)
	{
		final int lPower = (int) Math.round(pValue * (4096 - 1));
		final String lHexOperatingModeString = ProtocolXX.toHexadecimalString(lPower,
																																					1);
		final String lSetOperatingModeCommandString = String.format(ProtocolXX.cRecallOperatingModeCommand,
																																lHexOperatingModeString);

		final byte[] lSetOperatingModeCommandBytes = lSetOperatingModeCommandString.getBytes();

		return lSetOperatingModeCommandBytes;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return super.checkAcknowledgementSetValueReturnMessage(pMessage);
	}

}
