package rtlib.signalcond.devices.SIM.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolXX;

public abstract class SIMAdapter implements
																		SerialTextDeviceAdapter
{

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return ProtocolXX.cMessageTerminationCharacter;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolXX.cWaitTimeInMilliSeconds;
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pOldValue,
																					final double pNewValue)
	{
		return null;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return ProtocolXX.cMessageTerminationCharacter;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolXX.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return pMessage[0] == '!';
	}

	@Override
	public boolean hasResponseForSet()
	{
		return true;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return true;
	}

}