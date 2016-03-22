package rtlib.lasers.devices.cobolt.adapters;

import rtlib.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

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
