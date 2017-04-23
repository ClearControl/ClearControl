package clearcontrol.microscope.timelapse.timer;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;

/**
 * Interface implemented by all timelapse timers
 *
 * @author royer
 */
public interface TimelapseTimerInterface
{

  /**
   * Resets the timer
   */
  public void reset();

  /**
   * Returns the time left in the given unit until next time point is due.
   * 
   * @param pTimeUnit
   *          time unit
   * @return time left before next time point
   */
  public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit);

  /**
   * Returns true if there is enough time to complete a task of given duration
   * before the next time point is due.
   * 
   * @param pTimeNeeded
   *          time needed
   * @param pReservedTime
   *          reserved time
   * @param pTimeUnit
   *          time unit
   * @return true if there is enough time
   */

  public default boolean enoughTimeFor(long pTimeNeeded,
                                       long pReservedTime,
                                       TimeUnit pTimeUnit)
  {
    if (pTimeNeeded < 0)
      return false;

    long lTimeNeededInNS = TimeUnit.NANOSECONDS.convert(pTimeNeeded,
                                                        pTimeUnit);
    long lReservedTimeInNS =
                           TimeUnit.NANOSECONDS.convert(pReservedTime,
                                                        pTimeUnit);
    long lTimeLeftNoReserveInNS =
                                timeLeftBeforeNextTimePoint(TimeUnit.NANOSECONDS);

    long lTimeLeftInNS = lTimeLeftNoReserveInNS - lReservedTimeInNS;

    boolean lEnoughTime = lTimeLeftInNS > lTimeNeededInNS;

    return lEnoughTime;
  }

  /**
   * Waits until next acquisition is due, with a timeout.
   * 
   * @param pTimeout
   *          timeout
   * @param pTimeUnit
   *          time unit for timeout
   * @return true if succeeded without timeout
   */
  public boolean waitToAcquire(long pTimeout, TimeUnit pTimeUnit);

  /**
   * Returns the last acquisition time in the given unit
   * 
   * @param pTimeUnit
   *          time unit
   * @return last acquisition time
   */
  public long getLastAcquisitionTime(TimeUnit pTimeUnit);

  /**
   * Notifies of acquisition. This must be called _just_ before the acquistion
   * starts (not after!).
   */
  void notifyAcquisition();

  /**
   * Returns the current acquisition interval in the given time unit
   * 
   * @param pTimeUnit
   *          time unit
   * @return acquisition interval
   */
  long getAcquisitionInterval(TimeUnit pTimeUnit);

  /**
   * Returns acquisition interval variable
   * 
   * @return acquisition interval variable
   */
  Variable<Long> getAcquisitionIntervalVariable();

  /**
   * Returns actual acquisition interval variable This the last measured
   * interval between the last acquired time point and the one before that.
   * 
   * @return actual acquisition interval variable
   */
  Variable<Long> getActualAcquisitionIntervalVariable();

  /**
   * Returns acquisition interval unit variable
   * 
   * @return acquisition interval unit variable
   */
  public Variable<TimeUnitEnum> getAcquisitionIntervalUnitVariable();

  /**
   * Returns actual acquisition interval unit variable
   * 
   * @return actual acquisition interval unit variable
   */
  public Variable<TimeUnitEnum> getActualAcquisitionIntervalUnitVariable();

}
