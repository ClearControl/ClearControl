package clearcontrol.hardware.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class GetWavelengthAdapter extends OmicronAdapter<Integer>	implements
																																	SerialTextDeviceAdapter<Integer>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetSpecInfoCommand.getBytes();
	}

	@Override
	public Integer parseValue(final byte[] pMessage)
	{
		final String[] lSplittedMessage = ProtocolXX.splitMessage(ProtocolXX.cGetSpecInfoReplyPrefix,
																															pMessage);
		final String lWavelengthString = lSplittedMessage[0];
		final int lWavelengthInNanometer = Integer.parseInt(lWavelengthString);
		return lWavelengthInNanometer;
	}

}
