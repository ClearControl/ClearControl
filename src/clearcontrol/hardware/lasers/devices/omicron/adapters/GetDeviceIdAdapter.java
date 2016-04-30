package clearcontrol.hardware.lasers.devices.omicron.adapters;

import clearcontrol.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class GetDeviceIdAdapter extends OmicronAdapter<Integer>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetFirmwareCommand.getBytes();
	}

	@Override
	public Integer parseValue(final byte[] pMessage)
	{
		/*System.out.println(GetDeviceIdAdapter.class.getSimpleName() + ": message received: "
												+ new String(pMessage));/**/

		final String[] lSplittedMessage = ProtocolXX.splitMessage(ProtocolXX.cGetFirmwareReplyPrefix,
																															pMessage);
		final String lDeviceIdString = lSplittedMessage[1];
		final int lDeviceId = Integer.parseInt(lDeviceIdString);
		return lDeviceId;
	}

}
