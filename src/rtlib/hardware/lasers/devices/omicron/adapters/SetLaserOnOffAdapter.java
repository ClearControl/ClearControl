package rtlib.hardware.lasers.devices.omicron.adapters;

import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class SetLaserOnOffAdapter extends OmicronAdapter<Boolean>	implements
																																	SerialTextDeviceAdapter<Boolean>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return null;
	}

	@Override
	public Boolean parseValue(final byte[] pMessage)
	{
		return null;
	}

	@Override
	public byte[] getSetValueCommandMessage(final Boolean pOldValue,
																					final Boolean pNewValue)
	{
		return pNewValue ? ProtocolXX.cSetLaserOnCommand.getBytes()
										: ProtocolXX.cSetLaserOffCommand.getBytes();
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return super.checkAcknowledgementSetValueReturnMessage(pMessage);
	}

}
