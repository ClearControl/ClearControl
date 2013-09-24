package serial.adapters;

public interface SerialTextDeviceAdapter extends SerialDeviceAdapter
{

	public Character getGetValueReturnMessageTerminationCharacter();

	public Character getSetValueReturnMessageTerminationCharacter();

}
