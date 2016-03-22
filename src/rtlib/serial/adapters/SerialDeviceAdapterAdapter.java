package rtlib.serial.adapters;

public class SerialDeviceAdapterAdapter	implements
										SerialDeviceAdapter
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return null;
	}

	@Override
	public Double parseValue(byte[] pMessage)
	{
		return null;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return 0;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return false;
	}

	@Override
	public boolean purgeAfterGet()
	{
		return false;
	}

	@Override
	public double clampSetValue(double pValue)
	{
		return pValue;
	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
											double pNewValue)
	{
		return null;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return 0;
	}

	@Override
	public boolean hasResponseForSet()
	{
		return false;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage)
	{
		return false;
	}

	@Override
	public boolean purgeAfterSet()
	{
		return false;
	}

}
