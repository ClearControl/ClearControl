package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SetLaserOnOffAdapter extends OmicronAdapter implements
														SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return null;
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return null;
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pOldValue,
											final double pNewValue)
	{
		return pNewValue > 0 ? ProtocolXX.cSetLaserOnCommand.getBytes()
							: ProtocolXX.cSetLaserOffCommand.getBytes();
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return super.checkAcknowledgementSetValueReturnMessage(pMessage);
	}

}
