package rtlib.microscope.lsm.adaptation.modules;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.Future;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AdaptationW extends NDIteratorAdaptationModule implements
																														AdaptationModuleInterface
{

	public AdaptationW(	int pNumberOfSamples,
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
		double lMinW = lLightSheetDevice.getWidthFunction()
																		.get()
																		.getMin();
		double lMaxW = lLightSheetDevice.getWidthFunction()
																		.get()
																		.getMax();
		double lStepW = (lMaxW - lMinW) / pNumberOfSamples;

		lLSM.clearQueue();

		lStackAcquisition.setToControlPlane(pControlPlaneIndex);

		final TDoubleArrayList lIWList = new TDoubleArrayList();

		lLSM.setC(false);
		lLSM.setIW(pLightSheetIndex, lMinW);
		lLSM.addCurrentStateToQueue();

		lLSM.setC(true);
		for (double w = lMinW; w <= lMaxW; w += lStepW)
		{
			lIWList.add(w);
			lLSM.setIW(pLightSheetIndex, w);
			lLSM.addCurrentStateToQueue();
		}

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
											pLightSheetIndex,
											lLSM,
											lStackAcquisition,
											lIWList);

	}

	public void updateNewState(	int pControlPlaneIndex,
															int pLightSheetIndex,
															Double lArgmax)
	{
		getAdaptator().getNewAcquisitionState()
									.setAtControlPlaneIW(	pControlPlaneIndex,
																				pLightSheetIndex,
																				lArgmax);
	}

}
