package serial;

public interface SerialBinaryDevice
{

	public byte[] getGetValueCommandMessage();

	public int getGetValueReturnMessageLength();

	public double parseValue(byte[] pMessage);

	public byte[] getSetValueCommandMessage(double value);

	public int getSetValueReturnMessageLength();

	public double checkAcknowledgementSetValueReturnMessage(byte[] pMessage);

}
