package rtlib.microscope.lsm.adaptation.modules;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.concurrent.Future;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AdaptationZ2 extends NDIteratorAdaptationModule implements
															AdaptationModuleInterface
{

	private double mDeltaZ;

	public AdaptationZ2(double pDeltaZ,
						int pNumberOfSamples,
						double pProbabilityThreshold)
	{
		super(pNumberOfSamples, pProbabilityThreshold);
		mDeltaZ = pDeltaZ;
	}

	public Future<?> atomicStep(int pControlPlaneIndex,
								int pLightSheetIndex,
								int pNumberOfSamples)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();
		double lCurrentIZ = lLSM.getIZ(pLightSheetIndex);
		double lCurrentDZ = lLSM.getDZ(pLightSheetIndex);

		int lHalfSamples = (pNumberOfSamples-1)/2;
		double lMinZ = lCurrentIZ+mDeltaZ*lHalfSamples;
		double lMaxZ = mDeltaZ*lHalfSamples;
		
		final TDoubleArrayList lIZList = new TDoubleArrayList();
		
		lLSM.clearQueue();

		lStackAcquisition.setToControlPlane(pControlPlaneIndex);

		lLSM.setC(false);
		lLSM.setDZ(pLightSheetIndex, lMinZ);
		lLSM.addCurrentStateToQueue();
		lLSM.addCurrentStateToQueue();

		lLSM.setC(true);
		for (double z = lMinZ; z <= lMaxZ; z += mDeltaZ)
		{
			lIZList.add(z);
			lLSM.setDZ(pLightSheetIndex, z);
			lLSM.addCurrentStateToQueue();
		}

		lLSM.setC(false);
		lLSM.setDZ(pLightSheetIndex, lCurrentDZ);
		lLSM.addCurrentStateToQueue();

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
								pLightSheetIndex,
								lLSM,
								lStackAcquisition,
								lIZList);

	}

	public void updateNewState(	int pControlPlaneIndex,
								int pLightSheetIndex,
								ArrayList<Double> pArgMaxList)
	{
		int lBestDetectioArm = getAdaptator().getStackAcquisition()
												.getBestDetectioArm(pControlPlaneIndex);

		getAdaptator().getNewAcquisitionState()
						.addAtControlPlaneIZ(	pControlPlaneIndex,
												pLightSheetIndex,
												-pArgMaxList.get(lBestDetectioArm));
	}

}
