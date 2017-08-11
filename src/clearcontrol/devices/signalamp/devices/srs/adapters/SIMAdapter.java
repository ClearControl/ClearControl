package clearcontrol.devices.signalamp.devices.srs.adapters;

import java.util.concurrent.TimeUnit;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.devices.signalamp.devices.srs.SIM900MainframeDevice;
import clearcontrol.devices.signalamp.devices.srs.adapters.protocol.ProtocolSIM;

public abstract class SIMAdapter extends
                                 SerialDeviceAdapterAdapter<Number>
                                 implements
                                 SerialTextDeviceAdapter<Number>
{

  private final SIM900MainframeDevice mSim900MainframeDevice;
  private final int mPort;
  private final String mVariableName;

  public SIMAdapter(SIM900MainframeDevice pSim900MainframeDevice,
                    int pPort,
                    String pVariableName)
  {
    mSim900MainframeDevice = pSim900MainframeDevice;
    mPort = pPort;
    mVariableName = pVariableName;
  }

  @Override
  public byte[] getGetValueCommandMessage()
  {
    String lCommand = String.format(ProtocolSIM.cGetCommand,
                                    mVariableName);
    String lWrappedCommand =
                           mSim900MainframeDevice.wrapCommand(mPort,
                                                              lCommand);

    // System.out.println("GET sending: '" + lWrappedCommand + "'");
    return lWrappedCommand.getBytes();
  }

  @Override
  public Double parseValue(final byte[] pMessage)
  {
    Double lValue;
    try
    {
      final String lAnswer = new String(pMessage);

      // System.out.println("Received: '" + lAnswer + "'");

      int lLengthlHeaderStart = lAnswer.indexOf(',');
      String lLengthIntLengthString =
                                    lAnswer.substring(lLengthlHeaderStart
                                                      + 2,
                                                      lLengthlHeaderStart
                                                           + 3);
      int lNumberOfDigits = Integer.parseInt(lLengthIntLengthString);

      String lValueString = lAnswer.substring(lLengthlHeaderStart + 2
                                              + lNumberOfDigits
                                              + 1);

      lValue = Double.parseDouble(lValueString.trim());
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      return null;
    }
    return lValue;
  }

  @Override
  public byte[] getSetValueCommandMessage(Number pOldValue,
                                          Number pNewValue)
  {

    final String lSetCommandString =
                                   String.format(ProtocolSIM.cSetCommand,
                                                 mVariableName,
                                                 pNewValue);

    final String lWrappedSetCommandString =
                                          mSim900MainframeDevice.wrapCommand(mPort,
                                                                             lSetCommandString);

    /*System.out.println("SET sending: '" + lWrappedSetCommandString
    										+ "'");/**/

    final byte[] lWrappedSetCommandBytes =
                                         lWrappedSetCommandString.getBytes();

    return lWrappedSetCommandBytes;
  }

  @Override
  public Character getGetValueReturnMessageTerminationCharacter()
  {
    return ProtocolSIM.cMessageTerminationCharacter;
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return ProtocolSIM.cWaitTimeInMilliSeconds;
  }

  @Override
  public Character getSetValueReturnMessageTerminationCharacter()
  {
    return ProtocolSIM.cMessageTerminationCharacter;
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return ProtocolSIM.cWaitTimeInMilliSeconds;
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    return pMessage[0] == '!';
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

  @Override
  public boolean purgeAfterGet()
  {
    ThreadSleep.sleep(1, TimeUnit.MILLISECONDS);
    return true;
  }

  @Override
  public boolean purgeAfterSet()
  {
    ThreadSleep.sleep(1, TimeUnit.MILLISECONDS);
    return true;
  }

}