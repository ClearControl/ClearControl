package rtlib.microscope.lsm.adaptation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.AcquisitionState;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.adaptation.modules.AdaptationModuleInterface;

public class Adaptator implements Function<Void, Boolean>
{
	private final LightSheetMicroscope mLightSheetMicroscope;
	private final StackAcquisitionInterface mStackAcquisition;

	private ArrayList<AdaptationModuleInterface> mAdaptationModuleList = new ArrayList<>();
	private volatile double mCurrentAdaptationModule = 0;

	private volatile AcquisitionState mNewAcquisitionState;

	private HashMap<Function<Void, Boolean>, Long> mTimmingMap = new HashMap<>();

	public Adaptator(	LightSheetMicroscope pLightSheetMicroscope,
						StackAcquisitionInterface pStackAcquisition)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;
		mStackAcquisition = pStackAcquisition;
		mNewAcquisitionState = new AcquisitionState(mStackAcquisition.getCurrentState());
	}

	public LightSheetMicroscope getLightSheetMicroscope()
	{
		return mLightSheetMicroscope;
	}

	public StackAcquisitionInterface getStackAcquisition()
	{
		return mStackAcquisition;
	}

	public AcquisitionState getNewAcquisitionState()
	{
		return mNewAcquisitionState;
	}

	public void setNewAcquisitionState(AcquisitionState pNewAcquisitionState)
	{
		mNewAcquisitionState = pNewAcquisitionState;
	}

	public void add(AdaptationModuleInterface pAdaptationModule)
	{
		mAdaptationModuleList.add(pAdaptationModule);
		pAdaptationModule.setAdaptator(this);
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
			while (apply(null))
				ThreadUtils.sleep(500, TimeUnit.MILLISECONDS);
	}

	public Boolean step()
	{
		return apply(null);
	}

	public Boolean apply(Void pVoid)
	{
		System.out.format("Adaptator: step \n");

		boolean lModulesReady = isReady();

		System.out.format("lModulesReady: %s \n", lModulesReady);

		if (lModulesReady)
		{
			System.out.format("Modules all ready! \n");
			getStackAcquisition().setCurrentState(getNewAcquisitionState());
			setNewAcquisitionState(new AcquisitionState(getStackAcquisition().getCurrentState()));
			reset();
			return false;
		}
		else
		{
			AdaptationModuleInterface lAdaptationModule = mAdaptationModuleList.get((int) mCurrentAdaptationModule);

			System.out.format(	"lAdaptationModule: %s \n",
								lAdaptationModule);

			double lStepSize = 1.0 / lAdaptationModule.getPriority();

			time(lAdaptationModule::apply);

			mCurrentAdaptationModule = (mCurrentAdaptationModule + lStepSize);

			if (mCurrentAdaptationModule >= mAdaptationModuleList.size())
				mCurrentAdaptationModule = mCurrentAdaptationModule - mAdaptationModuleList.size();

			return true;
		}
	}

	private boolean time(Function<Void, Boolean> pMethod)
	{
		long lStartTimeNS = System.nanoTime();
		Boolean lResult = pMethod.apply(null);
		long lStopTimeNS = System.nanoTime();

		long lElapsedTimeInNS = lStopTimeNS - lStartTimeNS;
		double lElpasedTimeInMilliseconds = (double) TimeUnit.MILLISECONDS.convert(	lElapsedTimeInNS,
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

}
