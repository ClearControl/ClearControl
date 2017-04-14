package clearcontrol.microscope.timelapse;

import java.time.LocalDateTime;

import clearcontrol.core.device.startstop.StartStopSignalVariablesInterface;
import clearcontrol.core.device.task.IsRunningTaskInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

/**
 * Interface implemented by all timelapse devices.
 *
 * @author royer
 */
public interface TimelapseInterface extends
                                    StartStopSignalVariablesInterface,
                                    IsRunningTaskInterface

{

  /**
   * Acquires a single timepoint
   */
  void acquire();

  /**
   * Returns the timelapse timer variable
   * 
   * @return timelapse variable
   */
  Variable<TimelapseTimerInterface> getTimelapseTimerVariable();

  /**
   * Returns boolean variable deciding whether to limit the numbr of time points
   * 
   * @return true -> number of timepoints limited
   */
  Variable<Boolean> getEnforceMaxNumberOfTimePointsVariable();

  /**
   * Returns boolean variable deciding whether to limit the timelapse duration.
   * 
   * @return true -> timelapse duration limited
   */
  Variable<Boolean> getEnforceMaxDurationVariable();

  /**
   * Returns the timelapse date and time limit variable
   * 
   * @return timelapse date and time limit variable
   */
  Variable<Boolean> getEnforceMaxDateTimeVariable();

  /**
   * Returns the max number of time points variable
   * 
   * @return max number of time points variable
   */
  Variable<Long> getMaxNumberOfTimePointsVariable();

  /**
   * Returns the max duration variable
   * 
   * @return max duration variable
   */
  Variable<Long> getMaxDurationVariable();

  /**
   * Returns the max duration unit variable
   * 
   * @return max duration unit variable
   */
  Variable<TimeUnitEnum> getMaxDurationUnitVariable();

  /**
   * Returns the max date and time variable
   * 
   * @return max date and time variable
   */
  Variable<LocalDateTime> getMaxDateTimeVariable();

  /**
   * Returns the start date and time variable
   * 
   * @return start date and time variable
   */
  Variable<LocalDateTime> getStartDateTimeVariable();

  /**
   * Returns the time point counter variable.
   * 
   * @return time point counter variable
   */
  Variable<Long> getTimePointCounterVariable();

}
