package clearcontrol.microscope.lightsheet.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.log;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.stack.StackInterface;

public class CalibrationP
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;
	private TDoubleArrayList mRatioList;

	public CalibrationP(Calibrator pCalibrator)
	{
		mLightSheetMicroscope = pCalibrator.getLightSheetMicroscope();

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																												.getNumberOfDevices(DetectionArmInterface.class);

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfDevices(LightSheetInterface.class);

	}

	public boolean calibrate()
	{

		TDoubleArrayList lAverageIntensityList = new TDoubleArrayList();
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			Double lValue = calibrate(l, 0, 6);
			if (lValue == null)
				return false;
			lAverageIntensityList.add(lValue);

			if (ScriptingEngine.isCancelRequestedStatic())
				return false;
		}

		System.out.format("Average image intensity list: %s \n",
											lAverageIntensityList);

		double lWeakestLightSheetIntensity = lAverageIntensityList.min();

		System.out.format("Weakest lightsheet intensity: %g \n",
											lWeakestLightSheetIntensity);

		mRatioList = new TDoubleArrayList();
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			mRatioList.add(lWeakestLightSheetIntensity / lAverageIntensityList.get(l));

		System.out.format("Intensity ratios list: %s \n", mRatioList);

		return true;
	}

	public Double calibrate(int pLightSheetIndex,
													int pDetectionArmIndex,
													int pN)
	{
		try
		{
			mLightSheetMicroscope.setI(pLightSheetIndex);
			ThreadUtils.sleep(200, TimeUnit.MILLISECONDS);

			// Building queue start:
			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			mLightSheetMicroscope.setIX(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIY(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIP(pLightSheetIndex, 1);

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				mLightSheetMicroscope.setDZ(i, 0);

			for (int i = 1; i <= pN; i++)
			{
				for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
					mLightSheetMicroscope.setC(d, i == pN);
				mLightSheetMicroscope.addCurrentStateToQueue();
			}
			mLightSheetMicroscope.finalizeQueue();
			// Building queue end.

			mLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(mLightSheetMicroscope.getQueueLength(),
																																												TimeUnit.SECONDS);

			if (!lPlayQueueAndWait)
				return null;

			final StackInterface lStackInterface = mLightSheetMicroscope.getStackVariable(pDetectionArmIndex)
																																	.get();

			OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

			System.out.println("Image: " + lImage);

			long lWidth = lImage.dimension(0);
			long lHeight = lImage.dimension(1);

			System.out.format("Image: width=%d, height=%d \n",
												lWidth,
												lHeight);

			double lAverageIntensity = ImageAnalysisUtils.computeImageAverageIntensityPerPlane(lImage)[0];

			System.out.format("Image: average intensity: %s \n",
												lAverageIntensity);

			return lAverageIntensity;
		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public double apply()
	{
		double lError = 0;

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			System.out.format("Light sheet index: %d \n", l);

			LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																		.getDevice(	LightSheetInterface.class,
																																								l);

			Variable<UnivariateAffineFunction> lPowerFunctionVariable = lLightSheetDevice.getPowerFunction();

			double lPowerRatio = mRatioList.get(l);

			if (lPowerRatio == 0)
				continue;

			System.out.format("Applying power ratio correction: %g to lightsheet %d \n",
												lPowerRatio,
												l);

			lPowerFunctionVariable.get()
														.composeWith(UnivariateAffineFunction.axplusb(lPowerRatio,
																																					0));
			lPowerFunctionVariable.setCurrent();

			System.out.format("Power function for lightsheet %d is now: %s \n",
												l,
												lPowerFunctionVariable.get());

			lError += abs(log(lPowerRatio));
		}

		System.out.format("Error after applying power ratio correction: %g \n",
											lError);

		return lError;
	}

	public void reset()
	{

	}
}
