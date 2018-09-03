package clearcontrol.com.serial.adapters;

public class SerialDeviceAdapterAdapter<O> implements
                                       SerialDeviceAdapter<O>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return null;
  }

  @Override
  public O parseValue(byte[] pMessage)
  {
    return null;
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return 0;
  }

  @Override
  public boolean hasResponseForGet()
  {
    return false;
  }

  @Override
  public boolean purgeAfterGet()
  {
    return false;
  }

  @Override
  public O clampSetValue(O pValue)
  {
    return pValue;
  }

  @Override
  public byte[] getSetValueCommandMessage(O pOldValue, O pNewValue)
  {
    return null;
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return 0;
  }

  @Override
  public boolean hasResponseForSet()
  {
    return false;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage)
  {
    return false;
  }

  @Override
  public boolean purgeAfterSet()
  {
    return false;
  }

}
