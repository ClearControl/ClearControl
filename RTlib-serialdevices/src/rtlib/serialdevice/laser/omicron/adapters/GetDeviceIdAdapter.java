package rtlib.serialdevice.laser.omicron.adapters;

import rtlib.serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

public class GetDeviceIdAdapter extends OmicronAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetFirmwareCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		/*System.out.println(GetDeviceIdAdapter.class.getSimpleName() + ": message received: "
												+ new String(pMessage));/**/
		final String[] lSplittedMessage = ProtocolXX.splitMessage(pMessage);
		final String lDeviceIdString = lSplittedMessage[1];
		final int lDeviceId = Integer.parseInt(lDeviceIdString);
		return (double) lDeviceId;
	}

}
