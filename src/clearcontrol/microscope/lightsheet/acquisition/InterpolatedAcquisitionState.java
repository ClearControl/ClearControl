package clearcontrol.microscope.lightsheet.acquisition;

import static java.lang.Math.floor;
import static java.lang.Math.round;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.NameableWithChangeListener;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Inetrpolated acquisition state
 *
 * @author royer
 */
public class InterpolatedAcquisitionState extends
                                          NameableWithChangeListener<AcquisitionStateInterface<LightSheetMicroscopeInterface, LightSheetMicroscopeQueue>>
                                          implements
                                          LightSheetAcquisitionStateInterface<InterpolatedAcquisitionState>,
                                          Cloneable

{

  private int mNumberOfDetectionArms, mNumberOfLightSheets,
      mNumberOfLaserLines;

  private BoundedVariable<Number> mStageX =
                                          new BoundedVariable<Number>("StageX",
                                                                      0.0);

  private BoundedVariable<Number> mStageY =
                                          new BoundedVariable<Number>("StageY",
                                                                      0.0);

  private BoundedVariable<Number> mStageZ =
                                          new BoundedVariable<Number>("StageZ",
                                                                      0.0);

  private BoundedVariable<Number> mZLow;
  private BoundedVariable<Number> mZHigh;

  private final BoundedVariable<Number> mZStep =
                                               new BoundedVariable<Number>("ZStep",
                                                                           0.5,
                                                                           0,
                                                                           1000);

  private final Variable<Number> mZPlanes =
                                          new Variable<Number>("ZPlanes",
                                                               1);

  private final Variable<Boolean>[] mLightSheetOnOff;

  private final InterpolationTables mInterpolationTables;

  private LightSheetMicroscopeQueue mQueue;

  private LightSheetMicroscopeInterface mLightSheetMicroscope;

  /**
   * Instanciates an interpolated acquisition state
   * 
   * @param pName
   *          acquisition state name
   * @param pNumberOfDetectionArms
   *          number of detectin arms
   * @param pNumberOfLightSheets
   *          number of lighsheets
   * @param pNumberOfLaserLines
   *          number of laser lines
   */
  @SuppressWarnings("unchecked")
  public InterpolatedAcquisitionState(String pName,
                                      int pNumberOfDetectionArms,
                                      int pNumberOfLightSheets,
                                      int pNumberOfLaserLines)
  {
    super(pName);
    mLightSheetMicroscope = null;
    mNumberOfDetectionArms = pNumberOfDetectionArms;
    mNumberOfLightSheets = pNumberOfLightSheets;
    mNumberOfLaserLines = pNumberOfLaserLines;

    mInterpolationTables =
                         new InterpolationTables(mNumberOfDetectionArms,
                                                 mNumberOfLightSheets);

    mLightSheetOnOff = new Variable[mNumberOfLightSheets];

    for (int i = 0; i < mLightSheetOnOff.length; i++)
      mLightSheetOnOff[i] = new Variable<Boolean>(
                                                  String.format("LightSheet%dOnOff",
                                                                i),
                                                  false);

    mZLow = new BoundedVariable<Number>("LowZ", -100.0);
    mZHigh = new BoundedVariable<Number>("HighZ", 100.0);

    mZStep.addSetListener((o, n) -> {
      if (n != null && !n.equals(o))
      {
        long lZPlanes = (long) floor(getStackDepthInMicrons()
                                     / n.doubleValue());

        if (mZPlanes.get().longValue() != lZPlanes)
        {
          mZPlanes.set(lZPlanes);

          getStackZHighVariable().set(getStackZLowVariable().get()
                                                            .doubleValue()
                                      + lZPlanes * n.doubleValue());
        }
      }
    });

    mZPlanes.addSetListener((o, n) -> {
      if (n != null && !n.equals(o))
      {
        double lStepZ =
                      (getStackDepthInMicrons() / (n.doubleValue()));
        if (mZStep.get().doubleValue() != lStepZ)
          mZStep.set(lStepZ);
      }
    });

  }

  /**
   * Instanciates an interpolated acquisition state
   * 
   * @param pName
   *          acquisition state name
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
  public InterpolatedAcquisitionState(String pName,
                                      LightSheetMicroscopeInterface pLightSheetMicroscope)
  {
    this(pName,
         pLightSheetMicroscope.getNumberOfDetectionArms(),
         pLightSheetMicroscope.getNumberOfLightSheets(),
         pLightSheetMicroscope.getNumberOfLaserLines());
    mLightSheetMicroscope = pLightSheetMicroscope;

    DetectionArmInterface lDetectionArm =
                                        mLightSheetMicroscope.getDetectionArm(0);

    mZLow = new BoundedVariable<Number>("LowZ",
                                        25.0,
                                        lDetectionArm.getZVariable()
                                                     .getMin(),
                                        lDetectionArm.getZVariable()
                                                     .getMax());
    mZHigh = new BoundedVariable<Number>("HighZ",
                                         75.0,
                                         lDetectionArm.getZVariable()
                                                      .getMin(),
                                         lDetectionArm.getZVariable()
                                                      .getMax());

    mInterpolationTables.addChangeListener((e) -> {
      notifyListeners(this);
    });

    VariableSetListener<Number> lVariableSetListener = (o, n) -> {
      notifyListeners(this);
    };
    getStackZLowVariable().addSetListener(lVariableSetListener);
    getStackZHighVariable().addSetListener(lVariableSetListener);
    getStackZStepVariable().addSetListener(lVariableSetListener);

    StageDeviceInterface lMainXYZRStage =
                                        getLightSheetMicroscope().getMainXYZRStage();

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

    resetBounds();
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
    this(pName,
         pInterpolatedAcquisitionState.getLightSheetMicroscope());
  }

  @Override
  public InterpolatedAcquisitionState copy(String pName)
  {
    return new InterpolatedAcquisitionState(pName, this);
  }

  /**
   * resets the bounds
   */
  public void resetBounds()
  {
    // TODO: get bounds
  }

  /**
   * Setup defaults given a lightsheet microscope
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
  public void setupDefault(LightSheetMicroscopeInterface pLightSheetMicroscope)
  {
    setup(-120, 0, 120, 4, 20, 10);

    int lNumberOfControlPlanes =
                               getInterpolationTables().getNumberOfControlPlanes();

    Number lMaxHeight = new Double(1);

    if (pLightSheetMicroscope != null)
    {
      lMaxHeight = pLightSheetMicroscope.getLightSheet(0)
                                        .getHeightVariable()
                                        .getMax();
    }

    for (int zpi = 0; zpi < lNumberOfControlPlanes; zpi++)
    {
      double z = getInterpolationTables().getZ(zpi);
      // System.out.format("z=%g \n", z);
      getInterpolationTables().set(LightSheetDOF.IZ, zpi, z);
      getInterpolationTables().set(LightSheetDOF.IH,
                                   zpi,
                                   lMaxHeight.doubleValue());
      getInterpolationTables().set(LightSheetDOF.IP, zpi, 1);
    }

  }

  /**
   * Basic setup
   * 
   * @param pLowZ
   *          low z
   * @param pMiddleZ
   *          middle z
   * @param pHighZ
   *          high z
   * @param pStepZ
   *          z step
   * @param pControlPlaneStepZ
   *          control plane z step
   * @param pMarginZ
   *          margin z
   */
  public void setup(double pLowZ,
                    double pMiddleZ,
                    double pHighZ,
                    double pStepZ,
                    double pControlPlaneStepZ,
                    double pMarginZ)
  {
    getStackZLowVariable().set(pLowZ);
    getStackZHighVariable().set(pHighZ);
    getStackZStepVariable().set(pStepZ);
    mInterpolationTables.setTransitionPlaneZPosition(pMiddleZ);

    for (double z = pLowZ
                    + pMarginZ; z <= pHighZ
                                     - pMarginZ; z +=
                                                   pControlPlaneStepZ)
    {
      mInterpolationTables.addControlPlane(z);
    }
    notifyListeners(this);
  }

  /**
   * Returns lightsheet microscope parent
   * 
   * @return light sheet microscope
   */
  public LightSheetMicroscopeInterface getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }

  @Override
  public void updateQueue(LightSheetMicroscopeInterface pLightSheetMicroscope)
  {
    mQueue = pLightSheetMicroscope.requestQueue();

    long lStackDepthInPlanes =
                             getStackDepthInPlanesVariable().get()
                                                            .longValue();

    double lVoxelDepthInMicrons = getStackDepthInMicrons()
                                  / lStackDepthInPlanes;
    mQueue.addVoxelDimMetaData(pLightSheetMicroscope,
                               lVoxelDepthInMicrons);

    applyStagePosition();
    mQueue.clearQueue();
    for (int lIndex = 0; lIndex < lStackDepthInPlanes; lIndex++)
    {
      applyAcquisitionStateAtStackPlane(mQueue, lIndex);
      mQueue.addCurrentStateToQueue();
    }
    mQueue.finalizeQueue();
  }

  @Override
  public LightSheetMicroscopeQueue getQueue()
  {
    return mQueue;
  }

  @Override
  public void applyStagePosition()
  {
    getLightSheetMicroscope().getMainXYZRStage().enable();

    double lStageX = getStageXVariable().get().doubleValue();
    double lStageY = getStageYVariable().get().doubleValue();
    double lStageZ = getStageZVariable().get().doubleValue();

    getLightSheetMicroscope().setStageX(lStageX);
    getLightSheetMicroscope().setStageY(lStageY);
    getLightSheetMicroscope().setStageZ(lStageZ);
    getLightSheetMicroscope().getMainXYZRStage()
                             .waitToBeReady(10, TimeUnit.SECONDS);
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
      pQueue.setDZ(d, get(LightSheetDOF.DZ, pPlaneIndex, d));
    }

    for (int l = 0; l < mNumberOfLightSheets; l++)
    {
      pQueue.setI(l, mLightSheetOnOff[l].get());

      pQueue.setIX(l, get(LightSheetDOF.IX, pPlaneIndex, l));
      pQueue.setIY(l, get(LightSheetDOF.IY, pPlaneIndex, l));
      pQueue.setIZ(l, get(LightSheetDOF.IZ, pPlaneIndex, l));

      pQueue.setIA(l, get(LightSheetDOF.IA, pPlaneIndex, l));
      pQueue.setIB(l, get(LightSheetDOF.IB, pPlaneIndex, l));
      pQueue.setIW(l, get(LightSheetDOF.IW, pPlaneIndex, l));
      pQueue.setIH(l, get(LightSheetDOF.IH, pPlaneIndex, l));

      pQueue.setIP(l, get(LightSheetDOF.IP, pPlaneIndex, l));
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
    else
      return lInterpolatedValue;
  }

  /**
   * Returns best detction arm for given plane index
   * 
   * @param pPlaneIndex
   *          plane index
   * @return best detection arm index, -1 if something is wrong.
   */
  @Override
  public int getBestDetectionArm(int pPlaneIndex)
  {

    double lTransitionPlane =
                            mInterpolationTables.getTransitionPlaneZPosition();

    if (getZRamp(pPlaneIndex) <= lTransitionPlane)
      return 0;
    else if (getZRamp(pPlaneIndex) >= lTransitionPlane)
      return 1;

    return -1;

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
  public Variable<Number> getStackDepthInPlanesVariable()
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
  public BoundedVariable<Number> getStageXVariable()
  {
    return mStageX;
  }

  @Override
  public BoundedVariable<Number> getStageYVariable()
  {
    return mStageY;
  }

  @Override
  public BoundedVariable<Number> getStageZVariable()
  {
    return mStageZ;
  }

  @Override
  public Variable<Boolean> getLightSheetOnOffVariable(int pLightSheetIndex)
  {
    return mLightSheetOnOff[pLightSheetIndex];
  }

}
