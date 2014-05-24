package rtlib.serialdevice.laser.omicron.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

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
		final String[] lSplittedMessage = ProtocolXX.splitMessage(pMessage);
		final String lWavelengthString = lSplittedMessage[0];
		final int lWavelengthInNanometer = Integer.parseInt(lWavelengthString);
		return (double) lWavelengthInNanometer;
	}

}
