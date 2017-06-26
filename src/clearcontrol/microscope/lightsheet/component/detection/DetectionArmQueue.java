package clearcontrol.microscope.lightsheet.component.detection;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Light sheet microscope detection arm
 *
 * @author royer
 */
public class DetectionArmQueue extends VariableQueueBase implements
                               QueueInterface,
                               LoggingInterface
{
  private DetectionArm mDetectionArm;

  private final BoundedVariable<Number> mDetectionFocusZ;

  /**
   * Instantiates detection arm queue
   * 
   * @param pDetectionArm
   *          parent detection arm
   */
  public DetectionArmQueue(DetectionArm pDetectionArm)
  {
    super();
    mDetectionArm = pDetectionArm;

    mDetectionFocusZ =
                     new BoundedVariable<Number>(mDetectionArm.getName()
                                                 + "-DetectionFocusZ",
                                                 0.0);

    registerVariable(mDetectionFocusZ);
  }

  /**
   * Instantiates a new queue by copying the given queue current state.
   * 
   * @param pTemplateQueue
   *          template queue to copy (without history)
   */
  public DetectionArmQueue(DetectionArmQueue pTemplateQueue)
  {
    this(pTemplateQueue.getDetectionArm());
    getZVariable().set(pTemplateQueue.getZVariable());
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
