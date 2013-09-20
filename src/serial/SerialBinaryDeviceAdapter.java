package serial;

public interface SerialBinaryDeviceAdapter
{

	public byte[] getGetValueCommandMessage();

	public int getGetValueReturnMessageLength();

	public Double parseValue(byte[] pMessage);
	
	public long getGetValueReturnWaitTimeInMilliseconds();

	public byte[] getSetValueCommandMessage(double value);

	public int getSetValueReturnMessageLength();
	
	public long getSetValueReturnWaitTimeInMilliseconds();

	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage);





}
