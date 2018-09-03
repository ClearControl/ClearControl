package clearcontrol.devices.lasers.devices.cobolt.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

/**
 * base class providing common fields and methods for all Cobolt serial adapters
 *
 * @param <O>
 *          data type
 * @author royer
 */
public abstract class CoboltAdapter<O> extends
                                   SerialDeviceAdapterAdapter<O>
                                   implements
                                   SerialTextDeviceAdapter<O>
{

  @Override
  public Character getGetValueReturnMessageTerminationCharacter()
  {
    return ProtocolCobolt.cMessageTerminationCharacter;
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return ProtocolCobolt.cWaitTimeInMilliSeconds;
  }

  @Override
  public Character getSetValueReturnMessageTerminationCharacter()
  {
    return ProtocolCobolt.cMessageTerminationCharacter;
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return ProtocolCobolt.cWaitTimeInMilliSeconds;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    final String lResponseString = new String(pMessage);
    return lResponseString.contains("OK");
  }

  @Override
  public boolean hasResponseForSet()
  {
    return true;
  }

  @Override
  public boolean hasResponseForGet()
  {
    return true;
  }

}
