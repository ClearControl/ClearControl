package rtlib.lasers.devices.cobolt.adapters;

import rtlib.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SetPowerOnOffAdapter extends CoboltAdapter<Boolean> implements
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
		return pNewValue ? ProtocolCobolt.cSetLaserOnCommand.getBytes()
										: ProtocolCobolt.cSetLaserOffCommand.getBytes();
	}

}
