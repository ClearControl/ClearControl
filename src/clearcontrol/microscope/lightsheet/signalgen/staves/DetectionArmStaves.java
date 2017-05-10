package clearcontrol.microscope.lightsheet.signalgen.staves;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.signalgen.movement.Movement;
import clearcontrol.devices.signalgen.staves.BezierStave;
import clearcontrol.devices.signalgen.staves.ConstantStave;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmQueue;

/**
 * Detection arm staves
 *
 * @author royer
 */
public class DetectionArmStaves
{
  private final DetectionArmQueue mDetectionArmQueue;

  private final ConstantStave mDetectionZStave;
  private BezierStave mFinalDetectionZStave;

  private final int mStaveIndex;

  /**
   * Instantiates an object holding detection arm staves
   * 
   * @param pDetectionArmQueue
   *          detection arm queue
   */
  public DetectionArmStaves(DetectionArmQueue pDetectionArmQueue)
  {
    super();
    mDetectionArmQueue = pDetectionArmQueue;

    mDetectionZStave = new ConstantStave("detection.z", 0);

    mFinalDetectionZStave = new BezierStave("detection.z",0);

    mStaveIndex =
                MachineConfiguration.getCurrentMachineConfiguration()
                                    .getIntegerProperty("device.lsm.detection."
                                                        + pDetectionArmQueue.getDetectionArm()
                                                                            .getName()
                                                        + ".z.index",
                                                        0);

  }

  /**
   * Returns detection arm
   * 
   * @return detection arm
   */
  public DetectionArmQueue getDetectionArmQueue()
  {
    return mDetectionArmQueue;
  }

  /**
   * Adds staves to movements
   * 
   * @param pBeforeExposureMovement
   *          before exp movement
   * @param pExposureMovement
   *          exposure movement
   * @param pFinalMovement
   *          final movement
   */
  public void addStavesToMovements(Movement pBeforeExposureMovement,
                                   Movement pExposureMovement,
                                   Movement pFinalMovement)
  {
    // Analog outputs before exposure:
    pBeforeExposureMovement.setStave(mStaveIndex, mDetectionZStave);

    // Analog outputs at exposure:
    pExposureMovement.setStave(mStaveIndex, mDetectionZStave);

    // Analog outputs at final movement:
    pFinalMovement.setStave(mStaveIndex, mFinalDetectionZStave);
  }

  /**
   * Updates the staves based on the information from detection arm queue
   * 
   * @param pBeforeExposureMovement
   *          before exposure movement
   * @param pExposureMovement
   *          exposure movement
   * @param pFinalMovement final movement
   */
  public void update(Movement pBeforeExposureMovement,
                     Movement pExposureMovement,
                     Movement pFinalMovement)
  {

    BoundedVariable<Number> lZVariable =
                                       mDetectionArmQueue.getZVariable();
    BoundedVariable<Number> lFlyBackZVariable =
        mDetectionArmQueue.getFlyBackZVariable();
    
    Variable<UnivariateAffineFunction> lZFunction =
                                                  mDetectionArmQueue.getDetectionArm()
                                                                    .getZFunction();

    double lZFocus = lZVariable.get().doubleValue();
    float lZFocusTransformed =
                             (float) lZFunction.get().value(lZFocus);
    mDetectionZStave.setValue(lZFocusTransformed);
    
    double lFlyBackZFocus = lFlyBackZVariable.get().doubleValue();
    float lFlyBackZFocusTransformed =
                             (float) lZFunction.get().value(lFlyBackZFocus);
    
    mFinalDetectionZStave.setStartValue(lZFocusTransformed);
    mFinalDetectionZStave.setStopValue(lFlyBackZFocusTransformed);
    mFinalDetectionZStave.setStartSlope(0);
    mFinalDetectionZStave.setStopSlope(0);
    mFinalDetectionZStave.setSmoothness(0.3f);
    
  }

  /**
   * Returns detection stave
   * 
   * @return detection stave
   */
  public ConstantStave getDetectionZStave()
  {
    return mDetectionZStave;
  }

}
