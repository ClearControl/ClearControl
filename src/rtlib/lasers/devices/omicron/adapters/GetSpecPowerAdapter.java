package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetSpecPowerAdapter extends OmicronAdapter	implements
														SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetSpecInfoCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		final String[] lSplittedMessage = ProtocolXX.splitMessage(	ProtocolXX.cGetSpecInfoReplyPrefix,
																	pMessage);
		final String lSpecPowerString = lSplittedMessage[1];
		final int lSpecPower = Integer.parseInt(lSpecPowerString);
		return (double) lSpecPower;
	}

}
