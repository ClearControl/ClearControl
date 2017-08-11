package clearcontrol.core.device.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.startstop.SignalStartStopDevice;
import clearcontrol.core.device.startstop.StartStopSignalVariablesInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;

/**
 * Base class for task devices
 *
 * @author royer
 */
public abstract class TaskDevice extends SignalStartStopDevice
                                 implements
                                 Runnable,
                                 StartStopSignalVariablesInterface,
                                 IsRunningTaskInterface,
                                 OpenCloseDeviceInterface,
                                 AsynchronousExecutorFeature,
                                 LoggingFeature
{

  private final Variable<Boolean> mIsRunningVariable;
  private final Variable<Throwable> mLastExceptionVariable;

  private volatile CountDownLatch mStartedLatch, mStoppedLatch;
  private volatile Runnable mRunnableWrapper;
  private volatile Future<?> mTaskFuture;

  /**
   * Instanciates a task device given a device name
   * 
   * @param pDeviceName
   *          device name
   */
  public TaskDevice(final String pDeviceName)
  {
    this(pDeviceName, Thread.NORM_PRIORITY);
  }

  /**
   * Instanciates a task device given a device name and thread priority.
   * 
   * @param pDeviceName
   *          device name
   * @param pThreadPriority
   *          thread priority
   */
  public TaskDevice(final String pDeviceName, int pThreadPriority)
  {
    super(pDeviceName);

    setTaskOnStart(this::startTask);

    mIsRunningVariable = new Variable<Boolean>(pDeviceName
                                               + "IsRunning", false);

    mLastExceptionVariable = new Variable<Throwable>(pDeviceName
                                                     + "LastException",
                                                     null);

    ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                        pThreadPriority,
                                                        1,
                                                        1,
                                                        Integer.MAX_VALUE);
  }

  /**
   * Returns the boolean variable that indicates whether the task is currently
   * running.
   * 
   * @return is-running variable
   */
  @Override
  public Variable<Boolean> getIsRunningVariable()
  {
    return mIsRunningVariable;
  }

  /**
   * Starts this task
   * 
   * @return true if succeeded
   */
  public boolean startTask()
  {
    if (mTaskFuture != null && !mTaskFuture.isDone())
      return false;

    mStartedLatch = new CountDownLatch(1);
    mStoppedLatch = new CountDownLatch(1);

    mRunnableWrapper = () -> {
      mStopSignal.set(false);
      mIsRunningVariable.setEdge(false, true);
      mStartedLatch.countDown();
      try
      {
        run();
      }
      catch (Throwable e)
      {
        e.printStackTrace();
        mLastExceptionVariable.set(e);
      }
      finally
      {
        mIsRunningVariable.setEdge(true, false);
        mStoppedLatch.countDown();
      }
    };

    mTaskFuture = executeAsynchronously(mRunnableWrapper);
    return mTaskFuture != null;
  }

  /**
   * Stops this task
   */
  public void stopTask()
  {
    mStopSignal.setEdge(false, true);
  }

  /**
   * Clears task (does not stop a running task so use wisely)
   */
  public void clearTask()
  {
    mStopSignal.set(false);
    mIsRunningVariable.set(false);
    mTaskFuture = null;
  }

  /**
   * Waits for task to start
   * 
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time unit
   * @return true -> success, false -> timeout
   */
  public boolean waitForStarted(long pTimeOut, TimeUnit pTimeUnit)
  {
    try
    {
      if (mStartedLatch != null)
        return mStartedLatch.await(pTimeOut, pTimeUnit);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Waits for task to stop
   * 
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time unit
   * @return true -> success, false -> timeout
   */
  public boolean waitForStopped(int pTimeOut, TimeUnit pTimeUnit)
  {
    try
    {
      boolean lResult = false;
      if (mStoppedLatch != null)
        lResult = mStoppedLatch.await(pTimeOut, pTimeUnit);
      else
        lResult = true;

      return lResult;
    }
    catch (Throwable e)
    {
      String lError =
                    "Error during previous execution of loop function!";
      severe("Device", lError, e);
      return false;
    }

  }

  @Override
  public boolean open()
  {
    // nothing to do
    return true;
  }

  @Override
  public boolean close()
  {
    stopTask();
    return waitForStopped(100, TimeUnit.SECONDS);
  }

}
