package clearcontrol.microscope.lightsheet.component.detection;

import clearcontrol.core.device.queue.RealTimeQueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Light sheet microscope detection arm
 *
 * @author royer
 */
public class DetectionArmQueue extends VariableQueueBase implements
                               RealTimeQueueInterface,
                               LoggingInterface
{
  private DetectionArm mDetectionArm;

  private final BoundedVariable<Number> mDetectionFocusZ =
                                                         new BoundedVariable<Number>("FocusZ",
                                                                                     0.0);

  /**
   * Instanciates detection arm queue
   * 
   * @param pDetectionArm
   *          parent detection arm
   */
  public DetectionArmQueue(DetectionArm pDetectionArm)
  {
    super();
    mDetectionArm = pDetectionArm;
    registerVariable(mDetectionFocusZ);
  }

  /**
   * Copy constuctor
   * 
   * @param pTemplateQueue
   *          template queue to copy
   */
  public DetectionArmQueue(DetectionArmQueue pTemplateQueue)
  {
    this(pTemplateQueue.getDetectionArm());
    mDetectionFocusZ.set(pTemplateQueue.getZVariable());
  }

  /**
   * Returns parent detection arm
   * 
   * @return parent detection arm
   */
  public DetectionArm getDetectionArm()
  {
    return mDetectionArm;
  }

  /**
   * Returns the detection plane Z position variable
   * 
   * @return Z variable
   */
  public BoundedVariable<Number> getZVariable()
  {
    return mDetectionFocusZ;
  }

}
