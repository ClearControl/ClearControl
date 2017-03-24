package clearcontrol.devices.stages.devices.smc100;

import java.util.concurrent.TimeUnit;

import clearcontrol.com.serial.SerialDevice;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100EnableAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100HomingAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100MaxPositionAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100MinPositionAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100PositionCurrentAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100PositionTargetAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100Protocol;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100ReadyAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100ResetAdapter;
import clearcontrol.devices.stages.devices.smc100.adapters.SMC100StopAdapter;

public class SMC100StageDevice extends SerialDevice implements
                               StageDeviceInterface,
                               WaitingInterface
{

  private final Variable<Boolean> mEnableVariable, mReadyVariable,
      mHomingVariable, mStopVariable, mResetVariable;
  private final Variable<Double> mTargetPositionVariable,
      mCurrentPositionVariable, mMinPositionVariable,
      mMaxPositionVariable, mGranularityPositionVariable;

  public SMC100StageDevice(String pDeviceName, String pPortName)
  {
    super(pDeviceName, pPortName, SMC100Protocol.cBaudRate);

    final SerialTextDeviceAdapter<Boolean> lEnableAdapter =
                                                          new SMC100EnableAdapter();
    mEnableVariable = addSerialVariable(pDeviceName + "Enable",
                                        lEnableAdapter);

    final SerialTextDeviceAdapter<Boolean> lReadyAdapter =
                                                         new SMC100ReadyAdapter();
    mReadyVariable = addSerialVariable(pDeviceName + "Ready",
                                       lReadyAdapter);

    final SerialTextDeviceAdapter<Boolean> lHomingAdapter =
                                                          new SMC100HomingAdapter();
    mHomingVariable = addSerialVariable(pDeviceName + "Homing",
                                        lHomingAdapter);

    final SerialTextDeviceAdapter<Double> lMinPositionAdapter =
                                                              new SMC100MinPositionAdapter();
    mMinPositionVariable = addSerialVariable(pDeviceName
                                             + "MinPosition",
                                             lMinPositionAdapter);

    final SerialTextDeviceAdapter<Double> lMaxPositionAdapter =
                                                              new SMC100MaxPositionAdapter();
    mMaxPositionVariable = addSerialVariable(pDeviceName
                                             + "MaxPosition",
                                             lMaxPositionAdapter);

    mGranularityPositionVariable = new Variable<Double>(pDeviceName
                                                        + "GranularityPosition",
                                                        0d);

    final SerialTextDeviceAdapter<Boolean> lStopAdapter =
                                                        new SMC100StopAdapter();
    mStopVariable = addSerialVariable(pDeviceName + "Stop",
                                      lStopAdapter);

    final SerialTextDeviceAdapter<Double> lTargetPositionAdapter =
                                                                 new SMC100PositionTargetAdapter(this);
    mTargetPositionVariable =
                            addSerialVariable(pDeviceName
                                              + "TargetPosition",
                                              lTargetPositionAdapter);

    final SerialTextDeviceAdapter<Double> lCurrentPositionAdapter =
                                                                  new SMC100PositionCurrentAdapter(this);
    mCurrentPositionVariable =
                             addSerialVariable(pDeviceName
                                               + "CurrentPosition",
                                               lCurrentPositionAdapter);

    final SerialTextDeviceAdapter<Boolean> lResetAdapter =
                                                         new SMC100ResetAdapter();
    mResetVariable = addSerialVariable(pDeviceName + "Reset",
                                       lResetAdapter);

  }

  @Override
  public StageType getStageType()
  {
    return StageType.Single;
  }

  @Override
  public boolean open()
  {
    final boolean lStart = super.open();

    if (lStart)
    {
      home(0);
      waitToBeReady(0, 1, TimeUnit.MINUTES);
      enable(0);
      return true;
    }
    return lStart;
  }

  @Override
  public int getNumberOfDOFs()
  {
    return 1;
  }

  @Override
  public int getDOFIndexByName(String pName)
  {
    return 0;
  }

  @Override
  public String getDOFNameByIndex(int pIndex)
  {
    return getName();
  }

  @Override
  public void reset(int pIndex)
  {
    mResetVariable.set(false);
    mResetVariable.set(true);
  }

  @Override
  public void home(int pIndex)
  {
    mResetVariable.set(false);
    mResetVariable.set(true);
  }

  @Override
  public void enable(int pIndex)
  {
    mResetVariable.set(false);
    mResetVariable.set(true);
  }

  public void setMinimumPosition(double pMinimumPosition)
  {
    mMinPositionVariable.set(pMinimumPosition);
  }

  public void setMaximumPosition(double pMinimumPosition)
  {
    mMaxPositionVariable.set(pMinimumPosition);
  }

  @Override
  public void setTargetPosition(int pIndex, double pPosition)
  {
    mTargetPositionVariable.set(pPosition);
  }

  @Override
  public double getTargetPosition(int pIndex)
  {
    return mTargetPositionVariable.get();
  }

  @Override
  public double getCurrentPosition(int pIndex)
  {
    return mCurrentPositionVariable.get();
  }

  @Override
  public Variable<Double> getMinPositionVariable(int pIndex)
  {
    return mMinPositionVariable;
  }

  @Override
  public Variable<Double> getMaxPositionVariable(int pIndex)
  {
    return mMaxPositionVariable;
  }

  @Override
  public Variable<Double> getGranularityPositionVariable(int pDOFIndex)
  {
    return mGranularityPositionVariable;
  }

  @Override
  public Variable<Boolean> getEnableVariable(int pIndex)
  {
    return mEnableVariable;
  }

  @Override
  public Variable<Boolean> getHomingVariable(int pIndex)
  {
    return mHomingVariable;
  }

  @Override
  public Variable<Double> getTargetPositionVariable(int pIndex)
  {
    return mTargetPositionVariable;
  }

  @Override
  public Variable<Double> getCurrentPositionVariable(int pIndex)
  {
    return mCurrentPositionVariable;
  }

  @Override
  public Variable<Boolean> getReadyVariable(int pIndex)
  {
    return mReadyVariable;
  }

  @Override
  public Variable<Boolean> getStopVariable(int pIndex)
  {
    return mStopVariable;
  }

  @Override
  public Variable<Boolean> getResetVariable(int pIndex)
  {
    return mResetVariable;
  }

  @Override
  public Boolean waitToBeReady(int pIndex,
                               long pTimeOut,
                               TimeUnit pTimeUnit)
  {
    return waitFor(pTimeOut, pTimeUnit, () -> mReadyVariable.get());
  }

  @Override
  public Boolean waitToBeReady(long pTimeOut, TimeUnit pTimeUnit)
  {
    return waitToBeReady(0, pTimeOut, pTimeUnit);
  }

  @Override
  public String toString()
  {
    return "SMC100StageDevice [mSerial=" + getSerial()
           + ", getNumberOfDOFs()="
           + getNumberOfDOFs()
           + ", getDeviceName()="
           + getName()
           + "]";
  }

}
