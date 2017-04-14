package clearcontrol.microscope.timelapse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.task.LoopTaskDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;
import clearcontrol.microscope.timelapse.timer.fixed.FixedIntervalTimelapseTimer;

/**
 * Base implementation providing common fields and methods for all Timelapse
 * implementations
 *
 * @author royer
 */
public abstract class TimelapseBase extends LoopTaskDevice
                                    implements TimelapseInterface
{
  private final Variable<TimelapseTimerInterface> mTimelapseTimer =
                                                                  new Variable<>("TimelapseTimer",
                                                                                 null);

  private final Variable<Boolean> mEnforceMaxNumberOfTimePointsVariable =
                                                                        new Variable<>("LimitNumberOfTimePoints",
                                                                                       true);

  private final Variable<Boolean> mEnforceMaxDurationVariable =
                                                              new Variable<>("LimitTimelapseDuration",
                                                                             false);

  private final Variable<Boolean> mEnforceMaxDateTimeVariable =
                                                              new Variable<>("LimitTimelapseDateTime",
                                                                             false);

  private final Variable<Long> mMaxNumberOfTimePointsVariable =
                                                              new Variable<Long>("MaxNumberOfTimePoints",
                                                                                 1000L);

  private final Variable<Long> mMaxDurationVariable =
                                                    new Variable<Long>("MaxDuration",
                                                                       24L);

  private final Variable<TimeUnitEnum> mMaxDurationUnitVariable =
                                                                new Variable<TimeUnitEnum>("MaxDurationUnit",
                                                                                           TimeUnitEnum.Hours);

  private final Variable<LocalDateTime> mMaxDateTimeVariable =
                                                             new Variable<LocalDateTime>("MaxDateTime",
                                                                                         LocalDateTime.now());

  private final Variable<LocalDateTime> mStartDateTimeVariable =
                                                               new Variable<LocalDateTime>("StartDateTime",
                                                                                           LocalDateTime.now());

  private final Variable<Long> mTimePointCounterVariable =
                                                         new Variable<Long>("TimePointCounter",
                                                                            1L);

  /**
   * Instanciates a timelapse with a given timelapse timer
   * 
   * @param pTimelapseTimer
   *          timelapse timer
   */
  public TimelapseBase(TimelapseTimerInterface pTimelapseTimer)
  {
    super("Timelapse");
    getTimelapseTimerVariable().set(pTimelapseTimer);
  }

  /**
   * Instanciates a timelapse with a fixed interval timer
   */
  public TimelapseBase()
  {
    this(new FixedIntervalTimelapseTimer());
  }

  @Override
  public void run()
  {
    super.run();
  }

  @Override
  public boolean startTask()
  {
    if (!getIsRunningVariable().get())
    {
      getTimePointCounterVariable().set(0L);
      getStartDateTimeVariable().set(LocalDateTime.now());
    }
    return super.startTask();
  }

  @Override
  public void stopTask()
  {
    super.stopTask();
  }

  @Override
  public boolean loop()
  {
    if (getTimelapseTimerVariable() == null)
      return false;

    TimelapseTimerInterface lTimelapseTimer =
                                            getTimelapseTimerVariable().get();

    lTimelapseTimer.waitToAcquire(1, TimeUnit.DAYS);
    lTimelapseTimer.notifyAcquisition();
    acquire();

    getTimePointCounterVariable().increment();

    if (getEnforceMaxNumberOfTimePointsVariable().get())
      if (getTimePointCounterVariable().get() >= getMaxNumberOfTimePointsVariable().get())
        return false;

    if (getEnforceMaxDurationVariable().get()
        && getMaxDurationVariable().get() != null)
      if (checkMaxDuration())
        return false;

    if (getEnforceMaxDateTimeVariable().get()
        && getMaxDateTimeVariable().get() != null)
      if (checkMaxDateTime())
        return false;

    return true;
  }

  private boolean checkMaxDuration()
  {
    LocalDateTime lStartDateTime = getStartDateTimeVariable().get();

    Duration lDuration = Duration.between(lStartDateTime,
                                          LocalDateTime.now());

    long lCurrentlMeasuredDurationInNanos = lDuration.toNanos();

    long lMaxDurationInNanos =
                             TimeUnit.NANOSECONDS.convert(getMaxDurationVariable().get(),
                                                          getMaxDurationUnitVariable().get()
                                                                                      .getTimeUnit());

    long lTimeLeft = lMaxDurationInNanos
                     - lCurrentlMeasuredDurationInNanos;

    boolean lTimeIsOut = lTimeLeft < 0;

    return lTimeIsOut;
  }

  private boolean checkMaxDateTime()
  {
    LocalDateTime lMaxDateTime = getMaxDateTimeVariable().get();
    LocalDateTime lNowDateTime = LocalDateTime.now();

    return lNowDateTime.isAfter(lMaxDateTime);
  }

  @Override
  public abstract void acquire();

  @Override
  public Variable<TimelapseTimerInterface> getTimelapseTimerVariable()
  {
    return mTimelapseTimer;
  }

  @Override
  public Variable<Boolean> getEnforceMaxNumberOfTimePointsVariable()
  {
    return mEnforceMaxNumberOfTimePointsVariable;
  }

  @Override
  public Variable<Boolean> getEnforceMaxDurationVariable()
  {
    return mEnforceMaxDurationVariable;
  }

  @Override
  public Variable<Boolean> getEnforceMaxDateTimeVariable()
  {
    return mEnforceMaxDateTimeVariable;
  }

  @Override
  public Variable<Long> getMaxNumberOfTimePointsVariable()
  {
    return mMaxNumberOfTimePointsVariable;
  }

  @Override
  public Variable<Long> getMaxDurationVariable()
  {
    return mMaxDurationVariable;
  }

  @Override
  public Variable<TimeUnitEnum> getMaxDurationUnitVariable()
  {
    return mMaxDurationUnitVariable;
  }

  @Override
  public Variable<LocalDateTime> getMaxDateTimeVariable()
  {
    return mMaxDateTimeVariable;
  }

  @Override
  public Variable<LocalDateTime> getStartDateTimeVariable()
  {
    return mStartDateTimeVariable;
  }

  @Override
  public Variable<Long> getTimePointCounterVariable()
  {
    return mTimePointCounterVariable;
  }

}
