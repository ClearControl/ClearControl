package clearcontrol.com.serial.adapters;

public interface SerialTextDeviceAdapter<O>
                                        extends SerialDeviceAdapter<O>
{

  public Character getGetValueReturnMessageTerminationCharacter();

  public Character getSetValueReturnMessageTerminationCharacter();

}
