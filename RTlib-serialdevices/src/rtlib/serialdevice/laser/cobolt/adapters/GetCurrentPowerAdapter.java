package rtlib.serialdevice.laser.cobolt.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.cobolt.adapters.protocol.ProtocolCobolt;

public class GetCurrentPowerAdapter extends CoboltAdapter	implements
																													SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolCobolt.cReadOutputPowerCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return 1000 * ProtocolCobolt.parseFloat(pMessage);
	}

}
