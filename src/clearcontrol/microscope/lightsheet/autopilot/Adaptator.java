package clearcontrol.microscope.lightsheet.autopilot;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.autopilot.modules.AdaptationModuleInterface;

/**
 * Autopilot
 *
 * @author royer
 */
public class Adaptator implements
                       Function<Integer, Boolean>,
                       AsynchronousExecutorServiceAccess
{
  private static final double cEpsilon = 0.8;
  private final LightSheetMicroscope mLightSheetMicroscope;
  private ArrayList<AdaptationModuleInterface> mAdaptationModuleList =
                                                                     new ArrayList<>();

  private final Variable<Long> mAcquisitionStateCounterVariable =
                                                                new Variable<>("AcquisitionStateCounter",
                                                                               0L);

  private final Variable<InterpolatedAcquisitionState> mCurrentAcquisitionStateVariable =
                                                                                        new Variable<>("CurrentAcquisitionState",
                                                                                                       null);

  private volatile InterpolatedAcquisitionState mNewAcquisitionState;

  private volatile double mCurrentAdaptationModule = 0;

  private volatile boolean mConcurrentExecution = false;

  private HashMap<AdaptationModuleInterface, Long> mTimmingMap =
                                                               new HashMap<>();

  /**
   * Instanciates a lightsheet microscope autopilot given a lightsheet
   * microscope
   * 
   * @param pLightSheetMicroscope
   *          lightsheet
   */
  public Adaptator(LightSheetMicroscope pLightSheetMicroscope)
  {
    super();
    mLightSheetMicroscope = pLightSheetMicroscope;

    double lCPULoadRatio =
                         MachineConfiguration.getCurrentMachineConfiguration()
                                             .getDoubleProperty("autopilot.cpuloadratio",
                                                                0.2);

    int pMaxQueueLengthPerWorker =
                                 MachineConfiguration.getCurrentMachineConfiguration()
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
  }

  /**
   * Returns lightsheet microscope parent
   * 
   * @return parent
   */
  public LightSheetMicroscope getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }

  /**
   * Sets current acquisition state
   * 
   * @param pState
   *          current acquisition state
   */
  public void setCurrentAcquisitionState(InterpolatedAcquisitionState pState)
  {
    getCurrentAcquisitionStateVariable().set(pState);
  }

  /**
   * Returns stack acquisition variable
   * 
   * @return stack acquisition variable
   */
  public Variable<InterpolatedAcquisitionState> getCurrentAcquisitionStateVariable()
  {
    return mCurrentAcquisitionStateVariable;
  }

  /**
   * Returns new acquisition state
   * 
   * @return new acquisition state
   */
  public InterpolatedAcquisitionState getNewAcquisitionState()
  {
    return mNewAcquisitionState;
  }

  /**
   * Sets the new acquisition state
   * 
   * @param pNewAcquisitionState
   *          new acquisition state
   */
  public void setNewAcquisitionState(InterpolatedAcquisitionState pNewAcquisitionState)
  {
    mNewAcquisitionState = pNewAcquisitionState;
  }

  /**
   * Clears all modules.
   * 
   */
  public void clear()
  {
    mAdaptationModuleList.clear();
  }

  /**
   * Clears all modules and sets a single given adaptation module
   * 
   * @param pAdaptationModule
   *          adaptation module
   */
  public void set(AdaptationModuleInterface pAdaptationModule)
  {
    clear();
    add(pAdaptationModule);
  }

  /**
   * Adds a given adaptation module
   * 
   * @param pAdaptationModule
   *          adaptation module
   */
  public void add(AdaptationModuleInterface pAdaptationModule)
  {
    mAdaptationModuleList.add(pAdaptationModule);
    pAdaptationModule.setAdaptator(this);
    pAdaptationModule.reset();
  }

  /**
   * Removes a given adaptation module
   * 
   * @param pAdaptationModule
   *          adaptation module to remove
   */
  public void remove(AdaptationModuleInterface pAdaptationModule)
  {
    mAdaptationModuleList.remove(pAdaptationModule);
  }

  /**
   * Estimates the duration of the next step in agiven time unit.
   * 
   * @return estimated time for next step
   */
  public double estimateStepInSeconds()
  {
    boolean lModulesReady = isReady();
    if (lModulesReady)
      return 0;
    else
    {
      AdaptationModuleInterface lAdaptationModule =
                                                  mAdaptationModuleList.get((int) mCurrentAdaptationModule);
      int lPriority = lAdaptationModule.getPriority();

      Long lMethodTimming = getTimmingInNs(lAdaptationModule);

      if (lMethodTimming == null)
        return 0;

      long lEstimatedTimeInNanoseconds = lPriority * lMethodTimming;

      double lEstimatedTimeInSeconds = 1e-9
                                       * lEstimatedTimeInNanoseconds;

      return lEstimatedTimeInSeconds;
    }
  }

  /**
   * Applies a given number of rounds.
   * 
   * @param pNumberOfRounds
   *          number of rounds
   */
  public void applyInitialRounds(int pNumberOfRounds)
  {
    for (int i = 0; i < pNumberOfRounds; i++)
    {
      System.out.format("Round: %d \n", i);
      while (apply(1))
        ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Performs a single step
   * 
   * @return true -> there is a next-step, false otherwise
   */
  public Boolean step()
  {
    return apply(1);
  }

  @Override
  public Boolean apply(Integer pTimes)
  {
    if (pTimes <= 0 || mAdaptationModuleList.size() == 0)
      return false;

    System.out.format("Adaptator: step \n");

    AdaptationModuleInterface lAdaptationModule =
                                                mAdaptationModuleList.get((int) mCurrentAdaptationModule);

    System.out.format("lAdaptationModule: %s \n", lAdaptationModule);

    double lStepSize = 1.0 / lAdaptationModule.getPriority();

    Boolean lHasNext = time(lAdaptationModule,
                            lAdaptationModule::apply);

    mCurrentAdaptationModule = (mCurrentAdaptationModule + lStepSize);

    if (mCurrentAdaptationModule >= mAdaptationModuleList.size())
      mCurrentAdaptationModule = mCurrentAdaptationModule
                                 - mAdaptationModuleList.size();

    boolean lModulesReady = isReady();

    System.out.format("lModulesReady: %s \n", lModulesReady);

    if (lModulesReady)
    {
      System.out.format("Modules all ready! \n");
      getCurrentAcquisitionStateVariable().set(getNewAcquisitionState());
      mAcquisitionStateCounterVariable.increment();
      mNewAcquisitionState =
                           new InterpolatedAcquisitionState("state"
                                                            + mAcquisitionStateCounterVariable.get(),
                                                            getCurrentAcquisitionStateVariable().get());
      reset();
      return false;
    }
    else if (pTimes - 1 >= 1)
    {
      System.out.format("Modules are not yet ready, applying %d more time \n",
                        (pTimes - 1));
      return apply(pTimes - 1);
    }
    else
      return lHasNext;
  }

  private boolean time(AdaptationModuleInterface pAdaptationModule,
                       Function<Void, Boolean> pMethod)
  {
    long lStartTimeNS = System.nanoTime();
    Boolean lResult = pMethod.apply(null);
    long lStopTimeNS = System.nanoTime();

    long lElapsedTimeInNS = lStopTimeNS - lStartTimeNS;
    double lElpasedTimeInMilliseconds = lElapsedTimeInNS * 1e-6;
    System.out.format("elapsed time: %g ms \n",
                      lElpasedTimeInMilliseconds);

    Long lCurrentEstimate = mTimmingMap.get(pAdaptationModule);

    if (lCurrentEstimate != null)
    {
      lElapsedTimeInNS = (long)((1 - cEpsilon) * lCurrentEstimate
          + cEpsilon * lElapsedTimeInNS);
    }

    mTimmingMap.put(pAdaptationModule, lElapsedTimeInNS);

    return lResult;
  }

  private Long getTimmingInNs(AdaptationModuleInterface pMethod)
  {
    return mTimmingMap.get(pMethod);
  }

  private boolean isReady()
  {
    boolean lAllReady = true;
    for (AdaptationModuleInterface lAdaptationModule : mAdaptationModuleList)
      lAllReady &= lAdaptationModule.isReady();

    return lAllReady;
  }

  private void reset()
  {
    mCurrentAdaptationModule = 0;
    for (AdaptationModuleInterface lAdaptationModule : mAdaptationModuleList)
      lAdaptationModule.reset();
  }

  /**
   * Retuns the concurrent-execution flag
   * 
   * @return concurrent-execution flag
   */
  public boolean isConcurrentExecution()
  {
    return mConcurrentExecution;
  }

  /**
   * Sets the concurrent-execution flag
   * 
   * @param pConcurrentExecution
   *          concurrent execution flag
   */
  public void setConcurrentExecution(boolean pConcurrentExecution)
  {
    mConcurrentExecution = pConcurrentExecution;
  }

}
