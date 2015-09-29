package rtlib.microscope.lsm.adaptation.modules;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.concurrent.Future;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AdaptationA extends NDIteratorAdaptationModule	implements
															AdaptationModuleInterface
{

	private static final int cMaxAngle = 10;

	public AdaptationA(	int pNumberOfSamples,
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
		double lMinA = -cMaxAngle;
		double lMaxA = +cMaxAngle;
		double lStepA = (lMaxA - lMinA) / (pNumberOfSamples - 1);

		lLSM.clearQueue();

		lStackAcquisition.setToControlPlane(pControlPlaneIndex);

		final TDoubleArrayList lIAList = new TDoubleArrayList();

		double lCurrentA = lLSM.getIA(pLightSheetIndex);

		lLSM.setC(false);
		lLSM.setILO(false);
		lLSM.setIA(pLightSheetIndex, lMinA);
		lLSM.addCurrentStateToQueue();
		lLSM.addCurrentStateToQueue();
		lLSM.addCurrentStateToQueue();

		lLSM.setC(true);
		for (double a = lMinA; a <= lMaxA; a += lStepA)
		{
			lIAList.add(a);
			lLSM.setIA(pLightSheetIndex, a);

			lLSM.setILO(false);
			lLSM.setC(false);
			for (int r = 0; r < 10; r++)
				lLSM.addCurrentStateToQueue();

			lLSM.setILO(true);
			lLSM.setC(true);
			lLSM.addCurrentStateToQueue();
		}

		lLSM.setC(false);
		lLSM.setILO(false);
		lLSM.setIA(pLightSheetIndex, lCurrentA);
		lLSM.addCurrentStateToQueue();

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
								pLightSheetIndex,
								lLSM,
								lStackAcquisition,
								lIAList);

	}

	public void updateNewState(	int pControlPlaneIndex,
								int pLightSheetIndex,
								ArrayList<Double> pArgMaxList)
	{

		int lBestDetectioArm = getAdaptator().getStackAcquisition()
												.getBestDetectioArm(pControlPlaneIndex);

		getAdaptator().getNewAcquisitionState()
									.setAtControlPlaneIA(	pControlPlaneIndex,
												pLightSheetIndex,
												pArgMaxList.get(lBestDetectioArm));
	}

}
