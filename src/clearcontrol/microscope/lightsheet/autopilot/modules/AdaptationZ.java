package clearcontrol.microscope.lightsheet.autopilot.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import gnu.trove.list.array.TDoubleArrayList;

public class AdaptationZ extends NDIteratorAdaptationModule	implements
																														AdaptationModuleInterface
{

	private double mDeltaZ;

	public AdaptationZ(	double pDeltaZ,
											int pNumberOfSamples,
											double pProbabilityThreshold)
	{
		super(pNumberOfSamples, pProbabilityThreshold);
		mDeltaZ = pDeltaZ;
	}

	@Override
	public Future<?> atomicStep(int pControlPlaneIndex,
															int pLightSheetIndex,
															int pNumberOfSamples)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		LightSheetAcquisitionStateInterface lStackAcquisition = getAdaptator().getStackAcquisitionVariable().get();
		double lCurrentIZ = lLSM.getIZ(pLightSheetIndex);
		int lBestDetectionArm = getAdaptator().getStackAcquisitionVariable().get()
																					.getBestDetectionArm(pControlPlaneIndex);

		int lHalfSamples = (pNumberOfSamples - 1) / 2;
		double lMinZ = -mDeltaZ * lHalfSamples;
		double lMaxZ = mDeltaZ * lHalfSamples;

		final TDoubleArrayList lDZList = new TDoubleArrayList();

		lLSM.clearQueue();

		lStackAcquisition.applyStateAtControlPlane(pControlPlaneIndex);
		double lCurrentDZ = lLSM.getDZ(lBestDetectionArm);

		lLSM.setILO(false);
		lLSM.setC(false);
		lLSM.setDZ(lBestDetectionArm, lCurrentDZ + lMinZ);
		lLSM.addCurrentStateToQueue();
		lLSM.addCurrentStateToQueue();

		lLSM.setILO(true);
		lLSM.setC(true);
		for (double z = lMinZ; z <= lMaxZ; z += mDeltaZ)
		{
			lDZList.add(z);
			lLSM.setDZ(lBestDetectionArm, lCurrentDZ + z);
			lLSM.setI(pLightSheetIndex);
			lLSM.addCurrentStateToQueue();
		}

		lLSM.setILO(false);
		lLSM.setC(false);
		lLSM.setDZ(lBestDetectionArm, lCurrentDZ);
		lLSM.addCurrentStateToQueue();

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
														pLightSheetIndex,
														lLSM,
														lStackAcquisition,
														lDZList);

	}

	@Override
	public void updateNewState(	int pControlPlaneIndex,
															int pLightSheetIndex,
															ArrayList<Double> pArgMaxList)
	{
		int lBestDetectioArm = getAdaptator().getStackAcquisitionVariable().get()
																					.getBestDetectionArm(pControlPlaneIndex);

		double lCorrection = -pArgMaxList.get(lBestDetectioArm);

		getAdaptator().getNewAcquisitionState()
									.addAtControlPlaneIZ(	pControlPlaneIndex,
																				pLightSheetIndex,
																				lCorrection);
	}

}
