package clearcontrol.devices.stages.hub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Stage hub device
 *
 * @author royer
 */
public class StageHubDevice extends VirtualDevice implements
                            StageDeviceInterface,
                            StartStopDeviceInterface,
                            WaitingInterface
{

  private final StageType mStageType;

  private final ArrayList<StageDeviceInterface> mStageDeviceInterfaceList =
                                                                          new ArrayList<StageDeviceInterface>();
  private final ArrayList<StageDOF> mDOFList =
                                             new ArrayList<StageDOF>();
  private final BiMap<String, StageDOF> mNameToStageDeviceDOFMap =
                                                                 HashBiMap.create();

  /**
   * Instantiates a stage hub device with given name
   * 
   * @param pDeviceName
   *          device name
   */
  public StageHubDevice(String pDeviceName)
  {
    this(pDeviceName, StageType.Hub);
  }

  /**
   * Instantiates a stage hub device with given name and stage type
   * 
   * @param pDeviceName
   *          device name
   * @param pStageType
   *          stage type
   */
  public StageHubDevice(String pDeviceName, StageType pStageType)
  {
    super(pDeviceName);
    mStageType = pStageType;
  }

  @Override
  public StageType getStageType()
  {
    return mStageType;
  }

  /**
   * Adds a stage device DOF to this hub
   * 
   * @param pStageDeviceInterface
   *          stage device
   * @param pDOFIndex
   *          DOF index
   * @return DOF name
   */
  public String addDOF(StageDeviceInterface pStageDeviceInterface,
                       int pDOFIndex)
  {
    return addDOF(null, pStageDeviceInterface, pDOFIndex);
  }

  /**
   * Adds a stage device DOF to this hub, a new name for the DOF can be
   * provided.
   * 
   * @param pDOFName
   *          new DOF name, if null the original DOF name is used
   * @param pStageDeviceInterface
   *          stage device
   * @param pDOFIndex
   *          DOF index
   * @return DOF name (either original name or provided one)
   */
  public String addDOF(String pDOFName,
                       StageDeviceInterface pStageDeviceInterface,
                       int pDOFIndex)
  {
    mStageDeviceInterfaceList.add(pStageDeviceInterface);
    final String lDOFName =
                          pDOFName != null ? pDOFName
                                           : pStageDeviceInterface.getDOFNameByIndex(pDOFIndex);
    final StageDOF lStageDeviceDOF = new StageDOF(lDOFName,
                                                  pStageDeviceInterface,
                                                  pDOFIndex);

    mDOFList.add(lStageDeviceDOF);
    mNameToStageDeviceDOFMap.put(lDOFName, lStageDeviceDOF);
    return lDOFName;
  }

  /**
   * Returns the list of DOFs
   * 
   * @return list of DOFs
   */
  public List<StageDOF> getDOFList()
  {
    return Collections.unmodifiableList(mDOFList);
  }

  @Override
  public boolean open()
  {
    boolean lOpen = true;
    for (final StageDeviceInterface lStageDeviceInterface : new HashSet<>(mStageDeviceInterfaceList))
      lOpen &= lStageDeviceInterface.open();
    return lOpen;
  }

  @Override
  public boolean start()
  {
    boolean lStart = true;
    for (final StageDeviceInterface lStageDeviceInterface : new HashSet<>(mStageDeviceInterfaceList))
      if (lStageDeviceInterface instanceof StartStopDeviceInterface)
      {
        final StartStopDeviceInterface lStartStopDevice =
                                                        (StartStopDeviceInterface) lStageDeviceInterface;
        lStart &= lStartStopDevice.start();
      }
    return lStart;
  }

  @Override
  public boolean stop()
  {
    boolean lStop = true;
    for (final StageDeviceInterface lStageDeviceInterface : new HashSet<>(mStageDeviceInterfaceList))
      if (lStageDeviceInterface instanceof StartStopDeviceInterface)
      {
        final StartStopDeviceInterface lStartStopDevice =
                                                        (StartStopDeviceInterface) lStageDeviceInterface;
        lStop &= lStartStopDevice.stop();
      }
    return lStop;
  }

  @Override
  public boolean close()
  {
    boolean lClose = true;
    for (final StageDeviceInterface lStageDeviceInterface : new HashSet<>(mStageDeviceInterfaceList))
      lClose &= lStageDeviceInterface.close();
    return lClose;
  }

  @Override
  public int getNumberOfDOFs()
  {
    final int lNumberOFDOFs = mDOFList.size();
    return lNumberOFDOFs;
  }

  @Override
  public int getDOFIndexByName(String pName)
  {
    final StageDOF lStageDeviceDOF =
                                   mNameToStageDeviceDOFMap.get(pName);
    final int lIndex = mDOFList.indexOf(lStageDeviceDOF);
    return lIndex;
  }

  @Override
  public String getDOFNameByIndex(int pDOFIndex)
  {
    final StageDOF lStageDeviceDOF = mDOFList.get(pDOFIndex);
    final String lName = lStageDeviceDOF.getName();
    return lName;
  }

  @Override
  public void reset(int pDOFIndex)
  {
    mDOFList.get(pDOFIndex).reset();
  }

  @Override
  public void home(int pDOFIndex)
  {
    mDOFList.get(pDOFIndex).home();
  }

  @Override
  public void enable(int pDOFIndex)
  {
    mDOFList.get(pDOFIndex).enable();
  }

  @Override
  public void setTargetPosition(int pDOFIndex, double pPosition)
  {
    mDOFList.get(pDOFIndex)
            .getTargetPositionVariable()
            .set(pPosition);
  }

  @Override
  public double getTargetPosition(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getTargetPositionVariable().get();
  }

  @Override
  public double getCurrentPosition(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getCurrentPosition();
  }

  @Override
  public Boolean waitToBeReady(int pDOFIndex,
                               long pTimeOut,
                               TimeUnit pTimeUnit)
  {
    return mDOFList.get(pDOFIndex).waitToBeReady(pTimeOut, pTimeUnit);
  }

  @Override
  public Boolean waitToBeReady(long pTimeOut, TimeUnit pTimeUnit)
  {
    int lNumberOfDOFs = getNumberOfDOFs();

    Callable<Boolean> lCallable = () -> {
      for (int i = 0; i < lNumberOfDOFs; i++)
        if (!mDOFList.get(i).getReadyVariable().get())
          return false;
      return true;
    };

    return waitFor(pTimeOut, pTimeUnit, lCallable);
  }

  @Override
  public Variable<Double> getMinPositionVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getMinPositionVariable();
  }

  @Override
  public Variable<Double> getMaxPositionVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getMaxPositionVariable();
  }

  @Override
  public Variable<Double> getGranularityPositionVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getGranularityPositionVariable();
  }

  @Override
  public Variable<Boolean> getEnableVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getEnableVariable();
  }

  @Override
  public Variable<Double> getTargetPositionVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getTargetPositionVariable();
  }

  @Override
  public Variable<Double> getCurrentPositionVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getTargetPositionVariable();
  }

  @Override
  public Variable<Boolean> getReadyVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getReadyVariable();
  }

  @Override
  public Variable<Boolean> getHomingVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getHomingVariable();
  }

  @Override
  public Variable<Boolean> getStopVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getStopVariable();
  }

  @Override
  public Variable<Boolean> getResetVariable(int pDOFIndex)
  {
    return mDOFList.get(pDOFIndex).getResetVariable();
  }

  @Override
  public String toString()
  {
    return "StageHub [mDOFList=" + mDOFList
           + ", getNumberOfDOFs()="
           + getNumberOfDOFs()
           + "]";
  }

}
