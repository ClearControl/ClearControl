package clearcontrol.microscope.lightsheet.adaptor;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.adaptor.modules.AdaptationModuleInterface;

/**
 * Interface implemented by all adaptators
 *
 * @author royer
 * @param <S>
 *          state type
 */
public interface AdaptatorInterface<S extends LightSheetAcquisitionStateInterface<S>>
{

  /**
   * Clears all modules and sets a single given adaptation module
   * 
   * @param pAdaptationModule
   *          adaptation module
   */
  void set(AdaptationModuleInterface<S> pAdaptationModule);

  /**
   * Adds a given adaptation module
   * 
   * @param pAdaptationModule
   *          adaptation module
   */
  void add(AdaptationModuleInterface<S> pAdaptationModule);

  /**
   * Removes a given adaptation module
   * 
   * @param pAdaptationModule
   *          adaptation module to remove
   */
  void remove(AdaptationModuleInterface<S> pAdaptationModule);

  /**
   * Returns the lst of modules addedto this adaptor
   * 
   * @return module list
   */
  ArrayList<AdaptationModuleInterface<S>> getModuleList();

  /**
   * Removes all modules
   * 
   */
  void clear();

  /**
   * Returns lightsheet microscope parent
   * 
   * @return parent
   */
  LightSheetMicroscope getLightSheetMicroscope();

  /**
   * Retuns the concurrent-execution flag
   * 
   * @return concurrent-execution flag
   */
  Variable<Boolean> getConcurrentExecutionVariable();

  /**
   * Returns the variable that decides whther this adaptator should run until
   * all modules are ready.
   * 
   * @return execute-until-all-modules-ready variable
   */
  Variable<Boolean> getRunUntilAllModulesReadyVariable();

  /**
   * Returns current acquisition state variable
   * 
   * @return current acquisition state variable
   */
  Variable<S> getCurrentAcquisitionStateVariable();

  /**
   * Returns new acquisition state variable
   * 
   * @return new acquisition state
   */
  Variable<S> getNewAcquisitionStateVariable();

  /**
   * Returns the current adaptation module variable
   * 
   * @return the current adaptation module variable
   */
  Variable<Double> getCurrentAdaptationModuleVariable();

  /**
   * Returns progress variable
   * 
   * @return progress variable
   */
  Variable<Double> getProgressVariable();

  /**
   * Estimates the duration of the next step in agiven time unit.
   * 
   * @return estimated time for next step
   */
  double estimateNextStepInSeconds();

  /**
   * Performs a single step
   * 
   * @return true -> there is a next-step, false otherwise
   */
  Boolean step();

  /**
   * Applies a given number of rounds. A round is a full sequence of adaptation
   * steps
   * 
   * @param pNumberOfRounds
   *          number of rounds
   * @param pWaitToFinish
   *          true -> waits for steps to finish
   * @return future
   */
  Future<?> steps(int pNumberOfRounds, boolean pWaitToFinish);

  /**
   * Resets
   */
  void reset();

}
