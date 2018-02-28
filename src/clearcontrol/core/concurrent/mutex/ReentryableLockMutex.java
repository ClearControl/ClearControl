package clearcontrol.core.concurrent.mutex;

import java.util.concurrent.Semaphore;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class ReentryableLockMutex
{
  Semaphore mSemaphore = new Semaphore(1);
  Object mLock = null;


  public synchronized boolean lock(Object pLock) throws
                                                 InterruptedException
  {
    if (mLock == pLock) {
      // You had the lock already, just go ahead.
      return true;
    }

    mSemaphore.acquire();
    mLock = pLock;
    return true;
  }

  public synchronized boolean unlock(Object pLock) throws
                                                   InterruptedException
  {
    if (mLock != pLock) {
      // you don't have the lock. I won't unlock.
      return false;
    }

    mLock = null;
    mSemaphore.release();
    return true;
  }

}
