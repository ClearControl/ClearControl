package serial.adapters;

public interface SerialDeviceAdapter
{

	public byte[] getGetValueCommandMessage();

	public Double parseValue(byte[] pMessage);

	public long getGetValueReturnWaitTimeInMilliseconds();

	public byte[] getSetValueCommandMessage(double value);

	public long getSetValueReturnWaitTimeInMilliseconds();

	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage);

}
