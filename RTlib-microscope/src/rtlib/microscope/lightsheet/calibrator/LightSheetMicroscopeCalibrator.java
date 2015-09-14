package rtlib.microscope.lightsheet.calibrator;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jama.Matrix;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.calibrator.modules.CalibrationA;
import rtlib.microscope.lightsheet.calibrator.modules.CalibrationP;
import rtlib.microscope.lightsheet.calibrator.modules.CalibrationW;
import rtlib.microscope.lightsheet.calibrator.modules.CalibrationXY;
import rtlib.microscope.lightsheet.calibrator.modules.CalibrationZ;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.scripting.engine.ScriptingEngine;

public class LightSheetMicroscopeCalibrator
{

	private ObjectMapper mObjectMapper = new ObjectMapper();

	private File mCalibrationFolder = MachineConfiguration.getCurrentMachineConfiguration()
															.getFolder("Calibration");

	private final LightSheetMicroscope mLightSheetMicroscope;
	private CalibrationZ mCalibrationZ;
	private CalibrationA mCalibrationA;
	private CalibrationXY mCalibrationXY;
	private CalibrationP mCalibrationP;
	private CalibrationW mCalibrationW;

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

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfLightSheetDevices();
	}

	public boolean calibrate()
	{
		if (!calibrateZ(20))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		if (!calibrateA(21, 10))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		if (!calibrateXY(21))
			return false;
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		calibrateP();
		if (ScriptingEngine.isCancelRequestedStatic())
			return false;/**/

		if (!calibrateW(32))
			return false;

		return true;
	}

	public boolean calibrateZ(int pNumberOfSamples)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			double lError = Double.POSITIVE_INFINITY;
			do
			{
				lError = calibrateZ(l, pNumberOfSamples, l == 0);
				if (ScriptingEngine.isCancelRequestedStatic())
					return false;
			}
			while (lError >= 0);
		}
		return true;
	}

	public boolean calibrateA(	int pNumberOfAngles,
								int pNumberOfYPositions)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			calibrateA(l, pNumberOfAngles, pNumberOfYPositions);
			if (ScriptingEngine.isCancelRequestedStatic())
				return false;
		}
		return true;
	}

	public boolean calibrateXY(int pNumberOfPoints)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			calibrateXY(l, pNumberOfPoints);
			if (ScriptingEngine.isCancelRequestedStatic())
				return false;
		}

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

	public double calibrateA(	int pLightSheetIndex,
								int pNumberOfAngles,
								int pNumberOfYPositions)
	{
		mCalibrationA.calibrate(pLightSheetIndex,
								pNumberOfAngles,
								pNumberOfYPositions);

		return mCalibrationA.apply(pLightSheetIndex);
	}

	public double calibrateXY(	int pLightSheetIndex,
								int pNumberOfPoints)
	{
		mCalibrationXY.calibrate(pLightSheetIndex, pNumberOfPoints);

		return mCalibrationXY.apply(pLightSheetIndex);
	}

	public double calibrateP()
	{
		mCalibrationP.calibrate();

		return mCalibrationP.apply();
	}

	public double calibrateW(	int pDetectionArmIndex,
								int pNumberOfSamples)
	{
		mCalibrationW.calibrate(pDetectionArmIndex, pNumberOfSamples);

		return mCalibrationW.apply();
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

	public void saveCalibration(String pName)	throws JsonGenerationException,
												JsonMappingException,
												IOException
	{
		CalibrationData lCalibrationData = new CalibrationData(mLightSheetMicroscope);

		lCalibrationData.copyFrom(mLightSheetMicroscope);

		mObjectMapper.writeValue(getFile(pName), lCalibrationData);
	}

	public void loadCalibration(String pName)	throws JsonParseException,
												JsonMappingException,
												IOException
	{
		CalibrationData lCalibrationData = mObjectMapper.readValue(	getFile(pName),
																	CalibrationData.class);

		lCalibrationData.applyTo(mLightSheetMicroscope);
	}

	private File getFile(String pName)
	{
		return new File(mCalibrationFolder, pName + ".json");
	}

}
