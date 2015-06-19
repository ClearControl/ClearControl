package rtlib.serial.adapters;

public interface SerialDeviceAdapter
{

	// GET RELATED:

	public byte[] getGetValueCommandMessage();

	public Double parseValue(byte[] pMessage);

	public long getGetValueReturnWaitTimeInMilliseconds();

	public boolean hasResponseForGet();

	public boolean purgeAfterGet();

	
	
	// SET RELATED:
	
	public double clampSetValue(double pNewValue);
	
	public byte[] getSetValueCommandMessage(double pOldValue,
																					double pNewValue);

	public long getSetValueReturnWaitTimeInMilliseconds();

	public boolean hasResponseForSet();

	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage);

	public boolean purgeAfterSet();

}
