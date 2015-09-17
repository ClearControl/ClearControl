package rtlib.microscope.lsm.calibrator;

import jama.Matrix;

import java.io.File;
import java.io.IOException;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.calibrator.modules.CalibrationA;
import rtlib.microscope.lsm.calibrator.modules.CalibrationHP;
import rtlib.microscope.lsm.calibrator.modules.CalibrationP;
import rtlib.microscope.lsm.calibrator.modules.CalibrationW;
import rtlib.microscope.lsm.calibrator.modules.CalibrationWP;
import rtlib.microscope.lsm.calibrator.modules.CalibrationXY;
import rtlib.microscope.lsm.calibrator.modules.CalibrationZ;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.scripting.engine.ScriptingEngine;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LightSheetMicroscopeCalibrator
{

	private static final int cMaxIterations = 5;

	private ObjectMapper mObjectMapper;

	private File mCalibrationFolder = MachineConfiguration.getCurrentMachineConfiguration()
															.getFolder("Calibration");

	private final LightSheetMicroscope mLightSheetMicroscope;
	private CalibrationZ mCalibrationZ;
	private CalibrationA mCalibrationA;
	private CalibrationXY mCalibrationXY;
	private CalibrationP mCalibrationP;
	private CalibrationW mCalibrationW;
	private CalibrationHP mCalibrationHP;
	private CalibrationWP mCalibrationWP;

	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;

	public LightSheetMicroscopeCalibrator(LightSheetMicroscope pLightSheetMicroscope)
	{
		mLightSheetMicroscope = pLightSheetMicroscope;
		mCalibrationZ = new CalibrationZ(pLightSheetMicroscope);
		mCalibrationA = new CalibrationA(pLightSheetMicroscope);
		mCalibrationXY = new CalibrationXY(pLightSheetMicroscope);
		mCalibrationP = new CalibrationP(pLightSheetMicroscope);
		mCalibrationW = new CalibrationW(pLightSheetMicroscope);
		mCalibrationWP = new CalibrationWP(pLightSheetMicroscope);
		mCalibrationHP = new CalibrationHP(pLightSheetMicroscope);

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfLightSheetDevices();

		mObjectMapper = new ObjectMapper();
		mObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public boolean calibrate()
	{
		if (!calibrateZ(64))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		if (!calibrateA(32))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		if (!calibrateXY(6))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		calibrateP();
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		/*if (!calibrateW(32))
			return false;/**/

		if (!calibrateZ(64))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		return true;
	}

	public boolean calibrateZ(int pNumberOfSamples)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			int lIteration = 0;
			double lError = Double.POSITIVE_INFINITY;
			do
			{
				lError = calibrateZ(l, pNumberOfSamples, l == 0);
				System.out.println("Error = " + lError);
				if (ScriptingEngine.isCancelRequestedStatic())
					return false;
			}
			while (lError >= 0.02 && lIteration++ < cMaxIterations);
		}
		return true;
	}

	public boolean calibrateA(int pNumberOfAngles)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			int lIteration = 0;
			double lError = Double.POSITIVE_INFINITY;
			do
			{
				lError = calibrateA(l, pNumberOfAngles);
				System.out.println("Error = " + lError);
				if (ScriptingEngine.isCancelRequestedStatic())
					return false;
			}
			while (lError >= 0.5 && lIteration++ < cMaxIterations);
		}
		return true;
	}

	public boolean calibrateXY(int pNumberOfPoints)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			int lIteration = 0;
			double lError = Double.POSITIVE_INFINITY;
			do
			{
				lError = calibrateXY(l, 0, pNumberOfPoints);
				System.out.println("Error = " + lError);
				if (ScriptingEngine.isCancelRequestedStatic())
					return false;
			}
			while (lError >= 0.05 && lIteration++ < cMaxIterations);
		}

		return true;
	}

	public boolean calibrateP()
	{
		int lIteration = 0;
		double lError = Double.POSITIVE_INFINITY;
		do
		{
			mCalibrationP.calibrate();
			lError = mCalibrationP.apply();

			System.out.println("Error = " + lError);
			if (ScriptingEngine.isCancelRequestedStatic())
				return false;
		}
		while (lError >= 0.04 && lIteration++ < cMaxIterations);

		return true;
	}

	public boolean calibrateW(int pNumberOfSamples)
	{
		calibrateW(0, pNumberOfSamples);
		return true;
	}

	// /***************************************************************/ //

	public double calibrateZ(	int pLightSheetIndex,
								int pNumberOfSamples,
								boolean pAdjustDetectionZ)
	{
		mCalibrationZ.calibrate(pLightSheetIndex, pNumberOfSamples, 7);

		return mCalibrationZ.apply(	pLightSheetIndex,
									pAdjustDetectionZ);
	}

	public double calibrateA(int pLightSheetIndex, int pNumberOfAngles)
	{
		mCalibrationA.calibrate(pLightSheetIndex, pNumberOfAngles);

		return mCalibrationA.apply(pLightSheetIndex);
	}

	public double calibrateXY(	int pLightSheetIndex,
								int pDetectionArmIndex,
								int pNumberOfPoints)
	{
		mCalibrationXY.calibrate(	pLightSheetIndex,
									pDetectionArmIndex,
									pNumberOfPoints);

		return mCalibrationXY.apply(pLightSheetIndex,
									pDetectionArmIndex);
	}

	public double calibrateW(	int pDetectionArmIndex,
								int pNumberOfSamples)
	{
		mCalibrationW.calibrate(pDetectionArmIndex, pNumberOfSamples);

		return mCalibrationW.apply();
	}

	public double calibrateWP(	int pLightSheetIndex,
								int pDetectionArmIndex,
								int pNumberOfSamplesW,
								int pNumberOfSamplesP)
	{
		mCalibrationWP.calibrate(	pLightSheetIndex,
									pDetectionArmIndex,
									pNumberOfSamplesW,
									pNumberOfSamplesP);

		return mCalibrationWP.apply(pLightSheetIndex,
									pDetectionArmIndex);
	}

	public double calibrateHP(	int pLightSheetIndex,
								int pDetectionArmIndex,
								int pNumberOfSamplesH,
								int pNumberOfSamplesP)
	{
		mCalibrationHP.calibrate(pLightSheetIndex,pDetectionArmIndex, pNumberOfSamplesH,pNumberOfSamplesP);

		return mCalibrationHP.apply(pLightSheetIndex,
		          									pDetectionArmIndex);
	}

	public void reset()
	{
		mCalibrationZ.reset();
		mCalibrationA.reset();
		mCalibrationXY.reset();
		mCalibrationP.reset();
		mCalibrationW.reset();

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																		.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			final DetectionArmInterface lDetectionArmDevice = mLightSheetMicroscope.getDeviceLists()
																					.getDetectionArmDevice(i);
			lDetectionArmDevice.reset();

		}

		final int lNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																	.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfLightSheetDevices; i++)
		{
			final LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																				.getLightSheetDevice(i);

			lLightSheetDevice.resetFunctions();

		}
	}

	public LightSheetPositioner getLightSheetPositioner(int pLightSheetIndex,
														int pDetectionArmIndex)
	{
		LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																		.getLightSheetDevice(pLightSheetIndex);
		Matrix lTransformMatrix = mCalibrationXY.getTransformMatrix(pLightSheetIndex,
																	pDetectionArmIndex);

		LightSheetPositioner lLightSheetPositioner = new LightSheetPositioner(	lLightSheetDevice,
																				lTransformMatrix);

		return lLightSheetPositioner;
	}

	public void save(String pName)	throws JsonGenerationException,
									JsonMappingException,
									IOException
	{
		CalibrationData lCalibrationData = new CalibrationData(mLightSheetMicroscope);

		lCalibrationData.copyFrom(mLightSheetMicroscope);

		mObjectMapper.writeValue(getFile(pName), lCalibrationData);
	}

	public boolean load(String pName)	throws JsonParseException,
										JsonMappingException,
										IOException
	{
		File lFile = getFile(pName);

		if (!lFile.exists())
			return false;

		CalibrationData lCalibrationData = mObjectMapper.readValue(	lFile,
																	CalibrationData.class);

		lCalibrationData.applyTo(mLightSheetMicroscope);

		return true;
	}

	private File getFile(String pName)
	{
		return new File(mCalibrationFolder, pName + ".json");
	}

}
