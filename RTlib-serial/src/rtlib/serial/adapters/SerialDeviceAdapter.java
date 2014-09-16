package rtlib.serial.adapters;

public interface SerialDeviceAdapter
{

	public byte[] getGetValueCommandMessage();

	public Double parseValue(byte[] pMessage);

	public long getGetValueReturnWaitTimeInMilliseconds();

	public boolean hasResponseForGet();

	public byte[] getSetValueCommandMessage(double pOldValue,
																					double pNewValue);

	public long getSetValueReturnWaitTimeInMilliseconds();

	public boolean hasResponseForSet();

	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage);

}
