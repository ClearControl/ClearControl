package clearcontrol.core.concurrent.timing;

/**
 * This utility class can sync a loop to a given period.
 *
 * @author royer
 */
public class SyncAtPeriod
{

  private volatile Long mLastTime;

  /**
   * Syncs at given period. the number of nanoseconds exceeded is returned.
   * 
   * @param pPeriodInMilliseconds
   *          period in milliseconds
   * @return number of milliseconds that the period exceeded.
   */
  public long syncAtPeriod(double pPeriodInMilliseconds)
  {
    long lDeadline = 0;

    if (mLastTime != null)
    {
      lDeadline = (long) (mLastTime + (pPeriodInMilliseconds * 1.e6));

      while (System.nanoTime() < lDeadline)
      {
        try
        {
          Thread.sleep(1);
        }
        catch (InterruptedException e)
        {
        }
      }
    }

    mLastTime = System.nanoTime();

    return (long) (lDeadline == 0 ? 0
                                  : (mLastTime - lDeadline) * 1e-6);
  }
}
