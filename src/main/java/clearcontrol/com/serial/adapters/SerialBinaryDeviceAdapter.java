package clearcontrol.com.serial.adapters;

public interface SerialBinaryDeviceAdapter<O> extends
                                          SerialDeviceAdapter<O>
{
  public int getGetValueReturnMessageLength();

  public int getSetValueReturnMessageLength();

}
