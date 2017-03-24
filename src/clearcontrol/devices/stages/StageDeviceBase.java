package clearcontrol.devices.stages;

import java.util.ArrayList;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class StageDeviceBase extends VirtualDevice implements
                                      StageDeviceInterface,
                                      WaitingInterface
{
  protected ArrayList<Variable<Boolean>> mEnableVariables,
      mReadyVariables, mHomingVariables, mStopVariables,
      mResetVariables;
  protected ArrayList<Variable<Double>> mTargetPositionVariables,
      mCurrentPositionVariables, mMinPositionVariables,
      mMaxPositionVariables, mGranularityPositionVariables;

  protected final BiMap<Integer, String> mIndexToNameMap =
                                                         HashBiMap.create();

  public StageDeviceBase(String pDeviceName)
  {
    super(pDeviceName);
    mEnableVariables = new ArrayList<>();
    mReadyVariables = new ArrayList<>();
    mHomingVariables = new ArrayList<>();
    mStopVariables = new ArrayList<>();
    mResetVariables = new ArrayList<>();

    mTargetPositionVariables = new ArrayList<>();
    mCurrentPositionVariables = new ArrayList<>();
    mMinPositionVariables = new ArrayList<>();
    mMaxPositionVariables = new ArrayList<>();
    mGranularityPositionVariables = new ArrayList<>();
  }

  @Override
  public abstract StageType getStageType();

  @Override
  public int getNumberOfDOFs()
  {
    return mEnableVariables.size();
  }

  @Override
  public Variable<Double> getTargetPositionVariable(int pIndex)
  {
    return mTargetPositionVariables.get(pIndex);
  }

  @Override
  public Variable<Double> getCurrentPositionVariable(int pDOFIndex)
  {
    return mCurrentPositionVariables.get(pDOFIndex);
  }

  @Override
  public Variable<Double> getMinPositionVariable(int pIndex)
  {
    return mMinPositionVariables.get(pIndex);
  }

  @Override
  public Variable<Double> getMaxPositionVariable(int pIndex)
  {
    return mMaxPositionVariables.get(pIndex);
  }

  @Override
  public Variable<Double> getGranularityPositionVariable(int pDOFIndex)
  {
    return mGranularityPositionVariables.get(pDOFIndex);
  }

  @Override
  public Variable<Boolean> getEnableVariable(int pIndex)
  {
    return mEnableVariables.get(pIndex);
  }

  @Override
  public Variable<Boolean> getReadyVariable(int pIndex)
  {
    return mReadyVariables.get(pIndex);
  }

  @Override
  public Variable<Boolean> getHomingVariable(int pIndex)
  {
    return mHomingVariables.get(pIndex);
  }

  @Override
  public Variable<Boolean> getStopVariable(int pIndex)
  {
    return mStopVariables.get(pIndex);
  }

  @Override
  public Variable<Boolean> getResetVariable(int pIndex)
  {
    return mResetVariables.get(pIndex);
  }

  @Override
  public int getDOFIndexByName(String pName)
  {
    return mIndexToNameMap.inverse().get(pName);
  }

  @Override
  public String getDOFNameByIndex(int pIndex)
  {
    return mIndexToNameMap.get(pIndex);
  }

}
