package rtlib.microscope.lsm.adaptation.modules;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.calibrator.utils.ImageAnalysisUtils;
import rtlib.stack.StackInterface;

public class AdaptationP extends AdaptationModuleBase	implements
																											AdaptationModuleInterface
{

	private double mTargetLaserPower;

	public AdaptationP(double pTargetLaserPower)
	{
		super();
		mTargetLaserPower = pTargetLaserPower;
	}

	@Override
	public int getNumberOfSteps()
	{
		return 1;
	}

	@Override
	public Boolean apply(Void pVoid)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		int lNumberOfControlPlanes = getAdaptator().getNewAcquisitionState()
																								.getNumberOfControlPlanes();

		int lNumberOfLightSheets = getAdaptator().getLightSheetMicroscope()
																							.getDeviceLists()
																							.getNumberOfLightSheetDevices();

		int lNumberOfDetectionArmDevices = getAdaptator().getLightSheetMicroscope()
																											.getDeviceLists()
																											.getNumberOfDetectionArmDevices();

		lLSM.clearQueue();

		for (int l = 0; l < lNumberOfLightSheets; l++)
		{

			lStackAcquisition.setToControlPlane(0);
			lLSM.setILO(false);
			lLSM.setC(false);
			lLSM.setI(false);
			lLSM.setI(l);
			lLSM.addCurrentStateToQueue();

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				lStackAcquisition.setToControlPlane(czi);
				lLSM.setILO(false);
				lLSM.setC(false);
				lLSM.setI(l);
				lLSM.addCurrentStateToQueue();

				// lLSM.setIP(l, mTargetLaserPower);
				lLSM.setILO(true);
				lLSM.setC(true);
				lLSM.setI(l);
				lLSM.addCurrentStateToQueue();
			}
		}

		lLSM.finalizeQueue();

		try
		{
			lLSM.useRecycler("adaptation", 1, 4, 4);
			final Boolean lPlayQueueAndWait = lLSM.playQueueAndWaitForStacks(	10 + lLSM.getQueueLength(),
																																				TimeUnit.SECONDS);

			if (!lPlayQueueAndWait)
				return null;

			ArrayList<double[]> lAvgIntensities = new ArrayList<>();
			for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
			{
				final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStack = lLSM.getStackVariable(d)
																																									.get();
				double[] lImageSumIntensity = ImageAnalysisUtils.computeImageAverageIntensityPerPlane((OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStack.getImage());
				lAvgIntensities.add(lImageSumIntensity);
			}

			TDoubleArrayList lImageIntensityPerLightSheetList = new TDoubleArrayList();

			int i = 0;
			for (int l = 0; l < lNumberOfLightSheets; l++)
			{
				double lImageIntensityPerLightSheet = 0;
				for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
				{
					int lBestDetectionArm = getAdaptator().getStackAcquisition()
																								.getBestDetectionArm(czi);

					double[] lAvgIntensityArray = lAvgIntensities.get(lBestDetectionArm);

					double lImageSumIntensity = lAvgIntensityArray[i++];

					System.out.format("Average Image Intensity [%d,%d]= %g \n",
														l,
														czi,
														lImageSumIntensity);

					lImageIntensityPerLightSheet += lImageSumIntensity;

				}

				lImageIntensityPerLightSheet /= lNumberOfControlPlanes;

				System.out.format("lAverageIntensityPerLightSheet[%d]= %g \n",
													l,
													lImageIntensityPerLightSheet);

				lImageIntensityPerLightSheetList.add(lImageIntensityPerLightSheet);

			}

			System.out.println("lAverageIntensityPerLightSheetList=" + lImageIntensityPerLightSheetList);

			double lAverageIntensity = lImageIntensityPerLightSheetList.sum() / lImageIntensityPerLightSheetList.size();

			System.out.format("lAverageIntensity= %g \n", lAverageIntensity);

			TDoubleArrayList lPowerPerLightSheet = new TDoubleArrayList();

			for (int l = 0; l < lNumberOfLightSheets; l++)
			{

				double lOldPower = getAdaptator().getNewAcquisitionState()
																					.getAtControlPlaneIP(0, l);

				double lRatio = (lAverageIntensity / lImageIntensityPerLightSheetList.get(l));

				double lNewPower = lOldPower * lRatio;

				System.out.format("lOldPower[%d]= %g \n", l, lOldPower);
				System.out.format("lRatio[%d]= %g \n", l, lRatio);
				System.out.format("lNewPower[%d]= %g \n", l, lNewPower);

				lPowerPerLightSheet.add(lNewPower);
			}

			double lMaxUnNormalizedPower = lPowerPerLightSheet.max();

			System.out.format("lMaxUnNormalizedPowero= %g \n",
												lMaxUnNormalizedPower);

			for (int l = 0; l < lNumberOfLightSheets; l++)
			{
				double lRenormalizedPower = (mTargetLaserPower / lMaxUnNormalizedPower) * lPowerPerLightSheet.get(l);

				System.out.format("Max Damped Ratio[%d]= %g \n",
													l,
													lRenormalizedPower);

				lPowerPerLightSheet.set(l, lRenormalizedPower);
			}

			for (int l = 0; l < lNumberOfLightSheets; l++)
				for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
				{
					System.out.format("Setting new power for lightsheet %d and control mplane %d:  %g \n",
														l,
														czi,
														lPowerPerLightSheet.get(l));

					getAdaptator().getNewAcquisitionState()
												.setAtControlPlaneIP(	czi,
																							l,
																							lPowerPerLightSheet.get(l));
				}

		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
		}

		return false;
	}
}
