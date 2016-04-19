package rtlib.hardware.lasers.devices.omicron.adapters;

import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class SetOperatingModeAdapter extends OmicronAdapter<Integer> implements
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
		final String lHexOperatingModeString = ProtocolXX.toHexadecimalString(lOperatingMode,
																																					1);
		final String lSetOperatingModeCommandString = String.format(ProtocolXX.cSetOperatingModeCommand,
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
