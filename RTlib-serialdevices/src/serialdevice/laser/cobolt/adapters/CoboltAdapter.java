package serialdevice.laser.cobolt.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import serialdevice.laser.cobolt.adapters.protocol.ProtocolCobolt;

public abstract class CoboltAdapter	implements
																		SerialTextDeviceAdapter
{

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return ProtocolCobolt.cMessageTerminationCharacter;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolCobolt.cWaitTimeInMilliSeconds;
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pValue)
	{
		return null;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return ProtocolCobolt.cMessageTerminationCharacter;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolCobolt.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		final String lResponseString = new String(pMessage);
		return lResponseString.contains("OK");
	}

}
