package clearcontrol.devices.cameras.devices.sim;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.concurrent.timing.ExecuteMinDuration;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.devices.cameras.StackCameraDeviceBase;
import clearcontrol.devices.cameras.StackCameraRealTimeQueue;
import clearcontrol.stack.StackInterface;

/**
 * Real time queue for stack camera simulators
 *
 * @author royer
 */
public class StackCameraSimulationRealTimeQueue extends
                                                StackCameraRealTimeQueue
                                                implements
                                                AsynchronousSchedulerServiceAccess,
                                                AsynchronousExecutorServiceAccess
{

  private StackCameraDeviceSimulator mStackCamera;

  private int mStackWidth, mStackHeight, mChannel;

  private final AtomicLong mTriggeCounter = new AtomicLong();
  private VariableEdgeListener<Boolean> mTriggerListener;

  private volatile StackInterface mAquiredStack;
  private CountDownLatch mAcquisitionLatch;

  /**
   * Instanciates a queue
   * 
   * @param pStackCamera
   *          parent stack camera
   */
  public StackCameraSimulationRealTimeQueue(StackCameraDeviceSimulator pStackCamera)
  {
    super();
    mStackCamera = pStackCamera;
    mStackWidth =
                mStackCamera.getStackWidthVariable().get().intValue();
    mStackHeight = mStackCamera.getStackHeightVariable()
                               .get()
                               .intValue();

    mChannel = mStackCamera.getChannelVariable().get().intValue();

    mTriggerListener = new VariableEdgeListener<Boolean>()
    {
      @Override
      public void fire(Boolean pAfterEdge)
      {
        if (pAfterEdge)
          receivedTrigger();
      }
    };
  }

  /**
   * Returns current parent stack camera
   * 
   * @return current parent stack camera
   */
  public StackCameraDeviceBase getStackCamera()
  {
    return mStackCamera;
  }

  /**
   * Returns current stack width
   * 
   * @return current stack width
   */
  public int getStackWidth()
  {
    return mStackWidth;
  }

  /**
   * Returns current stack height
   * 
   * @return current stack height
   */
  public int getStackHeight()
  {
    return mStackHeight;
  }

  /**
   * Returns current stack channel
   * 
   * @return current stack channel
   */
  public int getChannel()
  {
    return mChannel;
  }

  /**
   * Starts the acquistion.
   * 
   * @return countdown latch used to determine when the acquisition finished
   */
  public CountDownLatch startAcquisition()
  {
    mStackCamera.getTriggerVariable()
                .addEdgeListener(mTriggerListener);

    mAcquisitionLatch = new CountDownLatch(1);

    return mAcquisitionLatch;
  }

  private void stopListeningToTrigger()
  {
    mStackCamera.getTriggerVariable()
                .removeEdgeListener(mTriggerListener);
  }

  protected void receivedTrigger()
  {
    if (mStackCamera.isSimLogging())
      mStackCamera.info("Received Trigger");
    final long lExposuretimeInMicroSeconds =
                                           mStackCamera.getExposureInMicrosecondsVariable()
                                                       .get()
                                                       .longValue();
    final long lDepth = getQueueLength();

    final long lAquisitionTimeInMicroseconds = lDepth
                                               * lExposuretimeInMicroSeconds;

    if (mTriggeCounter.incrementAndGet() >= lDepth)
    {
      mTriggeCounter.set(0);

      executeAsynchronously(() -> {
        acquisition(lAquisitionTimeInMicroseconds);
      });
    }

  }

  private void acquisition(final long lAquisitionTimeInMicroseconds)
  {
    Runnable lSimulatedAquisition = () -> {

      stopListeningToTrigger();

      try
      {
        mAquiredStack =
                      mStackCamera.getStackCameraSimulationProvider()
                                  .getStack(mStackCamera.getStackRecycler(),
                                            this);
      }
      catch (Throwable e)
      {
        mStackCamera.severe("Exception occured while getting stack: '%s'",
                            e.getMessage());
        e.printStackTrace();
      }
    };
    ExecuteMinDuration.execute(lAquisitionTimeInMicroseconds,
                               TimeUnit.MICROSECONDS,
                               lSimulatedAquisition);
    mStackCamera.getCurrentIndexVariable().increment();

    if (mAquiredStack == null)
      mStackCamera.severe("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
    else
    {

      mAquiredStack.setTimeStampInNanoseconds(System.nanoTime());
      mAquiredStack.setIndex(mStackCamera.getCurrentIndexVariable()
                                         .get());
      mAquiredStack.setNumberOfImagesPerPlane(mStackCamera.getNumberOfImagesPerPlaneVariable()
                                                          .get());
      mAquiredStack.setChannel(mStackCamera.getChannelVariable()
                                           .get());
      mStackCamera.getStackVariable().set(mAquiredStack);

    }

    if (mAcquisitionLatch != null)
    {
      try
      {
        Thread.sleep(((long) mStackCamera.getExposure() / 1000));
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      mAcquisitionLatch.countDown();
    }
  }

}
