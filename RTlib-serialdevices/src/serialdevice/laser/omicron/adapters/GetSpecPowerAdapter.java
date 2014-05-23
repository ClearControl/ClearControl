package serialdevice.laser.omicron.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

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
		final String[] lSplittedMessage = ProtocolXX.splitMessage(pMessage);
		final String lSpecPowerString = lSplittedMessage[1];
		final int lSpecPower = Integer.parseInt(lSpecPowerString);
		return (double) lSpecPower;
	}

}
