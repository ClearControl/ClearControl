package serial.adapters;

public interface SerialBinaryDeviceAdapter extends SerialDeviceAdapter
{
	public int getGetValueReturnMessageLength();

	public int getSetValueReturnMessageLength();
	
}
