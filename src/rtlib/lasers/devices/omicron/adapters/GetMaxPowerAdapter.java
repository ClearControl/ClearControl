package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetMaxPowerAdapter extends OmicronAdapter	implements
														SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetMaxPowerCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		// System.out.println(new String(pMessage));
		final String[] lSplittedMessage = ProtocolXX.splitMessage(	ProtocolXX.cGetMaxPowerReplyPrefix,
																	pMessage);
		final String lMaxPowerString = lSplittedMessage[0];
		final int lMaxPower = Integer.parseInt(lMaxPowerString);
		return (double) lMaxPower;
	}

}
