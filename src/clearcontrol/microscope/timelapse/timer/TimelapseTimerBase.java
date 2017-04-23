package clearcontrol.microscope.timelapse.timer;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;

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

  private volatile long mLastAcquisitionTimeInNS = -1;
  private final Variable<Long> mAcquisitionIntervalVariable =
                                                            new Variable<Long>("AcquisitionInterval",
                                                                               1L);
  private final Variable<Long> mActualAcquisitionIntervalVariable =
                                                                  new Variable<Long>("ActualAcquisitionInterval",
                                                                                     0L);
  private final Variable<TimeUnitEnum> mAcquisitionIntervalUnitVariable =
                                                                        new Variable<TimeUnitEnum>("AcquisitionIntervalUnit",
                                                                                                   TimeUnitEnum.Seconds);
  private final Variable<TimeUnitEnum> mActualAcquisitionIntervalUnitVariable =
                                                                              new Variable<TimeUnitEnum>("ActualAcquisitionIntervalUnit",
                                                                                                         TimeUnitEnum.Milliseconds);

  /**
   * Instanciates a timelapse timer for a given acquisition interval and time
   * unit
   * 
   * @param pAcquisitionInterval
   *          acquisition interval
   * @param pTimeUnit
   *          time unit
   */
  public TimelapseTimerBase(long pAcquisitionInterval,
                            TimeUnit pTimeUnit)
  {
    super();
    setAcquisitionInterval(pAcquisitionInterval, pTimeUnit);

    mActualAcquisitionIntervalUnitVariable.addSetListener((o, n) -> {
      if (o != n)
      {
        // converts the actual acquisition time to the new unit...
        long lActualAcquisitionTime = n.getTimeUnit()
                                       .convert(mActualAcquisitionIntervalVariable.get(),
                                                o.getTimeUnit());
        mActualAcquisitionIntervalVariable.set(lActualAcquisitionTime);
      }
    });
  }

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
    long lLastAcquisitionTimeInNs =
                                  TimeUnit.NANOSECONDS.convert(pLastAcquisitionTime,
                                                               pTimeUnit);

    if (mLastAcquisitionTimeInNS != -1)
    {
      long lActualAcquisitionTime =
                                  getActualAcquisitionIntervalUnitVariable().get()
                                                                            .getTimeUnit()
                                                                            .convert(lLastAcquisitionTimeInNs
                                                                                     - mLastAcquisitionTimeInNS,
                                                                                     TimeUnit.NANOSECONDS);
      mActualAcquisitionIntervalVariable.set(lActualAcquisitionTime);
    }

    mLastAcquisitionTimeInNS = lLastAcquisitionTimeInNs;
  }

  @Override
  public long getAcquisitionInterval(TimeUnit pTimeUnit)
  {
    TimeUnitEnum lTimeUnitEnum =
                               mAcquisitionIntervalUnitVariable.get();
    return pTimeUnit.convert(mAcquisitionIntervalVariable.get(),
                             lTimeUnitEnum.getTimeUnit());
  }

  @Override
  public Variable<Long> getAcquisitionIntervalVariable()
  {
    return mAcquisitionIntervalVariable;
  }

  @Override
  public Variable<TimeUnitEnum> getAcquisitionIntervalUnitVariable()
  {
    return mAcquisitionIntervalUnitVariable;
  }

  @Override
  public Variable<Long> getActualAcquisitionIntervalVariable()
  {
    return mActualAcquisitionIntervalVariable;
  }

  @Override
  public Variable<TimeUnitEnum> getActualAcquisitionIntervalUnitVariable()
  {
    return mActualAcquisitionIntervalUnitVariable;
  }

  @Override
  public void reset()
  {
    mLastAcquisitionTimeInNS = -1;
  }

  /**
   * Sets acquisition interval in the given time unit
   * 
   * @param pAcquisitionInterval
   *          acquisition interval
   * @param pTimeUnit
   *          time unit
   */
  public void setAcquisitionInterval(long pAcquisitionInterval,
                                     TimeUnit pTimeUnit)
  {
    TimeUnitEnum lTimeUnitEnum =
                               mAcquisitionIntervalUnitVariable.get();
    mAcquisitionIntervalVariable.set(lTimeUnitEnum.getTimeUnit()
                                                  .convert(pAcquisitionInterval,
                                                           pTimeUnit));
  }

  @Override
  public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit)
  {
    long lTimeLeftBeforeNextTimePointInNS =
                                          (getLastAcquisitionTime(TimeUnit.NANOSECONDS)
                                           + getAcquisitionInterval(TimeUnit.NANOSECONDS))
                                            - System.nanoTime();

    long lTimeLeftInTimeUnit =
                             pTimeUnit.convert(lTimeLeftBeforeNextTimePointInNS,
                                               TimeUnit.NANOSECONDS);

    // System.out.println("acq interval =
    // "+getAcquisitionInterval(TimeUnit.MILLISECONDS));
    // System.out.println("time left= "+lTimeLeftInTimeUnit);

    return lTimeLeftInTimeUnit;
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

    return lNowRef.now < lTimeOut;
  }

  @Override
  public void notifyAcquisition()
  {
    setLastAcquisitionTime(System.nanoTime(), TimeUnit.NANOSECONDS);
  }

}
