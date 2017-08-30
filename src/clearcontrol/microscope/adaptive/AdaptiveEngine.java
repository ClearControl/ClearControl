package clearcontrol.microscope.adaptive;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.task.TaskDevice;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;

/**
 * adaptive engine
 *
 * @author royer
 * 
 * @param <S>
 *          state type
 */
public class AdaptiveEngine<S extends AcquisitionStateInterface<?, ?>>
                           extends TaskDevice implements
                           Function<Integer, Boolean>,
                           AdaptiveEngineInterface<S>,
                           LoggingFeature,
                           AsynchronousExecutorFeature,
                           VisualConsoleInterface
{
  private static final double cEpsilon = 0.8;

  private final MicroscopeInterface<?> mMicroscope;
  private ArrayList<AdaptationModuleInterface<S>> mAdaptationModuleList =
                                                                        new ArrayList<>();

  private HashMap<AdaptationModuleInterface<S>, Long> mTimmingMap =
                                                                  new HashMap<>();

  private final Variable<Long> mAcquisitionStateCounterVariable =
                                                                new Variable<>("AcquisitionStateCounter",
                                                                               0L);

  private final Variable<S> mAcquisitionStateVariable =
                                                      new Variable<>("CurrentAcquisitionState",
                                                                     null);

  private final Variable<Double> mCurrentAdaptationModuleVariable =
                                                                  new Variable<>("CurrentAdaptationModule",
                                                                                 0.0);

  private final Variable<Double> mProgressVariable =
                                                   new Variable<Double>("Progress",
                                                                        0.0);

  private final Variable<Boolean> mConcurrentExecutionVariable =
                                                               new Variable<>("ConcurrentExecution",
                                                                              true);

  private final Variable<Boolean> mRunUntilAllModulesReadyVariable =
                                                                   new Variable<>("ConcurrentExecution",
                                                                                  false);

  /**
   * Instantiates an adaptive engine given a parent microscope
   * 
   * @param pMicroscope
   *          parent microscope
   * @param pAcquisitionState
   *          acquisition state
   */
  public AdaptiveEngine(MicroscopeInterface<?> pMicroscope,
                        S pAcquisitionState)
  {
    super("Adaptive");
    mMicroscope = pMicroscope;

    double lCPULoadRatio =
                         MachineConfiguration.get()
                                             .getDoubleProperty("autopilot.cpuloadratio",
                                                                0.2);

    int pMaxQueueLengthPerWorker =
                                 MachineConfiguration.get()
                                                     .getIntegerProperty("autopilot.worker.maxqueuelength",
                                                                         10);

    int lNumberOfWorkers =
                         (int) max(1,
                                   (lCPULoadRatio
                                    * Runtime.getRuntime()
                                             .availableProcessors()));

    ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                        Thread.MIN_PRIORITY,
                                                        lNumberOfWorkers,
                                                        lNumberOfWorkers,
                                                        pMaxQueueLengthPerWorker
                                                                          * lNumberOfWorkers);

    getAcquisitionStateVariable().set(pAcquisitionState);
    getAcquisitionStateCounterVariable().set(0L);
    reset();

  }

  @Override
  public MicroscopeInterface<?> getMicroscope()
  {
    return mMicroscope;
  }

  @Override
  public Variable<Long> getAcquisitionStateCounterVariable()
  {
    return mAcquisitionStateCounterVariable;
  }

  @Override
  public Variable<Boolean> getConcurrentExecutionVariable()
  {
    return mConcurrentExecutionVariable;
  }

  @Override
  public Variable<S> getAcquisitionStateVariable()
  {
    return mAcquisitionStateVariable;
  }

  @Override
  public Variable<Double> getCurrentAdaptationModuleVariable()
  {
    return mCurrentAdaptationModuleVariable;
  }

  @Override
  public Variable<Boolean> getRunUntilAllModulesReadyVariable()
  {
    return mRunUntilAllModulesReadyVariable;
  }

  @Override
  public Variable<Double> getProgressVariable()
  {
    return mProgressVariable;
  }

  @Override
  public void clear()
  {
    mAdaptationModuleList.clear();
  }

  @Override
  public void set(AdaptationModuleInterface<S> pAdaptationModule)
  {
    clear();
    add(pAdaptationModule);
  }

  @Override
  public void add(AdaptationModuleInterface<S> pAdaptationModule)
  {
    mAdaptationModuleList.add(pAdaptationModule);
    pAdaptationModule.setAdaptator(this);
    pAdaptationModule.reset();
  }

  @Override
  public void remove(AdaptationModuleInterface<S> pAdaptationModule)
  {
    mAdaptationModuleList.remove(pAdaptationModule);
  }

  @Override
  public ArrayList<AdaptationModuleInterface<S>> getModuleList()
  {
    return new ArrayList<>(mAdaptationModuleList);
  }

  @Override
  public double estimateNextStepInSeconds()
  {
    boolean lModulesReady = isReady();
    if (lModulesReady)
      return 0;
    else
    {
      AdaptationModuleInterface<S> lAdaptationModule =
                                                     mAdaptationModuleList.get(getCurrentAdaptationModuleVariable().get()
                                                                                                                   .intValue());
      int lPriority = lAdaptationModule.getPriority();

      Long lMethodTimming =
                          getModuleEstimatedStepTimeInNanoseconds(lAdaptationModule);

      if (lMethodTimming == null)
        return 0;

      long lEstimatedTimeInNanoseconds = lPriority * lMethodTimming;

      double lEstimatedTimeInSeconds = 1e-9
                                       * lEstimatedTimeInNanoseconds;

      return lEstimatedTimeInSeconds;
    }
  }

  @Override
  public void reset()
  {
    info("Reset");
    clearTask();
    getProgressVariable().set(0.0);
    getCurrentAdaptationModuleVariable().set(0.0);
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      lAdaptationModule.reset();

  }

  /**
   * Prepares a new acquisition state
   */
  public void logCurrentAcquisitionState()
  {
    if (getMicroscope() != null)
    {

      @SuppressWarnings("unchecked")
      AcquisitionStateManager<S> lAcquisitionStateManager =
                                                          (AcquisitionStateManager<S>) getMicroscope().getAcquisitionStateManager();

      S lCurrentAcquisitionState =
                                 (S) getAcquisitionStateVariable().get();

      @SuppressWarnings("unchecked")
      S lLoggedState =
                     (S) lCurrentAcquisitionState.duplicate("state "
                                                            + getAcquisitionStateCounterVariable().get());

      lAcquisitionStateManager.addState(lLoggedState);
    }
  }

  @Override
  public void run()
  {
    info("begin run");
    try
    {
      if (getRunUntilAllModulesReadyVariable().get())
      {
        while (step())
          if (getStopSignalVariable().get())
          {
            reset();
            break;
          }
      }
      else
        step();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    info("end run");
  }

  @Override
  public Future<?> steps(int pNumberOfRounds, boolean pWaitToFinish)
  {
    Runnable lRunnable = () -> {
      for (int i = 0; i < pNumberOfRounds; i++)
      {
        info("round: %d \n", i);
        while (apply(1))
          ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);
      }
    };

    if (pWaitToFinish)
    {
      lRunnable.run();
      return executeAsynchronously(() -> {
      });
    }
    else
      return executeAsynchronously(lRunnable);

  }

  @Override
  public Boolean step()
  {
    return apply(1);
  }

  @Override
  public Boolean apply(Integer pTimes)
  {
    info("begin step \n");

    int lTotalRemainingNumberOfSteps =
                                     getTotalRemainingNumberOfSteps();

    info("total remaining steps: %d \n",
         lTotalRemainingNumberOfSteps);

    if (lTotalRemainingNumberOfSteps == 0)
      return false;

    if (pTimes <= 0 || mAdaptationModuleList.size() == 0)
    {
      info("no more steps to apply \n");
      return false;
    }

    AdaptationModuleInterface<S> lAdaptationModule =
                                                   getCurrentModule();

    if (!lAdaptationModule.isActive())
      while (!(lAdaptationModule =
                                 incrementCurrentModule(1)).isActive())
      {
        info("Skipping module: %s \n" + lAdaptationModule);
      }

    int lAdaptationModuleIndex =
                               getCurrentAdaptationModuleVariable().get()
                                                                   .intValue();

    boolean lModuleWasReady = lAdaptationModule.isReady();

    info("Adaptation module: [%d] -> %s (is ready before apply: %s) \n",
         lAdaptationModuleIndex,
         lAdaptationModule,
         lModuleWasReady);

    double lStepSize = 1.0 / lAdaptationModule.getPriority();

    info("Applying: [%d] -> %s \n",
         lAdaptationModuleIndex,
         lAdaptationModule);
    time(lAdaptationModule, lAdaptationModule::apply);

    incrementCurrentModule(lStepSize);

    boolean lAreAllStepsCompleted = areAllStepsCompleted();

    info("Are all steps for all modules completed? %s \n",
         lAreAllStepsCompleted);

    if (lModuleWasReady && !lAreAllStepsCompleted)
    {
      info("this module was ready but some other is not \n");

      info("end step with recursive call\n");
      return apply(pTimes);
    }

    double lProgress =
                     1.0 - ((double) getTotalRemainingNumberOfSteps())
                           / getTotalNumberOfSteps();
    getProgressVariable().set(lProgress);

    if (lAreAllStepsCompleted)
    {
      info("All steps completed! \n");
      info("waiting for tasks to complete... \n");
      waitForAllTasksToComplete();

      logCurrentAcquisitionState();
      updateState(getAcquisitionStateVariable().get());
      getAcquisitionStateCounterVariable().increment();

      // prepareNewAcquisitionState();
      // reset();
      info("end step with false\n");
      return false;
    }
    else if (pTimes - 1 >= 1)
    {
      info("Modules are not yet ready, applying %d more time \n",
           (pTimes - 1));

      info("end step with recursive call\n");
      return apply(pTimes - 1);
    }
    else
    {
      info("end step with true... \n");
      return true;
    }
  }

  private void updateState(S pStateToUpdate)
  {
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      if (lAdaptationModule.isActive())
        lAdaptationModule.updateState(pStateToUpdate);
  }

  private AdaptationModuleInterface<S> getCurrentModule()
  {
    int lAdaptationModuleIndex =
                               getCurrentAdaptationModuleVariable().get()
                                                                   .intValue();
    AdaptationModuleInterface<S> lAdaptationModule =
                                                   mAdaptationModuleList.get(lAdaptationModuleIndex);

    return lAdaptationModule;
  }

  protected AdaptationModuleInterface<S> incrementCurrentModule(double lStepSize)
  {
    double lCurrentModuleIndex =
                               getCurrentAdaptationModuleVariable().get();

    lCurrentModuleIndex = (lCurrentModuleIndex + lStepSize);

    if (lCurrentModuleIndex >= mAdaptationModuleList.size())
      lCurrentModuleIndex = lCurrentModuleIndex
                            - mAdaptationModuleList.size();

    getCurrentAdaptationModuleVariable().set(lCurrentModuleIndex);

    AdaptationModuleInterface<S> lAdaptationModule =
                                                   mAdaptationModuleList.get((int) lCurrentModuleIndex);

    return lAdaptationModule;
  }

  private boolean time(AdaptationModuleInterface<S> pAdaptationModule,
                       Function<Void, Boolean> pMethod)
  {
    long lStartTimeNS = System.nanoTime();
    Boolean lResult = pMethod.apply(null);
    long lStopTimeNS = System.nanoTime();

    long lElapsedTimeInNS = lStopTimeNS - lStartTimeNS;
    double lElpasedTimeInMilliseconds = lElapsedTimeInNS * 1e-6;
    info("elapsed time: %g ms \n", lElpasedTimeInMilliseconds);

    Long lCurrentEstimate = mTimmingMap.get(pAdaptationModule);

    if (lCurrentEstimate != null)
    {
      lElapsedTimeInNS = (long) ((1 - cEpsilon) * lCurrentEstimate
                                 + cEpsilon * lElapsedTimeInNS);
    }

    mTimmingMap.put(pAdaptationModule, lElapsedTimeInNS);

    return lResult;
  }

  @SuppressWarnings("rawtypes")
  private Long getModuleEstimatedStepTimeInNanoseconds(AdaptationModuleInterface pModule)
  {
    return mTimmingMap.get(pModule);
  }

  /**
   * Returns estimated step execution time for the given module in the given
   * time unit.time
   * 
   * @param pModule
   *          module
   * @param pTimeUnit
   *          time unit
   * @return estimated time
   */
  @SuppressWarnings("rawtypes")
  public Long getEstimatedModuleStepTime(AdaptationModuleInterface pModule,
                                         TimeUnit pTimeUnit)
  {
    Long lModuleTimmimgInNs =
                            getModuleEstimatedStepTimeInNanoseconds(pModule);
    if (lModuleTimmimgInNs == null)
      return null;
    return pTimeUnit.convert(lModuleTimmimgInNs.longValue(),
                             TimeUnit.NANOSECONDS);
  }

  private boolean isReady()
  {
    boolean lAllReady = true;
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      if (lAdaptationModule.isActive())
        lAllReady &= lAdaptationModule.isReady();

    return lAllReady;
  }

  private boolean areAllTasksCompleted()
  {
    boolean lCompleted = true;
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      if (lAdaptationModule.isActive())
        lCompleted &= lAdaptationModule.areAllTasksCompleted();

    return lCompleted;
  }

  private void waitForAllTasksToComplete()
  {
    WaitingInterface.waitForStatic(() -> areAllTasksCompleted());
  }

  private boolean areAllStepsCompleted()
  {
    boolean lCompleted = true;
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      if (lAdaptationModule.isActive())
        lCompleted &= lAdaptationModule.areAllStepsCompleted();

    return lCompleted;
  }

  private int getTotalNumberOfSteps()
  {
    int lTotalNumberOfSteps = 0;
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      if (lAdaptationModule.isActive())
        lTotalNumberOfSteps += lAdaptationModule.getNumberOfSteps();
    return lTotalNumberOfSteps;
  }

  private int getTotalRemainingNumberOfSteps()
  {
    int lTotalRemainingNumberOfSteps = 0;
    for (AdaptationModuleInterface<S> lAdaptationModule : mAdaptationModuleList)
      if (lAdaptationModule.isActive())
        if (!lAdaptationModule.areAllStepsCompleted())
          lTotalRemainingNumberOfSteps +=
                                       1 + lAdaptationModule.getRemainingNumberOfSteps();
    return lTotalRemainingNumberOfSteps;
  }

}
