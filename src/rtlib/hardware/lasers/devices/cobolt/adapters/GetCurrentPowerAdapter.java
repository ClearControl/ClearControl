package rtlib.hardware.lasers.devices.cobolt.adapters;

import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

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
