package rtlib.serialdevice.laser.omicron.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

public class GetMaxPowerAdapter extends OmicronAdapter implements
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
		final String[] lSplittedMessage = ProtocolXX.splitMessage(pMessage);
		final String lMaxPowerString = lSplittedMessage[0];
		final int lMaxPower = Integer.parseInt(lMaxPowerString);
		return (double) lMaxPower;
	}

}
