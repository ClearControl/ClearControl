package clearcontrol.core.concurrent.timing;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import clearcontrol.core.concurrent.thread.ThreadSleep;

/**
 *
 *
 * @author royer
 */
public interface WaitingInterface
{

  /**
   * Waits until call to Callable returns true.
   * 
   * @param pCallable
   *          condition to wait for
   * @return last boolean state returned
   */
  default public Boolean waitFor(Callable<Boolean> pCallable)
  {
    return waitFor(Long.MAX_VALUE, TimeUnit.DAYS, pCallable);
  }

  /**
   * Waits until call to Callable returns true.
   * 
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time out unit
   * @param pCallable
   *          callable returning boolean state
   * @return last boolean state returned
   */
  default public Boolean waitFor(Long pTimeOut,
                                 TimeUnit pTimeUnit,
                                 Callable<Boolean> pCallable)
  {
    synchronized (this)
    {
      return waitForStatic(pTimeOut, pTimeUnit, pCallable);
    }
  }

  /**
   * Waits until call to Callable returns true. Static version.
   * 
   * @param pCallable
   *          condition to wait for
   * @return last boolean state returned
   */
  public static Boolean waitForStatic(Callable<Boolean> pCallable)
  {
    return waitForStatic(Long.MAX_VALUE, TimeUnit.DAYS, pCallable);
  }

  /**
   * Waits until call to Callable returns true. Static version.
   * 
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time out unit
   * @param pCallable
   *          callable returning boolean state
   * @return last boolean state returned
   */
  public static Boolean waitForStatic(Long pTimeOut,
                                      TimeUnit pTimeUnit,
                                      Callable<Boolean> pCallable)
  {
    try
    {
      AtomicLong lCounter = new AtomicLong();
      long lTimeOutInMillis =
                            pTimeUnit == null ? 0
                                              : pTimeUnit.toMillis(pTimeOut);
      while (!pCallable.call()
             && (pTimeOut == null
                 || lCounter.incrementAndGet() < lTimeOutInMillis))
      {
        ThreadSleep.sleep(1, TimeUnit.MILLISECONDS);
      }
      return pCallable.call();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
