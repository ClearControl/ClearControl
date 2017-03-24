package clearcontrol.devices.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.devices.lasers.devices.omicron.adapters.protocol.ProtocolOmicron;

/**
 * Base class providing common fields and methods for all Omicron serial device
 * adapters.
 *
 * @author royer
 * @param <O>
 *          data type
 */
public abstract class OmicronAdapter<O> extends
                                    SerialDeviceAdapterAdapter<O>
                                    implements
                                    SerialTextDeviceAdapter<O>
{

  @Override
  public Character getGetValueReturnMessageTerminationCharacter()
  {
    return ProtocolOmicron.cMessageTerminationCharacter;
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return ProtocolOmicron.cWaitTimeInMilliSeconds;
  }

  @Override
  public Character getSetValueReturnMessageTerminationCharacter()
  {
    return ProtocolOmicron.cMessageTerminationCharacter;
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return ProtocolOmicron.cWaitTimeInMilliSeconds;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    return pMessage[0] == '!';
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
