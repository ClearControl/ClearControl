package rtlib.microscope.lsm.adaptation;

import java.util.ArrayList;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.AcquisitionState;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.adaptation.modules.AdaptationModuleInterface;

public class Adaptator
{
	private final LightSheetMicroscope mLightSheetMicroscope;
	private final StackAcquisitionInterface mStackAcquisition;

	private ArrayList<AdaptationModuleInterface> mAdaptationModuleList = new ArrayList<>();
	private volatile int mCurrentAdaptationModule = 0;

	private volatile AcquisitionState mNewAcquisitionState;

	public Adaptator(LightSheetMicroscope pLightSheetMicroscope,
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

	public boolean step()
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
			AdaptationModuleInterface lAdaptationModule = mAdaptationModuleList.get(mCurrentAdaptationModule);

			System.out.format("lAdaptationModule: %s \n", lAdaptationModule);

			int lPriority = lAdaptationModule.getPriority();

			for (int i = 0; i < lPriority && lAdaptationModule.step(); i++)
			;//do not remove this semi-colon!

			mCurrentAdaptationModule = (mCurrentAdaptationModule + 1) % mAdaptationModuleList.size();

			return true;
		}
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
