package rtlib.microscope.lsm.adaptation;

import java.util.ArrayList;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.AcquisitionState;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.adaptation.modules.AdaptationModuleInterface;

public class LSMAdaptator
{
	private final LightSheetMicroscope mLightSheetMicroscope;
	private final StackAcquisitionInterface mStackAcquisition;

	private ArrayList<AdaptationModuleInterface> mAdaptationModuleList = new ArrayList<>();
	private volatile int mCurrentAdaptationModule = 0;

	private volatile AcquisitionState mNewAcquisitionState;

	public LSMAdaptator(LightSheetMicroscope pLightSheetMicroscope,
						StackAcquisitionInterface pStackAcquisition)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;
		mStackAcquisition = pStackAcquisition;
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

	public boolean step()
	{
		boolean lModulesReady = checkIfAllModulesAreReady();

		if (lModulesReady)
		{
			clearModulesAreReady();
			getStackAcquisition().setCurrentAcquisitionState(getNewAcquisitionState());
			setNewAcquisitionState(new AcquisitionState(getStackAcquisition().getCurrentAcquisitionState()));

			return true;
		}

		AdaptationModuleInterface lAdaptationModule = mAdaptationModuleList.get(mCurrentAdaptationModule);
		int lPriority = lAdaptationModule.getPriority();

		for (int i = 0; i < lPriority; i++)
			lAdaptationModule.step();

		mCurrentAdaptationModule = (mCurrentAdaptationModule + 1) % mAdaptationModuleList.size();

		return false;
	}

	private boolean checkIfAllModulesAreReady()
	{
		boolean lAllReady = true;
		for (AdaptationModuleInterface lAdaptationModule : mAdaptationModuleList)
			lAllReady &= lAdaptationModule.isReady();

		return lAllReady;
	}

	private void clearModulesAreReady()
	{
		for (AdaptationModuleInterface lAdaptationModule : mAdaptationModuleList)
			lAdaptationModule.isReady();
	}

}
