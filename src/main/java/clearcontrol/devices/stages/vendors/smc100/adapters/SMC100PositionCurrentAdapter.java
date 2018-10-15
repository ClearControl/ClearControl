package clearcontrol.devices.stages.devices.smc100.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.devices.smc100.SMC100StageDevice;

public class SMC100PositionCurrentAdapter extends
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

  public SMC100PositionCurrentAdapter(SMC100StageDevice pSmc100StageDevice)
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
  public byte[] getGetValueCommandMessage()
  {
    return SMC100Protocol.cGetAbsPosCommand.getBytes();
  }

  @Override
  public Double parseValue(byte[] pMessage)
  {
    return 1000
           * SMC100Protocol.parseFloat(SMC100Protocol.cGetAbsPosCommand,
                                       pMessage);
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return SMC100Protocol.cWaitTimeInMilliSeconds;
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
  public boolean hasResponseForGet()
  {
    return true;
  }

}
