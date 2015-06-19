package rtlib.stages.devices.smc100.adapters;

import rtlib.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class SMC100MinPositionAdapter	extends
																			SerialDeviceAdapterAdapter implements
																																SerialTextDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return SMC100Protocol.cGetMinPosCommand.getBytes();
	}

	@Override
	public Double parseValue(byte[] pMessage)
	{
		return 1000 * SMC100Protocol.parseFloat(SMC100Protocol.cGetMinPosCommand,
																			pMessage);
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
		String lSetMinPositionMessage = String.format(	SMC100Protocol.cSetMinPosCommand,
																								pNewValue * 0.001);
		return lSetMinPositionMessage.getBytes();
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
		return true;
	}

}
