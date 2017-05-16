package clearcontrol.microscope.lightsheet.state;

import static java.lang.Math.round;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.state.tables.InterpolationTables;
import clearcontrol.microscope.state.AcquisitionStateBase;

/**
 * Inetrpolated acquisition state
 *
 * @author royer
 */
public class InterpolatedAcquisitionState extends
                                          AcquisitionStateBase<LightSheetMicroscopeInterface, LightSheetMicroscopeQueue>
                                          implements
                                          LightSheetAcquisitionStateInterface<InterpolatedAcquisitionState>,
                                          LoggingInterface,
                                          Cloneable

{

  private int mNumberOfDetectionArms, mNumberOfLightSheets,
      mNumberOfLaserLines;

  private BoundedVariable<Number> mZLow;
  private BoundedVariable<Number> mZHigh;

  private final BoundedVariable<Number> mZStep =
                                               new BoundedVariable<Number>("ZStep",
                                                                           1,
                                                                           0,
                                                                           1000);

  private final Variable<Number> mZPlanes =
                                          new Variable<Number>("ZPlanes",
                                                               1);

  private final Variable<Boolean>[] mCameraOnOff, mLightSheetOnOff,
      mLaserOnOff;

  private final InterpolationTables mInterpolationTables;

  private volatile boolean mQueueUpdateNeeded = true;

  /**
   * Instantiates an interpolated acquisition state
   * 
   * @param pName
   *          acquisition state name
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
  @SuppressWarnings("unchecked")
  public InterpolatedAcquisitionState(String pName,
                                      LightSheetMicroscopeInterface pLightSheetMicroscope)
  {
    super(pName, pLightSheetMicroscope);

    mNumberOfDetectionArms =
                           getMicroscope().getNumberOfDetectionArms();
    mNumberOfLightSheets = getMicroscope().getNumberOfLightSheets();
    mNumberOfLaserLines = getMicroscope().getNumberOfLaserLines();

    @SuppressWarnings("rawtypes")
    final VariableSetListener lChangeListener = (o, n) -> {
      // info("State changed!");
      mQueueUpdateNeeded = true;
      notifyListeners(this);
    };

    DetectionArmInterface lDetectionArm =
                                        getMicroscope().getDetectionArm(0);

    if (lDetectionArm == null)
    {
      mZLow = new BoundedVariable<Number>("LowZ",
                                          -117.0,
                                          -200.0,
                                          200.0,
                                          1.0);
      mZHigh =
             new BoundedVariable<Number>("HighZ",
                                         +117.0,
                                         -200.0,
                                         200.0,
                                         1.0);

    }
    else
    {

      mZLow = new BoundedVariable<Number>("LowZ",
                                          lDetectionArm.getZVariable()
                                                       .getMin()
                                                       .doubleValue(),
                                          lDetectionArm.getZVariable()
                                                       .getMin(),
                                          lDetectionArm.getZVariable()
                                                       .getMax());
      mZHigh = new BoundedVariable<Number>("HighZ",
                                           lDetectionArm.getZVariable()
                                                        .getMax()
                                                        .doubleValue(),
                                           lDetectionArm.getZVariable()
                                                        .getMin(),
                                           lDetectionArm.getZVariable()
                                                        .getMax());
    }

    mInterpolationTables =
                         new InterpolationTables(mNumberOfDetectionArms,
                                                 mNumberOfLightSheets);

    mInterpolationTables.addChangeListener((e) -> {
      // info("Interpolation table changed!");
      notifyListeners(this);
    });

    {
      addControlPlane(mZLow.get().doubleValue());
      addControlPlane(mZHigh.get().doubleValue());

      setupDefaultValues();
    }

    mCameraOnOff = new Variable[mNumberOfDetectionArms];

    for (int i = 0; i < mCameraOnOff.length; i++)
    {
      mCameraOnOff[i] =
                      new Variable<Boolean>(String.format("Camera%dOnOff",
                                                          i),
                                            true);
      mCameraOnOff[i].addSetListener(lChangeListener);
    }

    mLightSheetOnOff = new Variable[mNumberOfLightSheets];

    for (int i = 0; i < mLightSheetOnOff.length; i++)
    {
      mLightSheetOnOff[i] = new Variable<Boolean>(
                                                  String.format("LightSheet%dOnOff",
                                                                i),
                                                  true);
      mLightSheetOnOff[i].addSetListener(lChangeListener);
    }

    mLaserOnOff = new Variable[mNumberOfLaserLines];

    for (int i = 0; i < mLaserOnOff.length; i++)
    {
      mLaserOnOff[i] =
                     new Variable<Boolean>(String.format("Laser%dOnOff",
                                                         i),
                                           true);

      mLaserOnOff[i].addSetListener(lChangeListener);
    }

    // Range listener and adjustement of the
    {
      VariableSetListener<Number> lRangeListener = (o, n) -> {
        if (n != null && !n.equals(o))
          setNumberOfPlanesAndZStepBasedOnRange();
      };

      mZLow.addSetListener(lRangeListener);
      mZHigh.addSetListener(lRangeListener);
      mZStep.addSetListener((o, n) -> {
        if (n != null && !n.equals(o))
          setNumberOfPlanesBasedOnZStep(n);
      });

      mZPlanes.addSetListener((o, n) -> {
        if (n != null && !n.equals(o))
          setZStepBasedOnNumberOfPlanes(n);
      });

      mZLow.addSetListener(lChangeListener);
      mZHigh.addSetListener(lChangeListener);
      mZStep.addSetListener(lChangeListener);
      mZPlanes.addSetListener(lChangeListener);

      mZPlanes.set(128);
      setZStepBasedOnNumberOfPlanes(128);
    }

    StageDeviceInterface lMainXYZRStage =
                                        getMicroscope().getMainStage();

    if (lMainXYZRStage != null)
    {

      getStageXVariable().setMinMax(lMainXYZRStage.getMinPositionVariable(0)
                                                  .get(),
                                    lMainXYZRStage.getMaxPositionVariable(0)
                                                  .get());

      getStageYVariable().setMinMax(lMainXYZRStage.getMinPositionVariable(1)
                                                  .get(),
                                    lMainXYZRStage.getMaxPositionVariable(1)
                                                  .get());

      getStageZVariable().setMinMax(lMainXYZRStage.getMinPositionVariable(2)
                                                  .get(),
                                    lMainXYZRStage.getMaxPositionVariable(2)
                                                  .get());
    }
    else
    {
      warning("No main stage defined, using generic range for X,Y and Z");

      getStageXVariable().setMinMax(-100, 100);

      getStageYVariable().setMinMax(-100, 100);

      getStageZVariable().setMinMax(-100, 100);
    }

  }

  protected void setNumberOfPlanesAndZStepBasedOnRange()
  {
    {
      double lZStep = mZStep.get().doubleValue();
      long lZPlanes = 1 + (long) (getStackDepthInMicrons() / lZStep);

      if (!Double.isNaN(lZPlanes)
          && mZPlanes.get().longValue() != lZPlanes)
        mZPlanes.set(lZPlanes);

      if (!Double.isNaN(lZStep)
          && mZStep.get().doubleValue() != lZStep)
        mZStep.set(lZStep);

      // System.out.println("lZPlanes=" + lZPlanes);
    }
  }

  protected void setZStepBasedOnNumberOfPlanes(Number pNumberofPlanes)
  {
    double lZStep = (getStackDepthInMicrons()
                     / (pNumberofPlanes.doubleValue() - 1));
    if (!Double.isNaN(lZStep) && mZStep.get().doubleValue() != lZStep)
      mZStep.set(lZStep);

    // System.out.println("lStepZ=" + lZStep);
  }

  protected void setNumberOfPlanesBasedOnZStep(Number pZStep)
  {
    long lZPlanes = 1 + (long) (getStackDepthInMicrons()
                                / pZStep.doubleValue());
    if (!Double.isNaN(lZPlanes)
        && mZPlanes.get().longValue() != lZPlanes)
      mZPlanes.set(lZPlanes);

    // System.out.println("lZPlanes=" + lZPlanes);
  }

  /**
   * Copy constructor
   * 
   * @param pName
   *          interpolated acquisition state name
   * @param pInterpolatedAcquisitionState
   *          acquisition state to use as template
   * 
   */
  public InterpolatedAcquisitionState(String pName,
                                      InterpolatedAcquisitionState pInterpolatedAcquisitionState)
  {
    this(pName, pInterpolatedAcquisitionState.getMicroscope());

    set(pInterpolatedAcquisitionState);
  }

  protected void set(InterpolatedAcquisitionState pInterpolatedAcquisitionState)
  {
    super.set(pInterpolatedAcquisitionState);

    getInterpolationTables().set(pInterpolatedAcquisitionState.getInterpolationTables());

    getStackZLowVariable().set(pInterpolatedAcquisitionState.getStackZLowVariable());
    getStackZHighVariable().set(pInterpolatedAcquisitionState.getStackZHighVariable());
    getStackZStepVariable().set(pInterpolatedAcquisitionState.getStackZStepVariable());
    getStackNumberOfPlanesVariable().set(pInterpolatedAcquisitionState.getStackNumberOfPlanesVariable());

    for (int i = 0; i < mCameraOnOff.length; i++)
      mCameraOnOff[i] =
                      pInterpolatedAcquisitionState.getCameraOnOffVariable(i);

    for (int i = 0; i < mLightSheetOnOff.length; i++)
      mLightSheetOnOff[i] =
                          pInterpolatedAcquisitionState.getLightSheetOnOffVariable(i);

    for (int i = 0; i < mLaserOnOff.length; i++)
      mLaserOnOff[i] =
                     pInterpolatedAcquisitionState.getLaserOnOffVariable(i);

  }

  @Override
  public InterpolatedAcquisitionState copy(String pName)
  {
    return new InterpolatedAcquisitionState(pName, this);
  }

  /**
   * Setting up control planes
   * 
   * @param pNumberOfControlPlanes
   *          number of control planes
   * @param pMarginZ
   *          distance between Z-range extremities and first and last control
   *          planes
   */
  public void setupControlPlanes(int pNumberOfControlPlanes,
                                 double pMarginZ)
  {
    double lHighZ = getStackZHighVariable().get().doubleValue();
    double lLowZ = getStackZLowVariable().get().doubleValue();

    double lIntervalBetweenControlPlanes = (lHighZ - lLowZ
                                            - 2 * pMarginZ)
                                           / (pNumberOfControlPlanes
                                              - 1);

    InterpolationTables lInterpolationTables =
                                             new InterpolationTables(mInterpolationTables);
    lInterpolationTables.removeAllControlPlanes();

    for (int cpi = 0; cpi < pNumberOfControlPlanes; cpi++)
    {
      double z =
               lLowZ + pMarginZ + cpi * lIntervalBetweenControlPlanes;
      lInterpolationTables.addControlPlane(mInterpolationTables, z);
    }

    mInterpolationTables.set(lInterpolationTables);

    notifyListeners(this);
  }

  protected void setupDefaultValues()
  {
    Number lMaxHeight = new Double(1);

    if (getMicroscope() != null)
    {
      lMaxHeight = getMicroscope().getLightSheet(0)
                                  .getHeightVariable()
                                  .getMax();
    }

    for (int zpi =
                 0; zpi < getInterpolationTables().getNumberOfControlPlanes(); zpi++)
    {
      getInterpolationTables().set(LightSheetDOF.IH,
                                   zpi,
                                   lMaxHeight.doubleValue());
      getInterpolationTables().set(LightSheetDOF.IP, zpi, 1);
    }
  }

  /**
   * Copies current microscope settings such as exposure and stage position to
   * this acquisition state
   */
  public void copyCurrentMicroscopeSettings()
  {
    double lExposureInSeconds = getMicroscope().getExposure(0);
    getExposureInSecondsVariable().set(lExposureInSeconds);

    getImageWidthVariable().set(getMicroscope().getCameraWidth(0));
    getImageHeightVariable().set(getMicroscope().getCameraHeight(0));

    try
    {
      getStageXVariable().set(getMicroscope().getStageX());
      getStageYVariable().set(getMicroscope().getStageY());
      getStageZVariable().set(getMicroscope().getStageZ());
    }
    catch (Exception e)
    {
      // e.printStackTrace();
    }

    int lNumberOfControlPlanes = getNumberOfControlPlanes();

    for (int cpi = 0; cpi < lNumberOfControlPlanes; cpi++)
    {
      for (int l = 0; l < mNumberOfLightSheets; l++)
      {
        double lHeight = getMicroscope().getLightSheet(l)
                                        .getHeightVariable()
                                        .get()
                                        .doubleValue();
        double lPower = getMicroscope().getLightSheet(l)
                                       .getPowerVariable()
                                       .get()
                                       .doubleValue();

        mInterpolationTables.set(LightSheetDOF.IH, cpi, l, lHeight);
        mInterpolationTables.set(LightSheetDOF.IP, cpi, l, lPower);
      }
    }
  }

  @Override
  public void prepareAcquisition(long pTimeOut, TimeUnit pTimeUnit)
  {
    applyStagePosition(pTimeOut, pTimeUnit);
  }

  protected void applyStagePosition(long pTimeOut, TimeUnit pTimeUnit)
  {
    StageDeviceInterface lMainXYZRStage =
                                        getMicroscope().getMainStage();
    if (lMainXYZRStage == null)
      return;

    lMainXYZRStage.enable();

    double lStageX = getStageXVariable().get().doubleValue();
    double lStageY = getStageYVariable().get().doubleValue();
    double lStageZ = getStageZVariable().get().doubleValue();

    getMicroscope().setStageX(lStageX);
    getMicroscope().setStageY(lStageY);
    getMicroscope().setStageZ(lStageZ);

    getMicroscope().getMainStage().waitToBeReady(pTimeOut, pTimeUnit);
  }

  /**
   * Updated the queue
   * 
   * @param pForceUpdate
   *          forces update of the queue
   */
  public void updateQueue(boolean pForceUpdate)
  {
    if (mQueueUpdateNeeded || pForceUpdate)
    {
      mQueue = getQueue(0,
                        mNumberOfDetectionArms,
                        0,
                        mNumberOfLightSheets,
                        0,
                        mNumberOfLaserLines);
    }
    mQueueUpdateNeeded = false;
  }

  @Override
  public LightSheetMicroscopeQueue getQueue(int pCameraIndexMin,
                                            int pCameraIndexMax,
                                            int pLightSheetIndexMin,
                                            int pLightSheetIndexMax,
                                            int pLaserLineIndexMin,
                                            int pLaserLineIndexMax)
  {
    LightSheetMicroscopeQueue lQueue = getMicroscope().requestQueue();

    long lStackDepthInPlanes =
                             getStackNumberOfPlanesVariable().get()
                                                             .longValue();

    double lVoxelDepthInMicrons = getStackDepthInMicrons()
                                  / lStackDepthInPlanes;
    lQueue.addVoxelDimMetaData(getMicroscope(), lVoxelDepthInMicrons);

    lQueue.clearQueue();

    for (int lIndex = 0; lIndex < lStackDepthInPlanes; lIndex++)
    {
      applyAcquisitionStateAtStackPlane(lQueue,
                                        lIndex,
                                        pCameraIndexMin,
                                        pCameraIndexMax,
                                        pLightSheetIndexMin,
                                        pLightSheetIndexMax,
                                        pLaserLineIndexMin,
                                        pLaserLineIndexMax);
      lQueue.addCurrentStateToQueue();
    }

    for (int d = 0; d < getNumberOfDetectionArms(); d++)
      lQueue.setFlyBackDZ(d, get(LightSheetDOF.DZ, 0, d));
    lQueue.setFinalisationTime(0.3);
    lQueue.finalizeQueue();

    return lQueue;
  }

  @Override
  public LightSheetMicroscopeQueue getQueue()
  {
    if (mQueueUpdateNeeded)
      updateQueue(false);
    return mQueue;
  }

  /**
   * Applies acquisition state at a given z position
   * 
   * @param pQueue
   *          lightsheet microscope queue
   * @param pZ
   *          z position
   */
  public void applyAcquisitionStateAtZ(LightSheetMicroscopeQueue pQueue,
                                       double pZ)
  {
    int lPlaneIndexForZRamp = getPlaneIndexForZRamp(pZ);

    applyAcquisitionStateAtStackPlane(pQueue, lPlaneIndexForZRamp);
  }

  /**
   * Applies acquisition state at a given stack plane and lightsheet and camera
   * index ranges
   * 
   * @param pQueue
   *          lightsheet microscope
   * @param pPlaneIndex
   *          stack plane index
   * @param pCameraIndexMin
   *          lower camera index (inclusive)
   * @param pCameraIndexMax
   *          higher camera index (exclusive)
   * @param pLightSheetIndexMin
   *          lower lightsheet index (inclusive)
   * @param pLightSheetIndexMax
   *          higher lightsheet index (exclusive)
   * @param pLaserLineIndexMin
   *          lower laser line index (inclusive)
   * @param pLaserLineIndexMax
   *          higher laser line index (exclusive)
   */
  public void applyAcquisitionStateAtStackPlane(LightSheetMicroscopeQueue pQueue,
                                                int pPlaneIndex,
                                                int pCameraIndexMin,
                                                int pCameraIndexMax,
                                                int pLightSheetIndexMin,
                                                int pLightSheetIndexMax,
                                                int pLaserLineIndexMin,
                                                int pLaserLineIndexMax)
  {

    for (int d = pCameraIndexMin; d < pCameraIndexMax; d++)
    {
      applyAcquisitionStateAtStackPlaneAndForCamera(pQueue,
                                                    pPlaneIndex,
                                                    d);
    }

    for (int l = 0; l < mNumberOfLightSheets; l++)
      pQueue.setI(l, false);

    for (int l = pLightSheetIndexMin; l < pLightSheetIndexMax; l++)
    {
      applyAcquisitionStateAtStackPlaneAndLightSheet(pQueue,
                                                     pPlaneIndex,
                                                     l,
                                                     pLaserLineIndexMin,
                                                     pLaserLineIndexMax);
    }
  }

  /**
   * Applies acquisition state at a given stack plane
   * 
   * @param pQueue
   *          lightsheet microscope
   * @param pPlaneIndex
   *          stack plane index
   */
  public void applyAcquisitionStateAtStackPlane(LightSheetMicroscopeQueue pQueue,
                                                int pPlaneIndex)
  {

    for (int d = 0; d < mNumberOfDetectionArms; d++)
    {
      applyAcquisitionStateAtStackPlaneAndForCamera(pQueue,
                                                    pPlaneIndex,
                                                    d);
    }

    for (int l = 0; l < mNumberOfLightSheets; l++)
      pQueue.setI(l, false);

    for (int l = 0; l < mNumberOfLightSheets; l++)
    {
      applyAcquisitionStateAtStackPlaneAndLightSheet(pQueue,
                                                     pPlaneIndex,
                                                     l,
                                                     0,
                                                     mNumberOfLaserLines);
    }
  }

  private void applyAcquisitionStateAtStackPlaneAndForCamera(LightSheetMicroscopeQueue pQueue,
                                                             int pPlaneIndex,
                                                             int d)
  {
    pQueue.setCenteredROI(getImageWidthVariable().get().intValue(),
                          getImageHeightVariable().get().intValue());
    pQueue.setExp(getExposureInSecondsVariable().get().doubleValue());
    pQueue.setDZ(d, get(LightSheetDOF.DZ, pPlaneIndex, d));
    pQueue.setC(d, mCameraOnOff[d].get());
  }

  private void applyAcquisitionStateAtStackPlaneAndLightSheet(LightSheetMicroscopeQueue pQueue,
                                                              int pPlaneIndex,
                                                              int pLightSheetIndex,
                                                              int pLaserLineIndexMin,
                                                              int pLaserLineIndexMax)
  {

    pQueue.setI(pLightSheetIndex,
                mLightSheetOnOff[pLightSheetIndex].get());

    pQueue.setIX(pLightSheetIndex,
                 get(LightSheetDOF.IX,
                     pPlaneIndex,
                     pLightSheetIndex));
    pQueue.setIY(pLightSheetIndex,
                 get(LightSheetDOF.IY,
                     pPlaneIndex,
                     pLightSheetIndex));
    pQueue.setIZ(pLightSheetIndex,
                 get(LightSheetDOF.IZ,
                     pPlaneIndex,
                     pLightSheetIndex));

    pQueue.setIA(pLightSheetIndex,
                 get(LightSheetDOF.IA,
                     pPlaneIndex,
                     pLightSheetIndex));
    pQueue.setIB(pLightSheetIndex,
                 get(LightSheetDOF.IB,
                     pPlaneIndex,
                     pLightSheetIndex));
    pQueue.setIW(pLightSheetIndex,
                 get(LightSheetDOF.IW,
                     pPlaneIndex,
                     pLightSheetIndex));
    pQueue.setIH(pLightSheetIndex,
                 get(LightSheetDOF.IH,
                     pPlaneIndex,
                     pLightSheetIndex));

    pQueue.setIP(pLightSheetIndex,
                 get(LightSheetDOF.IP,
                     pPlaneIndex,
                     pLightSheetIndex));

    for (int la = pLaserLineIndexMin; la < pLaserLineIndexMax; la++)
    {
      pQueue.setILO(pLightSheetIndex,
                    la,
                    mLightSheetOnOff[pLightSheetIndex].get()
                        && mLaserOnOff[la].get());
    }
  }

  /**
   * Applies state from a given control plane
   * 
   * @param pQueue
   *          lightsheet microscope queue
   * @param pControlPlaneIndex
   *          control plane index
   */
  @Override
  public void applyStateAtControlPlane(LightSheetMicroscopeQueue pQueue,
                                       int pControlPlaneIndex)
  {
    double lControlPlaneZ = getControlPlaneZ(pControlPlaneIndex);
    int lStackPlaneIndex = getPlaneIndexForZRamp(lControlPlaneZ);
    applyAcquisitionStateAtStackPlane(pQueue, lStackPlaneIndex);
  }

  /**
   * Adds stack margins to the current queue.
   * 
   * @param pQueue
   *          lightsheet microscope queue
   * @param pNumberOfMarginPlanesToAdd
   *          number of margins to add
   */
  public void addStackMargin(LightSheetMicroscopeQueue pQueue,
                             int pNumberOfMarginPlanesToAdd)
  {
    addStackMargin(pQueue, 0, pNumberOfMarginPlanesToAdd);
  }

  /**
   * Adds stack margins to the current queue using a given plane index as
   * template
   * 
   * @param pQueue
   *          lightsheet microscope queue
   * @param pStackPlaneIndex
   *          stack plane index to use as template
   * @param pNumberOfMarginPlanesToAdd
   *          number of margin planes to add
   */
  public void addStackMargin(LightSheetMicroscopeQueue pQueue,
                             int pStackPlaneIndex,
                             int pNumberOfMarginPlanesToAdd)
  {
    applyAcquisitionStateAtStackPlane(pQueue, pStackPlaneIndex);
    pQueue.setC(false);
    pQueue.setILO(false);
    for (int i = 0; i < pNumberOfMarginPlanesToAdd; i++)
      mQueue.addCurrentStateToQueue();
  }

  /**
   * Adds a control plane for a given z value
   * 
   * @param z
   *          value
   */
  public void addControlPlane(double z)
  {
    mInterpolationTables.addControlPlane(z);
  }

  /**
   * Removes the nearest control plane for a given z value
   * 
   * @param z
   *          value
   */
  public void removeControlPlane(double z)
  {
    mInterpolationTables.removeControlPlane(z);
  }

  /**
   * Changes the nearest control plane for a given z value to a new value
   * 
   * @param pControlPlaneIndex
   *          control plane index
   * @param pNewZ
   *          new z value for control plane
   */
  public void changeControlPlane(int pControlPlaneIndex, double pNewZ)
  {
    mInterpolationTables.changeControlPlane(pControlPlaneIndex,
                                            pNewZ);
  }

  /**
   * Returns the z value for a given control plane index
   * 
   * @param pControlPlaneIndex
   *          control plane index
   * @return z value
   */
  public double getControlPlaneZ(int pControlPlaneIndex)
  {
    return mInterpolationTables.getZ(pControlPlaneIndex);
  }

  /**
   * Returns the Z ramp value for a given plane index.
   * 
   * @param pPlaneIndex
   *          plane index
   * @return Z ramp value
   */
  public double getZRamp(int pPlaneIndex)
  {
    final double lZ = mZLow.get().doubleValue()
                      + pPlaneIndex
                        * getStackZStepVariable().get().doubleValue();
    return lZ;
  }

  /**
   * Returns the plane index for a given Z ramp position.
   * 
   * @param pZRampValue
   *          Z ramp value
   * @return corresponding plane index
   */
  public int getPlaneIndexForZRamp(double pZRampValue)
  {
    double lZStep = getStackZStepVariable().get().doubleValue();
    double lAdjustedZRamp = pZRampValue - mZLow.get().doubleValue();
    final int lIndex = (int) round(lAdjustedZRamp / lZStep);
    return lIndex;
  }

  /**
   * Returns value of a given DOF for given plane and device indices
   * 
   * @param pDOF
   *          DOF
   * @param pPlaneIndex
   *          plane index
   * @param pDeviceIndex
   *          device index
   * @return value of DOF for given plane and device
   */
  public double get(LightSheetDOF pDOF,
                    int pPlaneIndex,
                    int pDeviceIndex)
  {
    final double lRamp = getZRamp(pPlaneIndex);
    final double lInterpolatedValue =
                                    mInterpolationTables.getInterpolated(pDOF,
                                                                         pDeviceIndex,
                                                                         lRamp);

    if (pDOF == LightSheetDOF.DZ)
      return lRamp + lInterpolatedValue;
    else if (pDOF == LightSheetDOF.IZ)
      return lRamp + lInterpolatedValue;
    else
      return lInterpolatedValue;
  }

  /**
   * Returns the number of detection arms
   * 
   * @return number of detection arms
   */
  public int getNumberOfDetectionArms()
  {
    return mNumberOfDetectionArms;
  }

  /**
   * returns the number of lightsheets
   * 
   * @return number of lightsheets
   */
  public int getNumberOfLightSheets()
  {
    return mNumberOfLightSheets;
  }

  /**
   * returns the number of lightsheets
   * 
   * @return number of lightsheets
   */
  public int getNumberOfLaserLines()
  {
    return mNumberOfLaserLines;
  }

  /**
   * Returns the interpolation tables
   * 
   * @return interpolation tables
   */
  @Override
  public InterpolationTables getInterpolationTables()
  {
    return mInterpolationTables;
  }

  /**
   * Returns stack low z variable
   * 
   * @return stack low z
   */
  public BoundedVariable<Number> getStackZLowVariable()
  {
    return mZLow;
  }

  /**
   * Returns stack high z variable
   * 
   * @return stack high z
   */
  public BoundedVariable<Number> getStackZHighVariable()
  {
    return mZHigh;
  }

  /**
   * Returns stack z step variable
   * 
   * @return z step variable
   */
  public BoundedVariable<Number> getStackZStepVariable()
  {
    return mZStep;
  }

  /**
   * Returns the variable holding the stack depth in number of image planes
   * 
   * @return stack depth in number of image planes variable
   */
  public Variable<Number> getStackNumberOfPlanesVariable()
  {
    return mZPlanes;
  }

  /**
   * Returns stack depth in microns
   * 
   * @return stack depth in microns
   */
  public double getStackDepthInMicrons()
  {
    return (mZHigh.get().doubleValue() - mZLow.get().doubleValue());
  }

  /**
   * Returns the number of control planes
   * 
   * @return number of control planes
   */
  public int getNumberOfControlPlanes()
  {
    return mInterpolationTables.getNumberOfControlPlanes();
  }

  @Override
  public Variable<Boolean> getCameraOnOffVariable(int pLightSheetIndex)
  {
    return mCameraOnOff[pLightSheetIndex];
  }

  @Override
  public Variable<Boolean> getLightSheetOnOffVariable(int pLightSheetIndex)
  {
    return mLightSheetOnOff[pLightSheetIndex];
  }

  @Override
  public Variable<Boolean> getLaserOnOffVariable(int pLightSheetIndex)
  {
    return mLaserOnOff[pLightSheetIndex];
  }

}
