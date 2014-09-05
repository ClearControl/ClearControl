package rtlib.serialdevice.stage.smc100.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SMC100PositionAdapter implements SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return SMC100Protocol.cGetAbsPosCommand.getBytes();
	}

	@Override
	public Double parseValue(byte[] pMessage)
	{
		return SMC100Protocol.parseFloat(pMessage);
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return SMC100Protocol.cWaitTimeInMilliSeconds;
	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
																					double pNewValue)
	{
		String lGetPositionMessage = String.format(SMC100Protocol.cGetAbsPosCommand,
		                 													pNewValue);
		return lGetPositionMessage.getBytes();
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
