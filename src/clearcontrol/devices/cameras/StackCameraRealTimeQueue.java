package clearcontrol.devices.cameras;

import clearcontrol.core.device.queue.RealTimeQueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.variable.Variable;

/**
 * Real time queue for stack camera devices
 *
 * @author royer
 */
public class StackCameraRealTimeQueue extends VariableQueueBase
                                      implements
                                      RealTimeQueueInterface
{
  protected Variable<Boolean> mKeepPlane;

  /**
   * Instanciates a real-time stack camera queue
   */
  public StackCameraRealTimeQueue()
  {
    super();
    mKeepPlane = new Variable<Boolean>("KeepPlane", true);
    registerVariable(mKeepPlane);
  }

  /**
   * Returns the variable holding the flag that indicates whether to keep this
   * image. This is for sttqe queing purposes, and allows to discard images
   * within an acquired stack. This can be used for discarding images at the
   * beginning or end of a stack.
   * 
   * @return keep plane flag variable
   */
  public Variable<Boolean> getKeepPlaneVariable()
  {
    return mKeepPlane;
  }

  @Override
  public void clearQueue()
  {
    super.clearQueue();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    super.addCurrentStateToQueue();
  }

  @Override
  public void finalizeQueue()
  {
    super.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return super.getQueueLength();
  }

}
