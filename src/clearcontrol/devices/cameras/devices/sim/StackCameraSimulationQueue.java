package clearcontrol.devices.cameras.devices.sim;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.concurrent.timing.ExecuteMinDuration;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.devices.cameras.StackCameraQueue;
import clearcontrol.stack.StackInterface;

/**
 * Queue for stack camera simulators
 *
 * @author royer
 */
public class StackCameraSimulationQueue extends
                                        StackCameraQueue<StackCameraSimulationQueue>

{

  private final AtomicLong mTriggerCounter = new AtomicLong();
  private VariableEdgeListener<Boolean> mTriggerListener =
                                                         new VariableEdgeListener<Boolean>()
                                                         {
                                                           @Override
                                                           public void fire(Boolean pAfterEdge)
                                                           {
                                                             if (pAfterEdge)
                                                               receivedTrigger();
                                                           }
                                                         };

  private volatile StackInterface mAquiredStack;
  private CountDownLatch mAcquisitionLatch;

  /**
   * Instanciates a queue given a stack camera simulator
   * 
   */
  public StackCameraSimulationQueue()
  {
    super();
  }

  private StackCameraDeviceSimulator getStackCameraSimulator()
  {
    return (StackCameraDeviceSimulator) getStackCamera();
  }

  /**
   * Instanciates a queue given a template queue's current state
   * 
   * @param pStackCameraSimulationRealTimeQueue
   *          template queue
   * 
   */
  public StackCameraSimulationQueue(StackCameraSimulationQueue pStackCameraSimulationRealTimeQueue)
  {
    super(pStackCameraSimulationRealTimeQueue);
  }

  /**
   * Starts the acquistion.
   * 
   * @return countdown latch used to determine when the acquisition finished
   */
  public CountDownLatch startAcquisition()
  {
    getStackCameraSimulator().getTriggerVariable()
                             .addEdgeListener(mTriggerListener);

    mAcquisitionLatch = new CountDownLatch(1);

    return mAcquisitionLatch;
  }

  private void stopListeningToTrigger()
  {
    getStackCameraSimulator().getTriggerVariable()
                             .removeEdgeListener(mTriggerListener);
  }

  protected void receivedTrigger()
  {
    if (getStackCameraSimulator().isSimLogging())
      getStackCameraSimulator().info("Received Trigger");
    final long lExposuretimeInSeconds =
                                      getStackCameraSimulator().getExposureInSecondsVariable()
                                                               .get()
                                                               .longValue();
    final long lDepth = getQueueLength();

    final long lAquisitionTimeInSeconds = lDepth
                                          * lExposuretimeInSeconds;

    if (mTriggerCounter.incrementAndGet() >= lDepth)
    {
      mTriggerCounter.set(0);

      getStackCameraSimulator().executeAsynchronously(() -> {
        acquisition(lAquisitionTimeInSeconds);
      });
    }

  }

  private void acquisition(final long lAquisitionTimeInSeconds)
  {
    Runnable lSimulatedAquisition = () -> {

      stopListeningToTrigger();

      try
      {
        mAquiredStack =
                      getStackCameraSimulator().getStackCameraSimulationProvider()
                                               .getStack(getStackCameraSimulator().getStackRecycler(),
                                                         this);
      }
      catch (Throwable e)
      {
        getStackCameraSimulator().severe("Exception occured while getting stack: '%s'",
                                         e.getMessage());
        e.printStackTrace();
      }
    };
    ExecuteMinDuration.execute(lAquisitionTimeInSeconds,
                               TimeUnit.SECONDS,
                               lSimulatedAquisition);
    getStackCameraSimulator().getCurrentIndexVariable().increment();

    if (mAquiredStack == null)
      getStackCameraSimulator().severe("COULD NOT GET NEW STACK! QUEUE FULL OR INVALID STACK PARAMETERS!");
    else
    {
      mAquiredStack.setMetaData(getMetaDataVariable().get().clone());
      mAquiredStack.getMetaData()
                   .setTimeStampInNanoseconds(System.nanoTime());
      mAquiredStack.getMetaData()
                   .setIndex(getStackCameraSimulator().getCurrentIndexVariable()
                                                      .get());
      getStackCameraSimulator().getStackVariable().set(mAquiredStack);
    }

    if (mAcquisitionLatch != null)
    {
      mAcquisitionLatch.countDown();
    }
  }

}
