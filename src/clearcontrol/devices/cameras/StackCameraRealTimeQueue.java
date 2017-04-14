package clearcontrol.devices.cameras;

import clearcontrol.core.device.queue.RealTimeQueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.metadata.StackMetaData;

/**
 * Real time queue for stack camera devices
 *
 * @author royer
 */
public class StackCameraRealTimeQueue extends VariableQueueBase
                                      implements
                                      RealTimeQueueInterface
{
  private StackCameraDeviceInterface mStackCamera;

  private final Variable<Number> mExposureInSecondsVariable;
  private final Variable<StackMetaData> mMetaData;

  protected final Variable<Boolean> mKeepPlane;
  protected Variable<Long> mStackWidthVariable, mStackHeightVariable,
      mStackDepthVariable;

  /**
   * Instanciates a real-time stack camera queue
   * 
   */
  public StackCameraRealTimeQueue()
  {
    super();

    mKeepPlane = new Variable<Boolean>("KeepPlane", true);

    mExposureInSecondsVariable =
                               new Variable<Number>("ExposureInSeconds",
                                                    0);

    mStackWidthVariable = new Variable<Long>("FrameWidth", 320L);

    mStackHeightVariable = new Variable<Long>("FrameHeight", 320L);

    mStackDepthVariable = new Variable<Long>("FrameDepth", 100L);

    mMetaData = new Variable<StackMetaData>("MetaData",
                                            new StackMetaData());

    registerVariable(mKeepPlane);
  }

  /**
   * Returns parent stack camera
   * 
   * @param pStackCameraDevice
   *          parent stack camera
   * 
   */
  public void setStackCamera(StackCameraDeviceInterface pStackCameraDevice)
  {
    mStackCamera = pStackCameraDevice;
  }

  /**
   * Returns parent stack camera
   * 
   * @return parent stack camera
   */
  public StackCameraDeviceInterface getStackCamera()
  {
    return mStackCamera;
  }

  /**
   * Instanciates a stack camera queue
   * 
   * @param pStackCameraRealTimeQueue
   *          stack camera queue
   */
  public StackCameraRealTimeQueue(StackCameraRealTimeQueue pStackCameraRealTimeQueue)
  {
    this();

    setStackCamera(pStackCameraRealTimeQueue.getStackCamera());

    getKeepPlaneVariable().set(pStackCameraRealTimeQueue.getKeepPlaneVariable()
                                                        .get());

    getExposureInSecondsVariable().set(pStackCameraRealTimeQueue.getExposureInSecondsVariable()
                                                                .get());

    getStackWidthVariable().set(pStackCameraRealTimeQueue.getStackWidthVariable()
                                                         .get());

    getStackHeightVariable().set(pStackCameraRealTimeQueue.getStackHeightVariable()
                                                          .get());

    getStackDepthVariable().set(pStackCameraRealTimeQueue.getStackDepthVariable()
                                                         .get());

    getMetaDataVariable().set(pStackCameraRealTimeQueue.getMetaDataVariable()
                                                       .get()
                                                       .clone());

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

  /**
   * Returns the meta data variable
   * 
   * @return meta data variable
   */
  public Variable<StackMetaData> getMetaDataVariable()
  {
    return mMetaData;
  }

  /**
   * Returns the exposure variable (in seconds)
   * 
   * @return exposure variable
   */
  public Variable<Number> getExposureInSecondsVariable()
  {
    return mExposureInSecondsVariable;
  }

  /**
   * Returns the stack width
   * 
   * @return stack width
   */
  public Variable<Long> getStackWidthVariable()
  {
    return mStackWidthVariable;
  }

  /**
   * Returns the stack height
   * 
   * @return stack height
   */
  public Variable<Long> getStackHeightVariable()
  {
    return mStackHeightVariable;
  }

  /**
   * Returns the stack depth
   * 
   * @return stack depth
   */
  public Variable<Long> getStackDepthVariable()
  {
    return mStackDepthVariable;
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
