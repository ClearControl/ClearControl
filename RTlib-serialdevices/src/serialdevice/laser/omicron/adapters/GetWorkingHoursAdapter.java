package serialdevice.laser.omicron.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

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
