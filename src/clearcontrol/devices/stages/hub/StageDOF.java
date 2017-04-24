package clearcontrol.devices.stages.hub;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableBase;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceInterface;

/**
 * Stage DOF for stage hub devices
 *
 * @author royer
 */
public class StageDOF extends NameableBase
{
  private final StageDeviceInterface mStageDevice;
  private final int mDOFIndex;

  /**
   * Instantiates a stage DOF
   * 
   * @param pDOFName
   *          DOF name
   * @param pStageDevice
   *          stage device
   * @param pDOFIndex
   *          DOF index
   */
  public StageDOF(String pDOFName,
                  StageDeviceInterface pStageDevice,
                  int pDOFIndex)
  {
    super(pDOFName);
    mStageDevice = pStageDevice;
    mDOFIndex = pDOFIndex;
  }

  /**
   * Returns corresponding stage device
   * 
   * @return stage device
   */
  public StageDeviceInterface getStageDevice()
  {
    return mStageDevice;
  }

  /**
   * Returns this DOF's index
   * 
   * @return DOF's index
   */
  public int getDOFIndex()
  {
    return mDOFIndex;
  }

  /**
   * Resets DOF
   */
  public void reset()
  {
    mStageDevice.reset(mDOFIndex);
  }

  /**
   * Homes DOF
   */
  public void home()
  {
    mStageDevice.home(mDOFIndex);
  }

  /**
   * Enables DOF
   */
  public void enable()
  {
    mStageDevice.enable(mDOFIndex);
  }

  /**
   * Returns the DOF's current position
   * 
   * @return DOF's current position
   */
  public double getCurrentPosition()
  {
    return mStageDevice.getCurrentPosition(mDOFIndex);
  }

  /**
   * Waits until the DOF is ready.
   * 
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time unit
   * @return true -> no timeout, false otherwise
   */
  public Boolean waitToBeReady(long pTimeOut, TimeUnit pTimeUnit)
  {
    return mStageDevice.waitToBeReady(mDOFIndex, pTimeOut, pTimeUnit);
  }

  /**
   * Returns the DOF's min position variable
   * 
   * @return min position variable
   */
  public Variable<Double> getMinPositionVariable()
  {
    return mStageDevice.getMinPositionVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's max position variable
   * 
   * @return max position variable
   */
  public Variable<Double> getMaxPositionVariable()
  {
    return mStageDevice.getMaxPositionVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's position granularity variable
   * 
   * @return position granularity variable
   */
  public Variable<Double> getGranularityPositionVariable()
  {
    return mStageDevice.getGranularityPositionVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's enable variable
   * 
   * @return enable variable
   */
  public Variable<Boolean> getEnableVariable()
  {
    return mStageDevice.getEnableVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's target position variable
   * 
   * @return DOF's target position variable
   */
  public Variable<Double> getTargetPositionVariable()
  {
    return mStageDevice.getTargetPositionVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's current position variable
   * 
   * @return DOF's current position variable
   */
  public Variable<Double> getCurrentPositionVariable()
  {
    return mStageDevice.getCurrentPositionVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's ready variable
   * 
   * @return ready variable
   */
  public Variable<Boolean> getReadyVariable()
  {
    return mStageDevice.getReadyVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's homing variable
   * 
   * @return homing variable
   */
  public Variable<Boolean> getHomingVariable()
  {
    return mStageDevice.getHomingVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's stop variable
   * 
   * @return stop variable
   */
  public Variable<Boolean> getStopVariable()
  {
    return mStageDevice.getStopVariable(mDOFIndex);
  }

  /**
   * Returns the DOF's reset variable
   * 
   * @return reset variable
   */
  public Variable<Boolean> getResetVariable()
  {
    return mStageDevice.getStopVariable(mDOFIndex);
  }

  @Override
  public String toString()
  {
    return String.format("StageDeviceDOF [name=%s,  device=%s, DOF index=%d]",
                         getName(),
                         mStageDevice,
                         mDOFIndex);
  }

}
