package clearcontrol.microscope.lightsheet.calibrator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.ejml.simple.SimpleMatrix;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.task.TaskDevice;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationA;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationHP;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationP;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationW;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationWP;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationXY;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationZ;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.scripting.engine.ScriptingEngine;

public class Calibrator extends TaskDevice implements
																					LoggingInterface
{

	private static final int cMaxIterations = 5;

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

	private HashMap<String, LightSheetPositioner> mPositionersMap = new HashMap<>();

	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;

	private final Variable<Boolean> mCalibrateZVariable = new Variable<Boolean>("CalibrateZ",
																																							true);
	private final Variable<Boolean> mCalibrateAVariable = new Variable<Boolean>("CalibrateA",
																																							false);
	private final Variable<Boolean> mCalibrateXYVariable = new Variable<Boolean>(	"CalibrateXY",
																																								false);

	private final Variable<Boolean> mCalibratePVariable = new Variable<Boolean>("CalibrateP",
																																							false);
	private final Variable<Boolean> mCalibrateWVariable = new Variable<Boolean>("CalibrateW",
																																							false);
	private final Variable<Boolean> mCalibrateWPVariable = new Variable<Boolean>(	"CalibrateWP",
																																								false);
	private final Variable<Boolean> mCalibrateHPVariable = new Variable<Boolean>(	"CalibrateHP",
																																								false);

	private final Variable<String> mCalibrationDataName = new Variable<String>(	"CalibrationName",
																																							"system");

	private final Variable<Double> mProgressVariable;

	public Calibrator(LightSheetMicroscope pLightSheetMicroscope)
	{
		super(pLightSheetMicroscope.getName() + "Calibrator");

		mLightSheetMicroscope = pLightSheetMicroscope;
		mCalibrationZ = new CalibrationZ(this);
		mCalibrationA = new CalibrationA(this);
		mCalibrationXY = new CalibrationXY(this);
		mCalibrationP = new CalibrationP(this);
		mCalibrationW = new CalibrationW(this);
		mCalibrationWP = new CalibrationWP(this);
		mCalibrationHP = new CalibrationHP(this);

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																												.getNumberOfDevices(DetectionArmInterface.class);

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfDevices(LightSheetInterface.class);

		mProgressVariable = new Variable<Double>(	getName() + "Progress",
																							0.0);

	}

	public LightSheetMicroscope getLightSheetMicroscope()
	{
		return mLightSheetMicroscope;
	}

	@Override
	public void run()
	{
		mProgressVariable.set(0.0);
		calibrate();
		mProgressVariable.set(1.0);
		System.out.println("############################################## Calibration done");

	}

	public boolean calibrate()
	{

		if (getCalibrateZVariable().get() && !calibrateZ(32))
			return false;

		if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
			return false;/**/

		if (getCalibrateAVariable().get() && !calibrateA(32))
			return false;

		if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
			return false;/**/

		if (getCalibrateXYVariable().get() && !calibrateXY(3))
			return false;

		if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
			return false;/**/

		if (getCalibratePVariable().get() && !calibrateP())
			return false;

		if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
			return false;/**/

		/*if (!calibrateW(32))
			return false;/**/

		if ((getCalibrateAVariable().get() || getCalibrateXYVariable().get()) && getCalibrateZVariable().get()
				&& !calibrateZ(64))
			return false;

		if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
			return false;/**/

		if (getCalibratePVariable().get() && !calibrateP())
			if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
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
				System.out.println("############################################## Error = " + lError);
				if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
					return false;

			}
			while (lError >= 0.02 && lIteration++ < cMaxIterations);
			System.out.println("############################################## Done ");
			mProgressVariable.set((1.0 * l) / mNumberOfLightSheetDevices);
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
				System.out.println("############################################## Error = " + lError);
				if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
					return false;

			}
			while (lError >= 0.5 && lIteration++ < cMaxIterations);
			System.out.println("############################################## Done ");
			mProgressVariable.set((1.0 * l) / mNumberOfLightSheetDevices);
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
				System.out.println("############################################## Error = " + lError);
				if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
					return false;

			}
			while (lError >= 0.05 && lIteration++ < cMaxIterations);
			System.out.println("############################################## Done ");
			mProgressVariable.set((1.0 * l) / mNumberOfLightSheetDevices);
		}

		return true;
	}

	public boolean calibrateP()
	{
		mCalibrationP.reset();
		int lIteration = 0;
		double lError = Double.POSITIVE_INFINITY;
		do
		{
			mCalibrationP.calibrate();
			lError = mCalibrationP.apply();

			System.out.println("############################################## Error = " + lError);
			if (ScriptingEngine.isCancelRequestedStatic() || !isRunning())
				return false;

			mProgressVariable.set((1.0 * lIteration) / cMaxIterations);
		}
		while (lError >= 0.04 && lIteration++ < cMaxIterations);
		System.out.println("############################################## Done ");

		return true;
	}

	public boolean calibrateHP(	int pNumberOfSamplesH,
															int pNumberOfSamplesP)
	{
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			calibrateHP(l, 0, pNumberOfSamplesH, pNumberOfSamplesP);
			System.out.println("############################################## Done ");
			mProgressVariable.set((1.0 * l) / mNumberOfLightSheetDevices);
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
		mCalibrationZ.calibrate(pLightSheetIndex, pNumberOfSamples, 9);

		return mCalibrationZ.apply(pLightSheetIndex, pAdjustDetectionZ);
	}

	public double calibrateA(int pLightSheetIndex, int pNumberOfAngles)
	{
		mCalibrationA.calibrate(pLightSheetIndex, pNumberOfAngles);

		return mCalibrationA.apply(pLightSheetIndex);
	}

	public double calibrateXY(int pLightSheetIndex,
														int pDetectionArmIndex,
														int pNumberOfPoints)
	{
		mCalibrationXY.calibrate(	pLightSheetIndex,
															pDetectionArmIndex,
															pNumberOfPoints);

		return mCalibrationXY.apply(pLightSheetIndex, pDetectionArmIndex);
	}

	public double calibrateW(	int pDetectionArmIndex,
														int pNumberOfSamples)
	{
		mCalibrationW.calibrate(pDetectionArmIndex, pNumberOfSamples);

		return mCalibrationW.apply();
	}

	public double calibrateWP(int pLightSheetIndex,
														int pDetectionArmIndex,
														int pNumberOfSamplesW,
														int pNumberOfSamplesP)
	{
		mCalibrationWP.calibrate(	pLightSheetIndex,
															pDetectionArmIndex,
															pNumberOfSamplesW,
															pNumberOfSamplesP);

		return mCalibrationWP.apply(pLightSheetIndex, pDetectionArmIndex);
	}

	public double calibrateHP(int pLightSheetIndex,
														int pDetectionArmIndex,
														int pNumberOfSamplesH,
														int pNumberOfSamplesP)
	{
		mCalibrationHP.calibrate(	pLightSheetIndex,
															pDetectionArmIndex,
															pNumberOfSamplesH,
															pNumberOfSamplesP);

		return mCalibrationHP.apply(pLightSheetIndex, pDetectionArmIndex);
	}

	public void reset()
	{
		mCalibrationZ.reset();
		mCalibrationA.reset();
		mCalibrationXY.reset();
		mCalibrationP.reset();
		mCalibrationW.reset();

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDevices(DetectionArmInterface.class);

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			final DetectionArmInterface lDetectionArmDevice = mLightSheetMicroscope.getDeviceLists()
																																							.getDevice(	DetectionArmInterface.class,
																																													i);
			lDetectionArmDevice.resetFunctions();

		}

		final int lNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																																.getNumberOfDevices(LightSheetInterface.class);

		for (int i = 0; i < lNumberOfLightSheetDevices; i++)
		{
			final LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																					.getDevice(	LightSheetInterface.class,
																																											i);

			lLightSheetDevice.resetFunctions();

		}
	}

	public void positioners()
	{

		final int lNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																																.getNumberOfDevices(LightSheetInterface.class);

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDevices(DetectionArmInterface.class);

		for (int l = 0; l < lNumberOfLightSheetDevices; l++)
			for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
			{

				SimpleMatrix lTransformMatrix = mCalibrationXY.getTransformMatrix(l,
																																					d);

				if (lTransformMatrix != null)
				{
					LightSheetPositioner lLightSheetPositioner = new LightSheetPositioner(lTransformMatrix);

					setPositioner(l, d, lLightSheetPositioner);
				}
			}

	}

	public void setPositioner(int pLightSheetIndex,
														int pDetectionArmIndex,
														LightSheetPositioner pLightSheetPositioner)
	{
		mPositionersMap.put("i" + pLightSheetIndex
												+ "d"
												+ pDetectionArmIndex, pLightSheetPositioner);

	}

	public LightSheetPositioner getPositioner(int pLightSheetIndex,
																						int pDetectionArmIndex)
	{
		return mPositionersMap.get("i" + pLightSheetIndex
																+ "d"
																+ pDetectionArmIndex);

	}

	public void save() throws JsonGenerationException,
										JsonMappingException,
										IOException
	{
		save(mCalibrationDataName.get());
	}

	public boolean load()	throws JsonGenerationException,
												JsonMappingException,
												IOException
	{
		return load(mCalibrationDataName.get());
	}

	public void save(String pName) throws JsonGenerationException,
																JsonMappingException,
																IOException
	{
		CalibrationData lCalibrationData = new CalibrationData(mLightSheetMicroscope);

		lCalibrationData.copyFrom(mLightSheetMicroscope);

		lCalibrationData.copyFrom(mPositionersMap);

		lCalibrationData.saveTo(getFile(pName));

	}

	public boolean load(String pName)	throws JsonParseException,
																		JsonMappingException,
																		IOException
	{
		File lFile = getFile(pName);

		if (!lFile.exists())
			return false;

		CalibrationData lCalibrationData = CalibrationData.readFrom(lFile);

		lCalibrationData.applyTo(mLightSheetMicroscope);

		lCalibrationData.applyTo(mPositionersMap);

		return true;
	}

	private File getFile(String pName)
	{
		return new File(mCalibrationFolder, pName + ".json");
	}

	public Variable<Boolean> getCalibrateZVariable()
	{
		return mCalibrateZVariable;
	}

	public Variable<Boolean> getCalibrateAVariable()
	{
		return mCalibrateAVariable;
	}

	public Variable<Boolean> getCalibrateXYVariable()
	{
		return mCalibrateXYVariable;
	}

	public Variable<Boolean> getCalibratePVariable()
	{
		return mCalibratePVariable;
	}

	public Variable<Boolean> getCalibrateWVariable()
	{
		return mCalibrateWVariable;
	}

	public Variable<Boolean> getCalibrateWPVariable()
	{
		return mCalibrateWPVariable;
	}

	public Variable<Boolean> getCalibrateHPVariable()
	{
		return mCalibrateHPVariable;
	}

	public Variable<Double> getProgressVariable()
	{
		return mProgressVariable;
	}

	public Variable<String> getCalibrationDataNameVariable()
	{
		return mCalibrationDataName;
	}

	public boolean isRunning()
	{
		return getIsRunningVariable().get();
	}

}
