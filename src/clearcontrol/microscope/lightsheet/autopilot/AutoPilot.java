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
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;
import clearcontrol.microscope.lightsheet.autopilot.modules.AdaptationModuleInterface;

public class AutoPilot implements
											AutoPilotInterface,
											Function<Integer, Boolean>,
											AsynchronousExecutorServiceAccess
{
	private final LightSheetMicroscope mLightSheetMicroscope;
	private final Variable<LightSheetAcquisitionStateInterface> mStackAcquisitionVariable = new Variable<LightSheetAcquisitionStateInterface>("StackAcquisition",
																																																																						null);

	private ArrayList<AdaptationModuleInterface> mAdaptationModuleList = new ArrayList<>();
	private volatile double mCurrentAdaptationModule = 0;

	private volatile InterpolationTables mNewAcquisitionState;

	private volatile boolean mConcurrentExecution = false;

	private HashMap<Function<Void, Boolean>, Long> mTimmingMap = new HashMap<>();

	public AutoPilot(	LightSheetMicroscope pLightSheetMicroscope,
										LightSheetAcquisitionStateInterface pStackAcquisition)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;
		mStackAcquisitionVariable.set(pStackAcquisition);
		mNewAcquisitionState = new InterpolationTables(mStackAcquisitionVariable.get()
																																						.getCurrentState());

		double lCPULoadRatio = MachineConfiguration.getCurrentMachineConfiguration()
																								.getDoubleProperty(	"autopilot.cpuloadratio",
																																		0.2);

		int pMaxQueueLengthPerWorker = MachineConfiguration.getCurrentMachineConfiguration()
																												.getIntegerProperty("autopilot.worker.maxqueuelength",
																																						10);

		int lNumberOfWorkers = (int) max(	1,
																			(lCPULoadRatio * Runtime.getRuntime()
																															.availableProcessors()));

		ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
																												Thread.MIN_PRIORITY,
																												lNumberOfWorkers,
																												lNumberOfWorkers,
																												pMaxQueueLengthPerWorker * lNumberOfWorkers);
	}

	public LightSheetMicroscope getLightSheetMicroscope()
	{
		return mLightSheetMicroscope;
	}

	public Variable<LightSheetAcquisitionStateInterface> getStackAcquisitionVariable()
	{
		return mStackAcquisitionVariable;
	}

	public InterpolationTables getNewAcquisitionState()
	{
		return mNewAcquisitionState;
	}

	public void setNewAcquisitionState(InterpolationTables pNewAcquisitionState)
	{
		mNewAcquisitionState = pNewAcquisitionState;
	}

	public void set(AdaptationModuleInterface pAdaptationModule)
	{
		mAdaptationModuleList.clear();
		add(pAdaptationModule);
	}

	public void add(AdaptationModuleInterface pAdaptationModule)
	{
		mAdaptationModuleList.add(pAdaptationModule);
		pAdaptationModule.setAdaptator(this);
		pAdaptationModule.reset();
	}

	public void remove(AdaptationModuleInterface pAdaptationModule)
	{
		mAdaptationModuleList.remove(pAdaptationModule);
	}

	public long estimateStep(TimeUnit pTimeUnit)
	{
		boolean lModulesReady = isReady();
		if (lModulesReady)
			return 0;
		else
		{
			AdaptationModuleInterface lAdaptationModule = mAdaptationModuleList.get((int) mCurrentAdaptationModule);
			int lPriority = lAdaptationModule.getPriority();

			Long lMethodTimming = getTimming(lAdaptationModule::apply);

			if (lMethodTimming == null)
				return 0;

			long lEstimatedTimeInNanoseconds = lPriority * lMethodTimming;

			return lEstimatedTimeInNanoseconds;
		}
	}

	public void applyInitialRounds(int pNumberOfRounds)
	{
		for (int i = 0; i < pNumberOfRounds; i++)
		{
			System.out.format("Round: %d \n", i);
			while (apply(1))
				ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
		}
	}

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

		AdaptationModuleInterface lAdaptationModule = mAdaptationModuleList.get((int) mCurrentAdaptationModule);

		System.out.format("lAdaptationModule: %s \n", lAdaptationModule);

		double lStepSize = 1.0 / lAdaptationModule.getPriority();

		Boolean lHasNext = time(lAdaptationModule::apply);

		mCurrentAdaptationModule = (mCurrentAdaptationModule + lStepSize);

		if (mCurrentAdaptationModule >= mAdaptationModuleList.size())
			mCurrentAdaptationModule = mCurrentAdaptationModule - mAdaptationModuleList.size();

		boolean lModulesReady = isReady();

		System.out.format("lModulesReady: %s \n", lModulesReady);

		if (lModulesReady)
		{
			System.out.format("Modules all ready! \n");
			getStackAcquisitionVariable().get()
																		.setCurrentState(getNewAcquisitionState());
			setNewAcquisitionState(new InterpolationTables(getStackAcquisitionVariable().get()
																																									.getCurrentState()));
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

	private boolean time(Function<Void, Boolean> pMethod)
	{
		long lStartTimeNS = System.nanoTime();
		Boolean lResult = pMethod.apply(null);
		long lStopTimeNS = System.nanoTime();

		long lElapsedTimeInNS = lStopTimeNS - lStartTimeNS;
		double lElpasedTimeInMilliseconds = TimeUnit.MILLISECONDS.convert(lElapsedTimeInNS,
																																			TimeUnit.NANOSECONDS);

		mTimmingMap.put(pMethod, lElapsedTimeInNS);

		return lResult;
	}

	private Long getTimming(Function<Void, Boolean> pMethod)
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

	public boolean isConcurrentExecution()
	{
		return mConcurrentExecution;
	}

	public void setConcurrentExecution(boolean pConcurrentExecution)
	{
		mConcurrentExecution = pConcurrentExecution;
	}

}
