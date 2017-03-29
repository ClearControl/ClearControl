package clearcontrol.devices.cameras.devices.sim;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceBase;
import clearcontrol.devices.cameras.StackCameraRealTimeQueue;
import clearcontrol.devices.cameras.devices.sim.providers.RandomStackProvider;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;

/**
 * Stack camera simulator.
 *
 * @author royer
 */
public class StackCameraDeviceSimulator extends StackCameraDeviceBase
                                        implements
                                        LoggingInterface,

                                        SimulationDeviceInterface
{
  private StackCameraSimulationProvider mStackCameraSimulationProvider;

  /**
   * Crates a Stack Camera Device Simulator of a given name. Stacks from a
   * default noisy stack provider are sent to the output variable when a
   * positive edge is sent to the trigger variable (false -> true). the stack
   * provider can be changed at any point if needed.
   * 
   * @param pDeviceName
   *          camera device name
   * @param pTriggerVariable
   *          trigger variable
   *
   */
  public StackCameraDeviceSimulator(String pDeviceName,
                                    Variable<Boolean> pTriggerVariable)
  {
    this(pDeviceName, new RandomStackProvider(), pTriggerVariable);
  }

  /**
   * Crates a StackCameraDeviceSimulator of a given name. Stacks from the given
   * stack provider are sent to the output variable when a positive edge is sent
   * to the trigger variable (false -> true).
   * 
   * @param pDeviceName
   *          camera device name
   * @param pStackCameraSimulationProvider
   *          stack provider
   * @param pTriggerVariable
   *          trigger variable
   *
   */
  public StackCameraDeviceSimulator(String pDeviceName,
                                    StackCameraSimulationProvider pStackCameraSimulationProvider,
                                    Variable<Boolean> pTriggerVariable)
  {
    super(pDeviceName);
    setStackCameraSimulationProvider(pStackCameraSimulationProvider);
    mTriggerVariable = pTriggerVariable;

    if (mTriggerVariable == null)
    {
      severe("cameras",
             "Cannot instantiate "
                        + StackCameraDeviceSimulator.class.getSimpleName()
                        + " because trigger variable is null!");
      return;
    }

    mStackWidthVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info(getName() + ": New camera width: " + n);
    });

    mStackHeightVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info(getName() + ": New camera height: " + n);
    });

    mStackDepthVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info(getName() + ": New camera stack depth: " + n);
    });

    mExposureInMicrosecondsVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info(getName() + ": New camera exposure: " + n);
    });

    final ContiguousOffHeapPlanarStackFactory lContiguousOffHeapPlanarStackFactory =
                                                                                   new ContiguousOffHeapPlanarStackFactory();

    mRecycler =
              new BasicRecycler<StackInterface, StackRequest>(lContiguousOffHeapPlanarStackFactory,
                                                              40);

  }

  /**
   * Sets the stack provider
   * 
   * @param pStackCameraSimulationProvider
   *          new stack provider
   */
  public void setStackCameraSimulationProvider(StackCameraSimulationProvider pStackCameraSimulationProvider)
  {
    mStackCameraSimulationProvider = pStackCameraSimulationProvider;
  }

  /**
   * Returns the stack provider
   * 
   * @return current state provider.
   */
  public StackCameraSimulationProvider getStackCameraSimulationProvider()
  {
    return mStackCameraSimulationProvider;
  }

  @Override
  public void reopen()
  {
    return;
  }

  @Override
  public boolean start()
  {

    return true;
  }

  @Override
  public boolean stop()
  {

    return true;
  }

  @Override
  public StackCameraRealTimeQueue requestQueue()
  {
    return new StackCameraSimulationRealTimeQueue(this);
  }

  @Override
  public Future<Boolean> playQueue(StackCameraRealTimeQueue pQueue)
  {
    if (isSimLogging())
      info("Playing queue...");

    StackCameraSimulationRealTimeQueue lQueue =
                                              (StackCameraSimulationRealTimeQueue) pQueue;

    final CountDownLatch lLatch = lQueue.startAcquisition();

    super.playQueue(pQueue);

    final Future<Boolean> lFuture = new Future<Boolean>()
    {

      @Override
      public boolean cancel(boolean pMayInterruptIfRunning)
      {
        return false;
      }

      @Override
      public boolean isCancelled()
      {
        return false;
      }

      @Override
      public boolean isDone()
      {
        return false;
      }

      @Override
      public Boolean get() throws InterruptedException,
                           ExecutionException
      {
        lLatch.await();
        return true;
      }

      @Override
      public Boolean get(long pTimeout,
                         TimeUnit pUnit) throws InterruptedException,
                                         ExecutionException,
                                         TimeoutException
      {
        lLatch.await(pTimeout, pUnit);
        return true;
      }
    };

    return lFuture;
  }

}
