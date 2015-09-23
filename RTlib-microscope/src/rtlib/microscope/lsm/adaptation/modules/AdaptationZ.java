package rtlib.microscope.lsm.adaptation.modules;

import static java.lang.Math.max;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.concurrent.Future;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.AcquisitionState;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;

public class AdaptationZ extends NDIteratorAdaptationModule	implements
																														AdaptationModuleInterface
{

	private static final float cOverScan = 2f;
	private static final float cOverlappRatio = 0.3f;
	private static final double cDZDampening = 0.5;

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

		double lControlPlaneZ = lStackAcquisition.getControlPlaneZ(pControlPlaneIndex);
		int lControlPlaneIndexInStack = lStackAcquisition.getPlaneIndexForZRamp(lControlPlaneZ);

		final TDoubleArrayList lList = new TDoubleArrayList();

		double lCurrentIZ = lLSM.getIZ(pLightSheetIndex);

		lLSM.clearQueue();

		lStackAcquisition.addStackMargin(	max(0,
																					lControlPlaneIndexInStack - 2
																							* lHalfNumberOfSamples),
																			6);

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

			lLSM.setI(pLightSheetIndex);

			if (lControlPlaneIndexInStack - (int) (cOverScan * lHalfNumberOfSamples) <= zi && zi <= lControlPlaneIndexInStack + (int) (cOverScan * lHalfNumberOfSamples))
			{
				lLSM.addCurrentStateToQueue();
			}

		}

		lStackAcquisition.addStackMargin(1);

		lLSM.setIZ(pLightSheetIndex, lCurrentIZ);
		lLSM.addCurrentStateToQueue();

		lLSM.finalizeQueue();

		return findBestDOFValue(pControlPlaneIndex,
														pLightSheetIndex,
														lLSM,
														lStackAcquisition,
														lList);

	}

	public void updateNewState(	int pControlPlaneIndex,
															int pLightSheetIndex,
															ArrayList<Double> lArgMaxList)
	{

		final int lNumberOfDetectionArmDevices = getAdaptator().getLightSheetMicroscope()
																														.getDeviceLists()
																														.getNumberOfDetectionArmDevices();

		final int lNumberOfLightSheetDevices = getAdaptator().getLightSheetMicroscope()
																													.getDeviceLists()
																													.getNumberOfLightSheetDevices();

		boolean lIsOverlappingControlPlane = isOverlappingControlPlane(pControlPlaneIndex);

		AcquisitionState lCurrentAcquisitionState = getAdaptator().getStackAcquisition()
																															.getCurrentState();

		AcquisitionState lNewAcquisitionState = getAdaptator().getNewAcquisitionState();

		int lBestDetectioArm = getAdaptator().getStackAcquisition()
																					.getBestDetectioArm(pControlPlaneIndex);

		boolean lMissingInfo = true;
		double lAverageCorrection = 0;
		int lCount = 0;

		for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
		{
			if (!isRelevantDetectionArm(pControlPlaneIndex, d))
				continue;

			Double lArgMax = lArgMaxList.get(d);
			if (lArgMax != null && Double.isFinite(lArgMax))
			{
				lAverageCorrection += -lArgMax;
				lCount++;
				lMissingInfo = false;
			}
		}

		if (lMissingInfo)
		{
			lNewAcquisitionState.setAtControlPlaneIZ(	pControlPlaneIndex,
																								pLightSheetIndex,
																								Double.NaN);
			return;
		}

		lAverageCorrection = lAverageCorrection / lCount;

		if (Double.isNaN(lNewAcquisitionState.getAtControlPlaneIZ(pControlPlaneIndex,
																															pLightSheetIndex)))
			lNewAcquisitionState.setInterpolatedAtControlPlaneIZ(	pControlPlaneIndex,
																														pLightSheetIndex);

		System.out.format("Correcting IZ(cp=%d,ls=%d) by %g \n",
											pControlPlaneIndex,
											pLightSheetIndex,
											lAverageCorrection);

		lNewAcquisitionState.addAtControlPlaneIZ(	pControlPlaneIndex,
																							pLightSheetIndex,
																							lAverageCorrection);

		int lNumberOfOverlappingControlPlanes = getNumberOfOverlappingControlPlanes();

		if (lNumberOfOverlappingControlPlanes <= 0)
		{
			System.err.println("No overlapping control planes!");
			return;
		}

		if (lIsOverlappingControlPlane)
			for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
			{
				double lDeltaDZ = cDZDampening * (d == 0 ? -1 : 1)
													* (lArgMaxList.get(d) - lAverageCorrection)
													/ (lNumberOfOverlappingControlPlanes * lNumberOfLightSheetDevices);

				System.out.format("lArgMaxList.get(d): %g \n",
													lArgMaxList.get(d));
				System.out.format("lAverageCorrection: %g \n",
													lAverageCorrection);
				System.out.format("Correcting DZ by: %g \n", lDeltaDZ);

				int lNumberOfControlPlanes = getAdaptator().getNewAcquisitionState()
																										.getNumberOfControlPlanes();

				for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
					lNewAcquisitionState.addAtControlPlaneDZ(czi, d, lDeltaDZ);
			}

		/**/

	}

	@Override
	public boolean isRelevantDetectionArm(int pControlPlaneIndex,
																				int pDetectionArmIndex)
	{
		if (isOverlappingControlPlane(pControlPlaneIndex))
			return true;
		else
			return super.isRelevantDetectionArm(pControlPlaneIndex,
																					pDetectionArmIndex);
	}

	private boolean isOverlappingControlPlane(int pControlPlaneIndex)
	{
		double lMinZ = getAdaptator().getStackAcquisition().getMinZ();
		double lMaxZ = getAdaptator().getStackAcquisition().getMaxZ();
		double z = getAdaptator().getStackAcquisition()
															.getControlPlaneZ(pControlPlaneIndex);

		double nz = (z - lMinZ) / (lMaxZ - lMinZ);
		boolean lIsOverlapping = (0.5 - (0.5 * cOverlappRatio) <= nz && nz <= 0.5 + (0.5 * cOverlappRatio));
		return lIsOverlapping;
	}

	private int getNumberOfOverlappingControlPlanes()
	{
		int lNumberOfControlPlanes = getAdaptator().getNewAcquisitionState()
																								.getNumberOfControlPlanes();

		int lCount = 0;

		for (int i = 0; i < lNumberOfControlPlanes; i++)
			if (isOverlappingControlPlane(i))
				lCount++;

		return lCount;
	}

}
