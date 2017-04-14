package clearcontrol.gui.jfx.var.combo.enums;

import java.util.concurrent.TimeUnit;

/**
 * Enum for time units
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum TimeUnitEnum
{

 Nanoseconds(TimeUnit.NANOSECONDS),
 Microseconds(TimeUnit.MICROSECONDS),
 Milliseconds(TimeUnit.MILLISECONDS),
 Seconds(TimeUnit.SECONDS),
 Minutes(TimeUnit.MINUTES),
 Hours(TimeUnit.HOURS),
 Days(TimeUnit.DAYS);

  TimeUnit mTimeUnit;

  private TimeUnitEnum(TimeUnit pTimeUnit)
  {
    mTimeUnit = pTimeUnit;
  }

  /**
   * Returns the time unit
   * 
   * @return time unit
   */
  public TimeUnit getTimeUnit()
  {
    return mTimeUnit;
  }

}
