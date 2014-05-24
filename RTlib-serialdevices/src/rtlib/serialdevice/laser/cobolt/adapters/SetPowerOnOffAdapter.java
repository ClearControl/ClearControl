package rtlib.serialdevice.laser.cobolt.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.cobolt.adapters.protocol.ProtocolCobolt;

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
	public byte[] getSetValueCommandMessage(final double pValue)
	{
		return pValue > 0	? ProtocolCobolt.cSetLaserOnCommand.getBytes()
											: ProtocolCobolt.cSetLaserOffCommand.getBytes();
	}

}
