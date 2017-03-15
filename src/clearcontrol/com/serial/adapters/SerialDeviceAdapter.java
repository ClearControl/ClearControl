package clearcontrol.com.serial.adapters;

public interface SerialDeviceAdapter<O>
{

  // GET RELATED:

  public byte[] getGetValueCommandMessage();

  public O parseValue(byte[] pMessage);

  public long getGetValueReturnWaitTimeInMilliseconds();

  public boolean hasResponseForGet();

  public boolean purgeAfterGet();

  // SET RELATED:

  public O clampSetValue(O pNewValue);

  public byte[] getSetValueCommandMessage(O pOldValue, O pNewValue);

  public long getSetValueReturnWaitTimeInMilliseconds();

  public boolean hasResponseForSet();

  public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage);

  public boolean purgeAfterSet();

}
