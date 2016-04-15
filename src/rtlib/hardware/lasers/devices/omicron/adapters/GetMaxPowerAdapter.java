package rtlib.hardware.lasers.devices.omicron.adapters;

import rtlib.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetMaxPowerAdapter extends OmicronAdapter<Number> implements
																															SerialTextDeviceAdapter<Number>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetMaxPowerCommand.getBytes();
	}

	@Override
	public Number parseValue(final byte[] pMessage)
	{
		// System.out.println(new String(pMessage));
		final String[] lSplittedMessage = ProtocolXX.splitMessage(ProtocolXX.cGetMaxPowerReplyPrefix,
																															pMessage);
		final String lMaxPowerString = lSplittedMessage[0];
		final int lMaxPower = Integer.parseInt(lMaxPowerString);
		return (double) lMaxPower;
	}

}
