package rtlib.signalcond.devices.SIM.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolXX;

public class GainAdapter extends SIMAdapter	implements
																						SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetGainCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{

		return 0.0;
	}

}
