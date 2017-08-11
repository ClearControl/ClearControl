package clearcontrol.core.concurrent.executors;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Scheduled future that can wait for completion
 *
 * @param <V>
 *          future return type
 * @author royer
 */
public class WaitingScheduledFuture<V> implements ScheduledFuture<V>
{

  private final ScheduledFuture<V> mDelegatedScheduledFuture;

  /**
   * Instantiates a waiting scheduled future given a delegated scheduled future
   * 
   * @param pDelegatedScheduledFuture
   *          delegated scheduled future
   */
  public WaitingScheduledFuture(ScheduledFuture<V> pDelegatedScheduledFuture)
  {
    mDelegatedScheduledFuture = pDelegatedScheduledFuture;
  }

  @Override
  public long getDelay(TimeUnit pUnit)
  {
    return mDelegatedScheduledFuture.getDelay(pUnit);
  }

  @Override
  public int compareTo(Delayed pO)
  {
    return mDelegatedScheduledFuture.compareTo(pO);
  }

  @Override
  public boolean cancel(boolean pMayInterruptIfRunning)
  {
    return mDelegatedScheduledFuture.cancel(pMayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled()
  {
    return mDelegatedScheduledFuture.isCancelled();
  }

  @Override
  public boolean isDone()
  {
    return mDelegatedScheduledFuture.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException
  {
    try
    {
      return mDelegatedScheduledFuture.get();
    }
    catch (final CancellationException | InterruptedException e)
    {
      return null;
    }
  }

  @Override
  public V get(long pTimeout,
               TimeUnit pUnit) throws InterruptedException,
                               ExecutionException,
                               TimeoutException
  {
    try
    {
      return mDelegatedScheduledFuture.get(pTimeout, pUnit);
    }
    catch (final CancellationException | InterruptedException e)
    {
      return null;
    }
  }

  /**
   * Waist for completion of the delegated scheduled future or returns after
   * timeout.
   * 
   * @param pTimeout
   *          timeout
   * @param pUnit
   *          timeout unit
   * @return true if execution finished before timeout
   * @throws ExecutionException
   *           thrown if exception occured during execution
   */
  public boolean waitForCompletion(long pTimeout,
                                   TimeUnit pUnit) throws ExecutionException
  {
    try
    {
      mDelegatedScheduledFuture.get(pTimeout, pUnit);
      return true;
    }
    catch (final TimeoutException e)
    {
      return false;
    }
    catch (final CancellationException e)
    {
      return true;
    }
    catch (final InterruptedException e)
    {
      waitForCompletion(pTimeout, pUnit);
    }
    return false;

  }

}
