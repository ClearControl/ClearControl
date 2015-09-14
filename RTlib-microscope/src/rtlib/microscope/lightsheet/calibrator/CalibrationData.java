package rtlib.microscope.lightsheet.calibrator;

import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;

public class CalibrationData
{

	public UnivariateAffineComposableFunction[] mLightSheetXFunctions,
			mLightSheetYFunctions,
			mLightSheetZFunctions,
			mLightSheetWidthFunctions,
			mLightSheetHeightFunctions,
			mLightSheetAlphaFunctions,
			mLightSheetBetaFunctions,
			mLightSheetPowerFunctions, mDetectionArmZFunctions;

	public CalibrationData(	LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		
		int lNumberOfLightSheets = pLightSheetMicroscope.getDeviceLists()
				.getNumberOfLightSheetDevices();
		
		int lNumberOfDetectioArms = pLightSheetMicroscope.getDeviceLists()
				.getNumberOfDetectionArmDevices();
		
		mLightSheetXFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetYFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetZFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetWidthFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetHeightFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetAlphaFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetBetaFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		mLightSheetPowerFunctions = new UnivariateAffineComposableFunction[lNumberOfLightSheets];
		
		mDetectionArmZFunctions = new UnivariateAffineComposableFunction[lNumberOfDetectioArms];
	}

	public void applyTo(LightSheetMicroscope pLightSheetMicroscope)
	{
		
		for (int l = 0; l < mLightSheetXFunctions.length; l++)
		{
			LightSheetInterface lLightSheetDevice = pLightSheetMicroscope.getDeviceLists()
																			.getLightSheetDevice(l);

			lLightSheetDevice.getXFunction()
								.set(new UnivariateAffineFunction(mLightSheetXFunctions[l]));
			lLightSheetDevice.getYFunction()
								.set(new UnivariateAffineFunction(mLightSheetYFunctions[l]));
			lLightSheetDevice.getZFunction()
								.set(new UnivariateAffineFunction(mLightSheetZFunctions[l]));
			lLightSheetDevice.getWidthFunction()
								.set(new UnivariateAffineFunction(mLightSheetWidthFunctions[l]));
			lLightSheetDevice.getHeightFunction()
								.set(new UnivariateAffineFunction(mLightSheetHeightFunctions[l]));
			lLightSheetDevice.getAlphaFunction()
								.set(new UnivariateAffineFunction(mLightSheetAlphaFunctions[l]));
			lLightSheetDevice.getBetaFunction()
								.set(new UnivariateAffineFunction(mLightSheetBetaFunctions[l]));
			lLightSheetDevice.getPowerFunction()
								.set(new UnivariateAffineFunction(mLightSheetPowerFunctions[l]));
		}

		
		for (int d = 0; d < mDetectionArmZFunctions.length; d++)
		{
			DetectionArmInterface lDetectionArmDevice = pLightSheetMicroscope.getDeviceLists()
																				.getDetectionArmDevice(d);

			lDetectionArmDevice.getZFunction()
								.set(new UnivariateAffineFunction(mDetectionArmZFunctions[d]));
		}

	}

	public void copyFrom(LightSheetMicroscope pLightSheetMicroscope)
	{

		for (int l = 0; l < mLightSheetXFunctions.length; l++)
		{
			LightSheetInterface lLightSheetDevice = pLightSheetMicroscope.getDeviceLists()
																			.getLightSheetDevice(l);

			mLightSheetXFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getXFunction()
																						.get());
			mLightSheetYFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getYFunction()
																						.get());
			mLightSheetZFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getZFunction()
																						.get());
			mLightSheetWidthFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getWidthFunction()
																							.get());
			mLightSheetHeightFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getHeightFunction()
																							.get());
			mLightSheetAlphaFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getAlphaFunction()
																							.get());
			mLightSheetBetaFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getBetaFunction()
																						.get());
			mLightSheetPowerFunctions[l] = new UnivariateAffineFunction(lLightSheetDevice.getPowerFunction()
																							.get());
		}

		
		for (int d = 0; d < mDetectionArmZFunctions.length; d++)
		{
			DetectionArmInterface lDetectionArmDevice = pLightSheetMicroscope.getDeviceLists()
																				.getDetectionArmDevice(d);

			mDetectionArmZFunctions[d] = new UnivariateAffineFunction(lDetectionArmDevice.getZFunction()
																							.get());
		}
	}

}
