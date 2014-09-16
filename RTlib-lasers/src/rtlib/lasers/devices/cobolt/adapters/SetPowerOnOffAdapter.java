package rtlib.lasers.devices.cobolt.adapters;

import rtlib.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SetPowerOnOffAdapter extends CoboltAdapter	implements
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
		return pNewValue > 0 ? ProtocolCobolt.cSetLaserOnCommand.getBytes()
											: ProtocolCobolt.cSetLaserOffCommand.getBytes();
	}

}
