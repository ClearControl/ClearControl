package rtlib.hardware.lasers.devices.omicron.adapters;

import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class GetWorkingHoursAdapter extends OmicronAdapter<Integer>	implements
																																		SerialTextDeviceAdapter<Integer>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetWorkingHoursCommand.getBytes();
	}

	@Override
	public Integer parseValue(final byte[] pMessage)
	{
		final String[] lSplittedMessage = ProtocolXX.splitMessage(ProtocolXX.cGetWorkingHoursReplyPrefix,
																															pMessage);
		final String lMaxPowerString = lSplittedMessage[0];
		final int lMaxPower = Integer.parseInt(lMaxPowerString);
		return lMaxPower;
	}

}
