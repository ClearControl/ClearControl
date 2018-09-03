package clearcontrol.devices.stages.devices.smc100.adapters;

import java.util.concurrent.TimeUnit;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.devices.smc100.SMC100StageDevice;

public class SMC100PositionTargetAdapter extends
                                         SerialDeviceAdapterAdapter<Double>
                                         implements
                                         SerialTextDeviceAdapter<Double>
{
  protected static final double cEpsilon = 0.1; // 100nm

  private Variable<Boolean> mReadyVariable;
  private Variable<Boolean> mStopVariable;
  private Variable<Double> mMinPositionVariable;
  private Variable<Double> mMaxPositionVariable;

  private SMC100StageDevice mSmc100StageDevice;

  public SMC100PositionTargetAdapter(SMC100StageDevice pSmc100StageDevice)
  {
    mSmc100StageDevice = pSmc100StageDevice;
    mReadyVariable = pSmc100StageDevice.getReadyVariable(0);
    mStopVariable = pSmc100StageDevice.getStopVariable(0);
    mMinPositionVariable =
                         pSmc100StageDevice.getMinPositionVariable(0);
    mMaxPositionVariable =
                         pSmc100StageDevice.getMaxPositionVariable(0);
  }

  @Override
  public byte[] getSetValueCommandMessage(Double pOldValue,
                                          Double pNewValue)
  {
    double lMinPosition = mMinPositionVariable.get();
    double lMaxPosition = mMaxPositionVariable.get();

    if (pNewValue < lMinPosition)
      pNewValue = lMinPosition + cEpsilon;
    else if (pNewValue > lMaxPosition)
      pNewValue = lMaxPosition - cEpsilon;

    boolean lIsReady = mReadyVariable.get();

    if (!lIsReady)
    {
      // System.out.println("Not ready-> stopping");
      mStopVariable.set(false);
      mStopVariable.set(true);
      mSmc100StageDevice.waitToBeReady(0, 5, TimeUnit.SECONDS);
    }

    String lSetPositionMessage =
                               String.format(SMC100Protocol.cSetAbsPosCommand,
                                             pNewValue * 0.001);
    // System.out.println(lSetPositionMessage);
    return lSetPositionMessage.getBytes();
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

}
