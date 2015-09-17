package rtlib.microscope.lsm.adaptation.modules;

import java.util.concurrent.Future;

import gnu.trove.list.array.TDoubleArrayList;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AdaptationX extends NDIteratorAdaptationModule	implements
															AdaptationModuleInterface
{

	public AdaptationX(	int pNumberOfSamples,
						double pProbabilityThreshold)
	{
		super(pNumberOfSamples, pProbabilityThreshold);
	}

	public Future<?> atomicStep(int pControlPlaneIndex,
								int pLightSheetIndex,
								int pNumberOfSamples)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		LightSheetInterface lLightSheetDevice = lLSM.getDeviceLists()
													.getLightSheetDevice(pLightSheetIndex);
		double lMinX = lLightSheetDevice.getXFunction()
										.get()
										.getMin();
		double lMaxX = lLightSheetDevice.getXFunction()
										.get()
										.getMax();
		double lStepX = (lMaxX - lMinX) / pNumberOfSamples;

		lLSM.clearQueue();

		lStackAcquisition.setToControlPlane(pControlPlaneIndex);

		final TDoubleArrayList lIXList = new TDoubleArrayList();

		lLSM.setC(false);
		lLSM.setIX(pLightSheetIndex, lMinX);
		lLSM.addCurrentStateToQueue();

		lLSM.setC(true);
		for (double x = lMinX; x <= lMaxX; x += lStepX)
		{
			lIXList.add(x);
			lLSM.setIX(pLightSheetIndex, x);
			lLSM.addCurrentStateToQueue();
		}

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
								pLightSheetIndex,
								lLSM,
								lStackAcquisition,
								lIXList);
	}

	public void updateNewState(	int pControlPlaneIndex,
								int pLightSheetIndex,
								Double lArgmax)
	{
		getAdaptator().getNewAcquisitionState()
						.setAtControlPlaneIX(	pControlPlaneIndex,
												pLightSheetIndex,
												lArgmax);
	}

}
