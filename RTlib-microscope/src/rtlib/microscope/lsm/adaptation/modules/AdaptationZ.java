package rtlib.microscope.lsm.adaptation.modules;

import static java.lang.Math.max;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.Future;

import rtlib.gui.plots.MultiPlot;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AdaptationZ extends NDIteratorAdaptationModule implements
																														AdaptationModuleInterface
{

	private MultiPlot mMultiPlotZFocusCurves;

	public AdaptationZ(	int pNumberOfSamples,
											double pProbabilityThreshold)
	{
		super(pNumberOfSamples, pProbabilityThreshold);

	}

	public Future<?> atomicStep(int pControlPlaneIndex,
															int pLightSheetIndex,
															int pNumberOfSamples)
	{
		int lHalfNumberOfSamples = pNumberOfSamples / 2;

		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		LightSheetInterface lLightSheetDevice = lLSM.getDeviceLists()
																								.getLightSheetDevice(pLightSheetIndex);

		double lControlPlaneZ = lStackAcquisition.getControlPlaneZ(pControlPlaneIndex);
		int lControlPlaneIndexInStack = lStackAcquisition.getPlaneIndexForZRamp(lControlPlaneZ);

		int lBestDetectioArm = lStackAcquisition.getBestDetectioArm(pControlPlaneIndex);

		final TDoubleArrayList lList = new TDoubleArrayList();

		lLSM.clearQueue();

		lStackAcquisition.addStackMargin(	max(0,
																					lControlPlaneIndexInStack - 2
																							* lHalfNumberOfSamples),
																			3);

		for (int zi : lStackAcquisition)
		{
			double z = lStackAcquisition.getZRamp(zi);

			if (lControlPlaneIndexInStack - lHalfNumberOfSamples <= zi && zi <= lControlPlaneIndexInStack + lHalfNumberOfSamples)
			{
				lLSM.setC(true);
				lLSM.setILO(true);

				double lDeltaZ = (z - lControlPlaneZ);
				lList.add(lDeltaZ);
			}
			else
			{
				lLSM.setC(false);
				lLSM.setILO(false);
			}

			double lIZ = lStackAcquisition.getIZ(	lControlPlaneIndexInStack,
																						pLightSheetIndex);
			lLSM.setIZ(pLightSheetIndex, lIZ);

			lLSM.selectI(pLightSheetIndex);

			if (lControlPlaneIndexInStack - 2 * lHalfNumberOfSamples <= zi && zi <= lControlPlaneIndexInStack + 2
																																							* lHalfNumberOfSamples)
				lLSM.addCurrentStateToQueue();
		}

		lStackAcquisition.addStackMargin(3);

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
														pLightSheetIndex,
														lLSM,
														lStackAcquisition,
														lList);

	}

	public void updateNewState(	int pControlPlaneIndex,
															int pLightSheetIndex,
															Double lArgmax)
	{
		getAdaptator().getNewAcquisitionState()
									.addAtControlPlaneIZ(	pControlPlaneIndex,
																				pLightSheetIndex,
																				-lArgmax);
	}

}
