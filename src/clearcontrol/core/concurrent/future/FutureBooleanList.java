package clearcontrol.core.concurrent.future;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.log.LoggingInterface;

/**
 * Set of Futures that return booleans.
 *
 * @author royer
 */
public class FutureBooleanList implements
                               Future<Boolean>,
                               LoggingInterface
{

  LinkedHashMap<Future<Boolean>, String> mFutureMap =
                                                    new LinkedHashMap<Future<Boolean>, String>();

  /**
   * Instanciates a future boolean list
   */
  public FutureBooleanList()
  {
    super();
  }

  /**
   * Adds a future with a given string id
   * 
   * @param pString
   *          string id
   * @param pFuture
   *          future
   */
  public void addFuture(String pString, Future<Boolean> pFuture)
  {
    mFutureMap.put(pFuture, pString);
  }

  @Override
  public boolean cancel(boolean pMayInterruptIfRunning)
  {
    for (final Future<Boolean> lFuture : mFutureMap.keySet())
      if (!lFuture.cancel(pMayInterruptIfRunning))
        return false;
    return true;
  }

  @Override
  public boolean isCancelled()
  {
    for (final Future<Boolean> lFuture : mFutureMap.keySet())
    {
      if (!lFuture.isCancelled())
        return false;
    }
    return true;
  }

  @Override
  public boolean isDone()
  {
    for (final Future<Boolean> lFuture : mFutureMap.keySet())
    {
      if (!lFuture.isDone())
        return false;
    }
    return true;
  }

  @Override
  public Boolean get() throws InterruptedException, ExecutionException
  {
    for (final Future<Boolean> lFuture : mFutureMap.keySet())
    {
      final Boolean lBoolean = lFuture.get();
      if (lBoolean == null || !lBoolean)
        return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  @Override
  public Boolean get(long pTimeout, TimeUnit pUnit)
                                                    throws InterruptedException,
                                                    ExecutionException,
                                                    TimeoutException
  {
    for (final Future<Boolean> lFuture : mFutureMap.keySet())
      if (lFuture != null)
      {
        try
        {
          if (!lFuture.get(pTimeout, pUnit))
            return Boolean.FALSE;
        }
        catch (TimeoutException e)
        {
          warning("Timeout caused by: %s \n",
                  mFutureMap.get(lFuture).trim());

          throw e;
        }
      }
    return Boolean.TRUE;
  }

}
