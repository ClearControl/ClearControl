package rtlib.serialdevice.laser.cobolt.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.cobolt.adapters.protocol.ProtocolCobolt;

public class GetWorkingHoursAdapter extends CoboltAdapter	implements
																													SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolCobolt.cGetWorkingHoursCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return ProtocolCobolt.parseFloat(pMessage);
	}

}
