package rtlib.microscope.lsm.adaptation.modules;

import java.util.concurrent.Future;

import gnu.trove.list.array.TDoubleArrayList;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AdaptationZ extends NDIteratorAdaptationModule	implements
														AdaptationModuleInterface
{

	public AdaptationZ(	int pNumberOfSamples,
						double pProbabilityThreshold)
	{
		super(pNumberOfSamples,pProbabilityThreshold);
	}

	public Future<?> atomicStep(	int pControlPlaneIndex,
									int pLightSheetIndex,
									int pNumberOfSamples)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		LightSheetInterface lLightSheetDevice = lLSM.getDeviceLists()
													.getLightSheetDevice(pLightSheetIndex);
		double lMinIZ = lLightSheetDevice.getWidthFunction()
										.get()
										.getMin();
		double lMaxIZ = lLightSheetDevice.getWidthFunction()
										.get()
										.getMax();
		double lStepIZ = (lMaxIZ - lMinIZ) / pNumberOfSamples;

		lLSM.clearQueue();

		lStackAcquisition.setToControlPlane(pControlPlaneIndex);

		final TDoubleArrayList lIZList = new TDoubleArrayList();

		lLSM.setC(false);
		lLSM.setIZ(pLightSheetIndex, lMinIZ);
		lLSM.addCurrentStateToQueue();

		lLSM.setC(true);
		for (double z = lMinIZ; z <= lMaxIZ; z += lStepIZ)
		{
			lLSM.setIZ(pLightSheetIndex, z);
			lLSM.addCurrentStateToQueue();
		}

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
								pLightSheetIndex,
								lLSM,
								lStackAcquisition,
								lIZList);
	}

	public void updateNewState(int pControlPlaneIndex,
								int pLightSheetIndex,
								Double lArgmax)
	{
		getAdaptator().getNewAcquisitionState()
						.setAtControlPlaneIZ(	pControlPlaneIndex,
												pLightSheetIndex,
												lArgmax);
	}


}
