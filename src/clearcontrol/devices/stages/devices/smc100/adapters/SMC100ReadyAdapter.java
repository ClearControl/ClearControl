package clearcontrol.devices.stages.devices.smc100.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;

public class SMC100ReadyAdapter extends
                                SerialDeviceAdapterAdapter<Boolean>
                                implements
                                SerialTextDeviceAdapter<Boolean>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return SMC100Protocol.cGetStateCommand.getBytes();
  }

  @Override
  public Boolean parseValue(byte[] pMessage)
  {
    return SMC100Protocol.parseReadyFromState(pMessage);
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return SMC100Protocol.cWaitTimeInMilliSeconds;
  }

  @Override
  public byte[] getSetValueCommandMessage(Boolean pOldValue,
                                          Boolean pNewValue)
  {
    return null;
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return SMC100Protocol.cWaitTimeInMilliSeconds;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage)
  {
    return true;
  }

  @Override
  public Character getGetValueReturnMessageTerminationCharacter()
  {
    return SMC100Protocol.cMessageTerminationCharacter;
  }

  @Override
  public Character getSetValueReturnMessageTerminationCharacter()
  {
    return SMC100Protocol.cMessageTerminationCharacter;
  }

  @Override
  public boolean hasResponseForSet()
  {
    return false;
  }

  @Override
  public boolean hasResponseForGet()
  {
    return true;
  }

}
