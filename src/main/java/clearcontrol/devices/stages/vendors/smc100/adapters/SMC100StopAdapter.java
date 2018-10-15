package clearcontrol.devices.stages.devices.smc100.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;

public class SMC100StopAdapter extends
                               SerialDeviceAdapterAdapter<Boolean>
                               implements
                               SerialTextDeviceAdapter<Boolean>
{

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return null;
  }

  @Override
  public Boolean parseValue(byte[] pMessage)
  {
    return false;
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return 0;
  }

  @Override
  public byte[] getSetValueCommandMessage(Boolean pOldValue,
                                          Boolean pNewValue)
  {
    if (pOldValue == false && pNewValue == true)
    {
      // System.out.println("Stopping!");
      return SMC100Protocol.cStopCommand.getBytes();
    }
    else
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
    return false;
  }

}
