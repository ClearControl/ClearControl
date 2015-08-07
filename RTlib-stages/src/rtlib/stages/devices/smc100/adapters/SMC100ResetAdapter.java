package rtlib.stages.devices.smc100.adapters;

import rtlib.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SMC100ResetAdapter extends SerialDeviceAdapterAdapter	implements
																	SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return null;
	}

	@Override
	public Double parseValue(byte[] pMessage)
	{
		return 0.0;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return 0;
	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
											double pNewValue)
	{
		if (pOldValue == 0 && pNewValue > 0)
			return SMC100Protocol.cResetCommand.getBytes();
		else
			return null;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return SMC100Protocol.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage)
	{
		return true;
	}

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return SMC100Protocol.cMessageTerminationCharacter;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return SMC100Protocol.cMessageTerminationCharacter;
	}

	@Override
	public boolean hasResponseForSet()
	{
		return false;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return false;
	}

}
