package clearcontrol.devices.cameras;

import java.util.concurrent.Future;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
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
                                            LoggingInterface

{
  protected Variable<Boolean> mStackMode;

  protected Variable<Long> mNumberOfImagesPerPlaneVariable,
      mStackBytesPerPixelVariable, mStackWidthVariable,
      mStackHeightVariable, mStackMaxWidthVariable,
      mStackMaxHeightVariable, mStackDepthVariable;

  protected RecyclerInterface<StackInterface, StackRequest> mRecycler;

  protected Variable<StackInterface> mStackVariable;

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

    mStackMode = new Variable<Boolean>("StackMode", true);

    mNumberOfImagesPerPlaneVariable =
                                    new Variable<Long>("NumberOfImagesPerPlane",
                                                       1L);

    mChannelVariable = new Variable<Integer>("Channel", 0);

    mLineReadOutTimeInMicrosecondsVariable =
                                           new Variable<Double>("LineReadOutTimeInMicroseconds",
                                                                1.0);

    mStackBytesPerPixelVariable =
                                new Variable<Long>("FrameBytesPerPixel",
                                                   2L);

    mStackWidthVariable = new Variable<Long>("FrameWidth", 320L);

    mStackHeightVariable = new Variable<Long>("FrameHeight", 320L);

    mStackMaxWidthVariable =
                           new Variable<Long>("FrameMaxWidth", 2048L);

    mStackMaxHeightVariable = new Variable<Long>("FrameMaxHeight",
                                                 2048L);

    mStackDepthVariable = new Variable<Long>("FrameDepth", 100L);

    mExposureInMicrosecondsVariable =
                                    new Variable<Double>("ExposureInMicroseconds",
                                                         1000.0);

    mPixelSizeinNanometersVariable =
                                   new Variable<Double>("PixelSizeinNanometers",
                                                        160.0);

    mStackVariable = new Variable<>("StackReference");
  }

  @Override
  public long getCurrentStackIndex()
  {
    return getCurrentIndexVariable().get();
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
  public Variable<StackInterface> getStackVariable()
  {
    return mStackVariable;
  }

  @Override
  public void trigger()
  {
    mTriggerVariable.setEdge(false, true);
  }

  @Override
  public StackCameraRealTimeQueue requestQueue()
  {
    return new StackCameraRealTimeQueue();
  }

  @Override
  public Future<Boolean> playQueue(StackCameraRealTimeQueue pQueue)
  {
    if (getStackRecycler() == null)
    {
      severe("No recycler defined for: " + this);
      return null;
    }
    mStackDepthVariable.set((long) pQueue.getQueueLength());
    // This method should be called by overriding methods of descendants.
    return null;
  }

}