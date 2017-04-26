package clearcontrol.devices.cameras;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.metadata.StackMetaData;

/**
 * Queue for stack camera devices
 *
 * @author royer
 * @param <Q>
 *          reflexive queue type
 */
public abstract class StackCameraQueue<Q extends StackCameraQueue<Q>>
                                      extends VariableQueueBase
                                      implements QueueInterface
{
  private StackCameraDeviceInterface<Q> mStackCamera;

  private final Variable<Number> mExposureInSecondsVariable;
  private final Variable<StackMetaData> mMetaData;

  protected final Variable<Boolean> mKeepPlane;
  protected Variable<Long> mStackWidthVariable, mStackHeightVariable,
      mStackDepthVariable;

  /**
   * Instantiates a real-time stack camera queue
   * 
   */
  public StackCameraQueue()
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
  public void setStackCamera(StackCameraDeviceInterface<Q> pStackCameraDevice)
  {
    mStackCamera = pStackCameraDevice;
  }

  /**
   * Returns parent stack camera
   * 
   * @return parent stack camera
   */
  public StackCameraDeviceInterface<Q> getStackCamera()
  {
    return mStackCamera;
  }

  /**
   * Instantiates a stack camera queue
   * 
   * @param pStackCameraQueue
   *          stack camera queue
   */
  public StackCameraQueue(StackCameraQueue<Q> pStackCameraQueue)
  {
    this();

    setStackCamera(pStackCameraQueue.getStackCamera());

    getKeepPlaneVariable().set(pStackCameraQueue.getKeepPlaneVariable()
                                                .get());

    getExposureInSecondsVariable().set(pStackCameraQueue.getExposureInSecondsVariable()
                                                        .get());

    getStackWidthVariable().set(pStackCameraQueue.getStackWidthVariable()
                                                 .get());

    getStackHeightVariable().set(pStackCameraQueue.getStackHeightVariable()
                                                  .get());

    getStackDepthVariable().set(pStackCameraQueue.getStackDepthVariable()
                                                 .get());

    getMetaDataVariable().set(pStackCameraQueue.getMetaDataVariable()
                                               .get()
                                               .clone());

  }

  /**
   * Returns the variable holding the flag that indicates whether to keep this
   * image. This is for state queueing purposes, and allows to discard images
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
    getStackDepthVariable().set(0L);
    super.clearQueue();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    getStackDepthVariable().increment();
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
