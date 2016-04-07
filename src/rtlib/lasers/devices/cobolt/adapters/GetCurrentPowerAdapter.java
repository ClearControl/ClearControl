package rtlib.lasers.devices.cobolt.adapters;

import rtlib.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetCurrentPowerAdapter extends CoboltAdapter<Number>	implements
																																	SerialTextDeviceAdapter<Number>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolCobolt.cReadOutputPowerCommand.getBytes();
	}

	@Override
	public Number parseValue(final byte[] pMessage)
	{
		return 1000 * ProtocolCobolt.parseFloat(pMessage);
	}

}
