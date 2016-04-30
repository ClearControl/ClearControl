package clearcontrol.hardware.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class GetSpecPowerAdapter extends OmicronAdapter<Number>	implements
																																SerialTextDeviceAdapter<Number>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetSpecInfoCommand.getBytes();
	}

	@Override
	public Number parseValue(final byte[] pMessage)
	{
		final String[] lSplittedMessage = ProtocolXX.splitMessage(ProtocolXX.cGetSpecInfoReplyPrefix,
																															pMessage);
		final String lSpecPowerString = lSplittedMessage[1];
		final int lSpecPower = Integer.parseInt(lSpecPowerString);
		return (double) lSpecPower;
	}

}
