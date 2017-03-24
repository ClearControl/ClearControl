package clearcontrol.devices.cameras;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.device.queue.StateQueueDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.queue.VariableStateQueues;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Base class providing common fields and methods for all stack camera devices.
 *
 * @author royer
 */
public abstract class StackCameraDeviceBase extends CameraDeviceBase
                                            implements
                                            StackCameraDeviceInterface,
                                            StateQueueDeviceInterface
{
  protected Variable<Boolean> mStackMode, mKeepPlane;

  protected Variable<Long> mNumberOfImagesPerPlaneVariable,
      mStackBytesPerPixelVariable, mStackWidthVariable,
      mStackHeightVariable, mStackMaxWidthVariable,
      mStackMaxHeightVariable, mStackDepthVariable;

  protected final AtomicLong mCurrentStackIndex = new AtomicLong(0);

  protected RecyclerInterface<StackInterface, StackRequest> mRecycler;

  protected Variable<StackInterface> mStackReference;

  private int mMinimalNumberOfAvailableStacks = 6;

  /**
   * Instanciates a stack cemra device with a given name
   * 
   * @param pDeviceName
   *          device name
   */
  public StackCameraDeviceBase(String pDeviceName)
  {
    super(pDeviceName);
    mVariableStateQueues = new VariableStateQueues();

    mStackMode = new Variable<Boolean>("StackMode", true);
    mVariableStateQueues.registerConstantVariable(mStackMode);

    mKeepPlane = new Variable<Boolean>("KeepPlane", true);
    mVariableStateQueues.registerVariable(mKeepPlane);

    mNumberOfImagesPerPlaneVariable =
                                    new Variable<Long>("NumberOfImagesPerPlane",
                                                       1L);
    mVariableStateQueues.registerConstantVariable(mNumberOfImagesPerPlaneVariable);

    mChannelVariable = new Variable<Integer>("Channel", 0);
    mVariableStateQueues.registerConstantVariable(mChannelVariable);

    mLineReadOutTimeInMicrosecondsVariable =
                                           new Variable<Double>("LineReadOutTimeInMicroseconds",
                                                                1.0);
    mVariableStateQueues.registerConstantVariable(mLineReadOutTimeInMicrosecondsVariable);

    mStackBytesPerPixelVariable =
                                new Variable<Long>("FrameBytesPerPixel",
                                                   2L);
    mVariableStateQueues.registerConstantVariable(mStackBytesPerPixelVariable);

    mStackWidthVariable = new Variable<Long>("FrameWidth", 320L);
    mVariableStateQueues.registerConstantVariable(mStackWidthVariable);

    mStackHeightVariable = new Variable<Long>("FrameHeight", 320L);
    mVariableStateQueues.registerConstantVariable(mStackHeightVariable);

    mStackMaxWidthVariable =
                           new Variable<Long>("FrameMaxWidth", 2048L);
    mVariableStateQueues.registerConstantVariable(mStackMaxWidthVariable);

    mStackMaxHeightVariable = new Variable<Long>("FrameMaxHeight",
                                                 2048L);
    mVariableStateQueues.registerConstantVariable(mStackMaxHeightVariable);

    mStackDepthVariable = new Variable<Long>("FrameDepth", 100L);
    mVariableStateQueues.registerConstantVariable(mStackDepthVariable);

    mExposureInMicrosecondsVariable =
                                    new Variable<Double>("ExposureInMicroseconds",
                                                         1000.0);
    mVariableStateQueues.registerConstantVariable(mExposureInMicrosecondsVariable);

    mPixelSizeinNanometersVariable =
                                   new Variable<Double>("PixelSizeinNanometers",
                                                        160.0);
    mVariableStateQueues.registerConstantVariable(mPixelSizeinNanometersVariable);

    mStackReference = new Variable<>("StackReference");
  }

  /**
   * @return
   */
  @Override
  public long getCurrentStackIndex()
  {
    return mCurrentStackIndex.get();
  }

  @Override
  public int getMinimalNumberOfAvailableStacks()
  {
    return mMinimalNumberOfAvailableStacks;
  }

  @Override
  public void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks)
  {
    mMinimalNumberOfAvailableStacks = pMinimalNumberOfAvailableStacks;
  }

  @Override
  public Variable<Long> getNumberOfImagesPerPlaneVariable()
  {
    return mNumberOfImagesPerPlaneVariable;
  }

  @Override
  public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler)
  {
    mRecycler = pRecycler;
  }

  @Override
  public RecyclerInterface<StackInterface, StackRequest> getStackRecycler()
  {
    return mRecycler;
  }

  @Override
  public Variable<Long> getStackBytesPerPixelVariable()
  {
    return mStackBytesPerPixelVariable;
  }

  @Override
  public Variable<Long> getStackWidthVariable()
  {
    return mStackWidthVariable;
  }

  @Override
  public Variable<Long> getStackHeightVariable()
  {
    return mStackHeightVariable;
  }

  @Override
  public Variable<Long> getStackDepthVariable()
  {
    return mStackDepthVariable;
  }

  @Override
  public Variable<Long> getStackMaxWidthVariable()
  {
    return mStackMaxWidthVariable;
  }

  @Override
  public Variable<Long> getStackMaxHeightVariable()
  {
    return mStackMaxHeightVariable;
  }

  @Override
  public Variable<Boolean> getStackModeVariable()
  {
    return mStackMode;
  }

  @Override
  public Variable<Boolean> getKeepPlaneVariable()
  {
    return mKeepPlane;
  }

  @Override
  public Variable<StackInterface> getStackVariable()
  {
    return mStackReference;
  }

  @Override
  public void trigger()
  {
    mTriggerVariable.setEdge(false, true);
  }

  @Override
  public void clearQueue()
  {
    mVariableStateQueues.clearQueue();
    mStackDepthVariable.set(0L);
  }

  @Override
  public void addCurrentStateToQueue()
  {
    mVariableStateQueues.addCurrentStateToQueue();
  }

  @Override
  public void finalizeQueue()
  {
    mVariableStateQueues.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return mVariableStateQueues.getQueueLength();
  }

  @Override
  public Future<Boolean> playQueue()
  {
    if (getStackRecycler() == null)
    {
      System.err.println("No recycler defined for: " + this);
      return null;
    }
    mStackDepthVariable.set((long) getQueueLength());
    // This method should be called by overriding methods of descendants.
    return null;
  }

}