package rtlib.serial.adapters;

public interface SerialTextDeviceAdapter extends SerialDeviceAdapter
{

	public Character getGetValueReturnMessageTerminationCharacter();

	public Character getSetValueReturnMessageTerminationCharacter();

	public boolean hasResponseForGet();

	public boolean hasResponseForSet();
}
