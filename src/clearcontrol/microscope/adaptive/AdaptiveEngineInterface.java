package clearcontrol.microscope.adaptive;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Interface implemented by all adaptive engines
 *
 * @author royer
 * 
 * @param <S>
 *          state type
 */
public interface AdaptiveEngineInterface<S extends AcquisitionStateInterface<?, ?>>
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
  MicroscopeInterface<?> getMicroscope();

  /**
   * Returns variable holding the acquisition state counter variable
   * 
   * @return acquisition state counter variable
   */
  Variable<Long> getAcquisitionStateCounterVariable();

  /**
   * Returns the concurrent-execution flag
   * 
   * @return concurrent-execution flag
   */
  Variable<Boolean> getConcurrentExecutionVariable();

  /**
   * Returns the variable that decides whether this adaptive engine should run
   * until all modules are ready.
   * 
   * @return execute-until-all-modules-ready variable
   */
  Variable<Boolean> getRunUntilAllModulesReadyVariable();

  /**
   * Returns current acquisition state variable
   * 
   * @return current acquisition state variable
   */
  Variable<S> getAcquisitionStateVariable();

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
