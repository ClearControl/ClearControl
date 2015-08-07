package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetWavelengthAdapter extends OmicronAdapter implements
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
		final String lWavelengthString = lSplittedMessage[0];
		final int lWavelengthInNanometer = Integer.parseInt(lWavelengthString);
		return (double) lWavelengthInNanometer;
	}

}
