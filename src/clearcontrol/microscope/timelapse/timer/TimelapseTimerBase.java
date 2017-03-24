package clearcontrol.microscope.timelapse.timer;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.timing.WaitingInterface;

/**
 * Base class providing common fields and methods for all timelapse timer
 * implementations
 *
 * @author royer
 */
public class TimelapseTimerBase implements
                                TimelapseTimerInterface,
                                WaitingInterface
{

  private volatile long mLastAcquisitionTimeInNS = System.nanoTime();
  private volatile long mAcquisitionIntervalInNS;

  @Override
  public long getLastAcquisitionTime(TimeUnit pTimeUnit)
  {
    return pTimeUnit.convert(mLastAcquisitionTimeInNS,
                             TimeUnit.NANOSECONDS);
  }

  /**
   * Sets last acquisition time in the given time unit
   * 
   * @param pLastAcquisitionTime
   *          last acquisition time
   * @param pTimeUnit
   *          time unit
   */
  public void setLastAcquisitionTime(long pLastAcquisitionTime,
                                     TimeUnit pTimeUnit)
  {
    mLastAcquisitionTimeInNS =
                             TimeUnit.NANOSECONDS.convert(pLastAcquisitionTime,
                                                          pTimeUnit);
  }

  /**
   * Returns acquisition interval in the given time unit
   * 
   * @param pTimeUnit
   *          time unit
   * @return acquisition interval
   */
  public long getAcquisitionInterval(TimeUnit pTimeUnit)
  {
    return pTimeUnit.convert(mAcquisitionIntervalInNS,
                             TimeUnit.NANOSECONDS);
  }

  /**
   * Sets acquisistion interval in the given time unit
   * 
   * @param pAcquisitionInterval
   *          acquisition interval
   * @param pTimeUnit
   *          time unit
   */
  public void setAcquisitionInterval(long pAcquisitionInterval,
                                     TimeUnit pTimeUnit)
  {
    mAcquisitionIntervalInNS =
                             TimeUnit.NANOSECONDS.convert(pAcquisitionInterval,
                                                          pTimeUnit);
  }

  @Override
  public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit)
  {
    long lTimeLeftBeforeNextTimePointInNS =
                                          (getLastAcquisitionTime(TimeUnit.NANOSECONDS)
                                           + getAcquisitionInterval(TimeUnit.NANOSECONDS))
                                            - System.nanoTime();
    return pTimeUnit.convert(lTimeLeftBeforeNextTimePointInNS,
                             TimeUnit.NANOSECONDS);
  }

  @Override
  public boolean waitToAcquire(long pTimeOut, TimeUnit pTimeUnit)
  {
    long lNow = System.nanoTime();
    long lTimeOut = lNow + TimeUnit.NANOSECONDS.convert(pTimeOut,
                                                        pTimeUnit);

    class NowRef
    {
      public long now;
    }

    final NowRef lNowRef = new NowRef();

    waitFor(() -> timeLeftBeforeNextTimePoint(TimeUnit.NANOSECONDS) <= 0
                  || (lNowRef.now = System.nanoTime()) > lTimeOut);
    return lNowRef.now > lTimeOut;
  }

  @Override
  public void notifyAcquisition()
  {
    setLastAcquisitionTime(System.nanoTime(), TimeUnit.NANOSECONDS);
  }

}
