package clearcontrol.microscope.lightsheet.acquisition;

import static java.lang.Math.floor;
import static java.lang.Math.round;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.NameableWithChangeListener;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Inetrpolated acquisition state
 *
 * @author royer
 */
public class InterpolatedAcquisitionState extends
                                          NameableWithChangeListener<AcquisitionStateInterface<LightSheetMicroscopeInterface>>
                                          implements
                                          AcquisitionStateInterface<LightSheetMicroscopeInterface>
{

  private int mNumberOfDetectionArms, mNumberOfLightSheets;

  private BoundedVariable<Number> mStageX =
                                          new BoundedVariable<Number>("StageX",
                                                                      25.0);

  private BoundedVariable<Number> mStageY =
                                          new BoundedVariable<Number>("StageY",
                                                                      25.0);

  private BoundedVariable<Number> mStageZ =
                                          new BoundedVariable<Number>("StageZ",
                                                                      25.0);

  private final BoundedVariable<Number> mXLow =
                                              new BoundedVariable<Number>("LowX",
                                                                          25.0);
  private final BoundedVariable<Number> mXHigh =
                                               new BoundedVariable<Number>("HighX",
                                                                           75.0);

  private final BoundedVariable<Number> mYLow =
                                              new BoundedVariable<Number>("LowY",
                                                                          25.0);
  private final BoundedVariable<Number> mYHigh =
                                               new BoundedVariable<Number>("HighY",
                                                                           75.0);

  private final BoundedVariable<Number> mZLow =
                                              new BoundedVariable<Number>("LowZ",
                                                                          25.0);
  private final BoundedVariable<Number> mZHigh =
                                               new BoundedVariable<Number>("HighZ",
                                                                           75.0);

  private final BoundedVariable<Number> mZStep =
                                               new BoundedVariable<Number>("ZStep",
                                                                           0.5,
                                                                           0,
                                                                           1000);

  private final InterpolationTables mInterpolationTables;

  private LightSheetMicroscopeQueue mQueue;

  /**
   * Instanciates an interpolated acquisition state
   * 
   * @param pName
   *          acquisition state name
   * @param pNumberOfDetectionArmDevices
   *          number of detection arms
   * @param pNumberOfLightSheetDevices
   *          number of lightsheets
   */
  public InterpolatedAcquisitionState(String pName,
                                      int pNumberOfDetectionArmDevices,
                                      int pNumberOfLightSheetDevices)
  {
    super(pName);
    mNumberOfDetectionArms = pNumberOfDetectionArmDevices;
    mNumberOfLightSheets = pNumberOfLightSheetDevices;

    mInterpolationTables =
                         new InterpolationTables(pNumberOfDetectionArmDevices,
                                                 pNumberOfLightSheetDevices);

    mInterpolationTables.addChangeListener((e) -> {
      notifyListeners(this);
    });

    VariableSetListener<Number> lVariableSetListener = (o, n) -> {
      notifyListeners(this);
    };
    getStackZLowVariable().addSetListener(lVariableSetListener);
    getStackZHighVariable().addSetListener(lVariableSetListener);
    getStackZStepVariable().addSetListener(lVariableSetListener);

    resetBounds();
  }

  /**
   * Instanciates an interpolated acquisition state for a given lightsheet
   * microscope
   * 
   * @param pName
   *          interpolated acquisition state name
   * @param pMicroscope
   *          lightsheet microscope
   */
  public InterpolatedAcquisitionState(String pName,
                                      LightSheetMicroscopeInterface pMicroscope)
  {
    this(pName, 2, 4);

    if (pMicroscope != null)
    {
      mNumberOfDetectionArms =
                             pMicroscope.getDeviceLists()
                                        .getNumberOfDevices(DetectionArmInterface.class);
      mNumberOfLightSheets =
                           pMicroscope.getDeviceLists()
                                      .getNumberOfDevices(LightSheetInterface.class);
    }
  }

  /**
   * Instanciates an interpolated acquisition state for agiven lightsheet
   * microscope
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
         pInterpolatedAcquisitionState.getNumberOfDetectionArms(),
         pInterpolatedAcquisitionState.getNumberOfLightSheets());
  }

  /**
   * resets the bounds
   */
  public void resetBounds()
  {
    // TODO: get bounds
  }

  /**
   * Setup defaults
   */
  public void setupDefault()
  {
    setup(-100, 0, 100, 1, 20, 10);
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
   * Returns stack depth in microns
   * 
   * @return stack depth in microns
   */
  public double getStackDepthInMicrons()
  {
    return (mZHigh.get().doubleValue() - mZLow.get().doubleValue());
  }

  /**
   * Sets stack depth
   * 
   * @param pNumberOfPlanes
   *          number of planes
   */
  public void setStackDepth(int pNumberOfPlanes)
  {
    double lStepZ = (getStackDepthInMicrons() / (pNumberOfPlanes));

    getStackZHighVariable().set(getStackZLowVariable().get()
                                                      .doubleValue()
                                + pNumberOfPlanes * lStepZ);
    getStackZStepVariable().set(lStepZ);
  }

  /**
   * Returns stack depth
   * 
   * @return stack depth
   */
  public int getStackDepth()
  {
    return (int) floor(getStackDepthInMicrons()
                       / mZStep.get().doubleValue());
  }

  @Override
  public void applyAcquisitionState(LightSheetMicroscopeInterface pLightSheetMicroscopeInterface)
  {
    if (mQueue == null)
      mQueue = pLightSheetMicroscopeInterface.requestQueue();

    mQueue.clearQueue();
    applyStagePosition(pLightSheetMicroscopeInterface);
    mQueue.clearQueue();
    for (int lIndex = 0; lIndex < getStackDepth(); lIndex++)
    {
      applyAcquisitionStateAtStackPlane(mQueue, lIndex);
      mQueue.addCurrentStateToQueue();
    }
    mQueue.finalizeQueue();
    // addStackMargin();
  }

  /**
   * Applies stage position
   * 
   * @param pLightSheetMicroscopeInterface
   *          lightsheet microscope
   */
  public void applyStagePosition(LightSheetMicroscopeInterface pLightSheetMicroscopeInterface)
  {
    double lStageX = getStageXVariable().get().doubleValue();
    double lStageY = getStageYVariable().get().doubleValue();
    double lStageZ = getStageZVariable().get().doubleValue();

    pLightSheetMicroscopeInterface.setStageX(lStageX);
    pLightSheetMicroscopeInterface.setStageY(lStageY);
    pLightSheetMicroscopeInterface.setStageZ(lStageZ);
    pLightSheetMicroscopeInterface.getMainXYZRStage()
                                  .waitToBeReady(10,
                                                 TimeUnit.SECONDS);
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
    final int lNumberOfDetectionPathDevices =
                                            pQueue.getMicroscope()
                                                  .getNumberOfDevices(DetectionArmInterface.class);

    for (int d = 0; d < lNumberOfDetectionPathDevices; d++)
    {
      pQueue.setDZ(d, get(LightSheetDOF.DZ, pPlaneIndex, d));
    }

    final int lNumberOfLightsheetDevices =
                                         pQueue.getMicroscope()
                                               .getNumberOfDevices(LightSheetInterface.class);

    for (int l = 0; l < lNumberOfLightsheetDevices; l++)
    {

      pQueue.setIX(l, get(LightSheetDOF.IX, pPlaneIndex, l));
      pQueue.setIY(l, get(LightSheetDOF.IY, pPlaneIndex, l));
      pQueue.setIZ(l, get(LightSheetDOF.IZ, pPlaneIndex, l));

      pQueue.setIA(l, get(LightSheetDOF.IA, pPlaneIndex, l));
      pQueue.setIB(l, get(LightSheetDOF.IB, pPlaneIndex, l));
      pQueue.setIW(l, get(LightSheetDOF.IW, pPlaneIndex, l));
      pQueue.setIH(l, get(LightSheetDOF.IH, pPlaneIndex, l));
      pQueue.setIP(l, get(LightSheetDOF.IP, pPlaneIndex, l));
    }

    final int lNumberOfLaserDevices =
                                    pQueue.getMicroscope()
                                          .getNumberOfDevices(LaserDeviceInterface.class);

    for (int i = 0; i < lNumberOfLaserDevices; i++)
    {
      pQueue.setIP(i, get(LightSheetDOF.IP, pPlaneIndex, i));
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
   * Returns stack low z variable
   * 
   * @return stack low z
   */
  public BoundedVariable<Number> getStackXLowVariable()
  {
    return mXLow;
  }

  /**
   * Returns stack high x variable
   * 
   * @return stack high x
   */
  public BoundedVariable<Number> getStackXHighVariable()
  {
    return mXHigh;
  }

  /**
   * Returns stack low y variable
   * 
   * @return stack low y
   */
  public BoundedVariable<Number> getStackYLowVariable()
  {
    return mYLow;
  }

  /**
   * Returns stack high y variable
   * 
   * @return stack high y
   */
  public BoundedVariable<Number> getStackYHighVariable()
  {
    return mYHigh;
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
   * Returns the number of control planes
   * 
   * @return number of control planes
   */
  public int getNumberOfControlPlanes()
  {
    return mInterpolationTables.getNumberOfControlPlanes();
  }

  /**
   * Returns state variable x
   * 
   * @return stage variable x
   */
  public BoundedVariable<Number> getStageXVariable()
  {
    return mStageX;
  }

  /**
   * Returns state variable y
   * 
   * @return stage variable y
   */
  public BoundedVariable<Number> getStageYVariable()
  {
    return mStageY;
  }

  /**
   * Returns state variable z
   * 
   * @return stage variable z
   */
  public BoundedVariable<Number> getStageZVariable()
  {
    return mStageZ;
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
   * Returns the interpolation tables
   * 
   * @return interpolation tables
   */
  public InterpolationTables getInterpolationTables()
  {
    return mInterpolationTables;
  }

}
