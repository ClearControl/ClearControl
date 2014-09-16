package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetWorkingHoursAdapter extends OmicronAdapter implements
																													SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetWorkingHoursCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		final String[] lSplittedMessage = ProtocolXX.splitMessage(pMessage);
		final String lMaxPowerString = lSplittedMessage[0];
		final int lMaxPower = Integer.parseInt(lMaxPowerString);
		return (double) lMaxPower;
	}

}
