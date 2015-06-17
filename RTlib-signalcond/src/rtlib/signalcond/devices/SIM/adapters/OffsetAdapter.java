package rtlib.signalcond.devices.SIM.adapters;

import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolXX;

public class OffsetAdapter extends SIMAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolXX.cGetOffsetCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return 0.0;
	}

}
