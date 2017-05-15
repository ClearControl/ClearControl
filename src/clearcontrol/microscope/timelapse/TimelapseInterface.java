package clearcontrol.microscope.timelapse;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import clearcontrol.core.device.startstop.StartStopSignalVariablesInterface;
import clearcontrol.core.device.task.IsRunningTaskInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;
import clearcontrol.stack.sourcesink.sink.FileStackSinkInterface;

/**
 * Interface implemented by all timelapse devices.
 *
 * 
 * 
 * @author royer
 */
public interface TimelapseInterface extends
                                    StartStopSignalVariablesInterface,
                                    IsRunningTaskInterface

{

  /**
   * Starts timelapse
   */
  void startTimelapse();

  /**
   * Stops timelapse
   */
  void stopTimelapse();

  /**
   * Acquires a single timepoint
   */
  void acquire();

  /**
   * Adds a file stack sink type to use to save a time-lapse
   * 
   * @param pFileStackSinkType
   *          file stack sink type
   */
  void addFileStackSinkType(Class<?> pFileStackSinkType);

  /**
   * Returns file stack sink type list
   * 
   * @return file stack sink type list
   */
  ArrayList<Class<? extends FileStackSinkInterface>> getFileStackSinkTypeList();

  /**
   * Returns the variable that controls whether the adaptive engine should be
   * turned on.
   * 
   * @return adaptive-engine-on variable
   */
  Variable<Boolean> getAdaptiveEngineOnVariable();

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

  /**
   * Returns the variable holding the current file stack sink type.
   * 
   * @return current file stack sink type variable
   */
  Variable<Class<? extends FileStackSinkInterface>> getCurrentFileStackSinkTypeVariable();

  /**
   * Returns the variable holding the current file stack sink
   * 
   * @return current file stack sink
   */
  Variable<? extends FileStackSinkInterface> getCurrentFileStackSinkVariable();

  /**
   * Returns the variable holding the root folder
   * 
   * @return root folder variable
   */
  Variable<File> getRootFolderVariable();

  /**
   * Returns the variable holding the dataset name postfix.
   * 
   * @return dataset name postfix
   */
  Variable<String> getDataSetNamePostfixVariable();

  /**
   * Returns the variable holding the boolean flag that decides whether the
   * stacks should be saved or not.
   * 
   * @return save stacks variable
   */
  Variable<Boolean> getSaveStacksVariable();

}
