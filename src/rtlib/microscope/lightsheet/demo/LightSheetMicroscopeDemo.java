package rtlib.microscope.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.variable.Variable;
import rtlib.hardware.cameras.StackCameraDeviceInterface;
import rtlib.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.hardware.lasers.LaserDeviceInterface;
import rtlib.hardware.lasers.devices.sim.LaserDeviceSimulator;
import rtlib.hardware.optomech.filterwheels.FilterWheelDeviceInterface;
import rtlib.hardware.optomech.filterwheels.devices.sim.FilterWheelDeviceSimulator;
import rtlib.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import rtlib.hardware.optomech.opticalswitch.devices.sim.OpticalSwitchDeviceSimulator;
import rtlib.hardware.signalamp.ScalingAmplifierDeviceInterface;
import rtlib.hardware.signalamp.devices.sim.ScalingAmplifierSimulator;
import rtlib.hardware.signalgen.SignalGeneratorInterface;
import rtlib.hardware.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.hardware.signalgen.movement.Movement;
import rtlib.hardware.signalgen.score.ScoreInterface;
import rtlib.hardware.stages.StageType;
import rtlib.hardware.stages.devices.sim.StageDeviceSimulator;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.component.detection.DetectionArm;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheet;
import rtlib.microscope.lightsheet.gui.LightSheetMicroscopeGUI;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackIdentityPipeline;
import rtlib.stack.sourcesink.RandomStackSource;

public class LightSheetMicroscopeDemo
{

	private static final long cImageResolution = 2048;

	@Test
	public void demoSimulatedScope() throws InterruptedException,
																	ExecutionException
	{

		final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		final RecyclerInterface<StackInterface, StackRequest> lRecycler = new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
																																																											10);
		RandomStackSource lRandomStackSource = new RandomStackSource(	100L,
																																	101L,
																																	103L,
																																	lRecycler);

		Variable<Boolean> lTrigger = new Variable<Boolean>(	"CameraTrigger",
																												false);

		final LightSheetMicroscope lLightSheetMicroscope = new LightSheetMicroscope("demoscope");

		// Setting up lasers:

		int[] lLaserWavelengths = new int[]
		{ 405, 488, 561, 594 };
		for (int l = 0; l < 3; l++)
		{
			LaserDeviceInterface lLaser = new LaserDeviceSimulator(	"Laser" + l,
																															l,
																															lLaserWavelengths[l],
																															100 + 10 * l);
			lLightSheetMicroscope.getDeviceLists().addDevice(l, lLaser);
		}

		// Setting up Stage:

		StageDeviceSimulator lStageDeviceSimulator = new StageDeviceSimulator("Stage",
																																					StageType.XYZR);
		lStageDeviceSimulator.addXYZRDOFs();

		lLightSheetMicroscope.getDeviceLists()
													.addDevice(0, lStageDeviceSimulator);

		// Setting up optical switch:

		OpticalSwitchDeviceInterface lOpticalSwitchDeviceSimulator = new OpticalSwitchDeviceSimulator("OpticalSwitch",
																																																	4);
		lLightSheetMicroscope.getDeviceLists()
													.addDevice(0, lOpticalSwitchDeviceSimulator);

		// Setting up Filterwheel:

		int[] lFilterWheelPositions = new int[]
		{ 0, 1, 2, 3 };
		FilterWheelDeviceInterface lFilterWheelDevice = new FilterWheelDeviceSimulator(	"FilterWheel",
																																										lFilterWheelPositions);
		lFilterWheelDevice.setPositionName(0, "405 filter");
		lFilterWheelDevice.setPositionName(1, "488 filter");
		lFilterWheelDevice.setPositionName(2, "561 filter");
		lFilterWheelDevice.setPositionName(3, "594 filter");
		lLightSheetMicroscope.getDeviceLists()
													.addDevice(0, lFilterWheelDevice);

		// Setting up cameras:
		for (int c = 0; c < 2; c++)
		{
			final StackCameraDeviceInterface lCamera = new StackCameraDeviceSimulator("StackCamera" + c,
																																								lRandomStackSource,
																																								lTrigger);

			final StackIdentityPipeline lStackIdentityPipeline = new StackIdentityPipeline();

			lStackIdentityPipeline.getOutputVariable()
														.addSetListener((pCurrentValue, pNewValue) -> {
															System.out.println("StackIdentityPipeline" + lCamera.getName()
																									+ "->"
																									+ pNewValue);

														});

			lCamera.getStackWidthVariable().set(cImageResolution);
			lCamera.getStackHeightVariable().set(cImageResolution);
			lCamera.getExposureInMicrosecondsVariable().set(5000.0);

			lLightSheetMicroscope.addStackCameraDevice(	c,
																									lCamera,
																									lStackIdentityPipeline);
		}

		// Scaling Amplifier:

		ScalingAmplifierDeviceInterface lScalingAmplifier1 = new ScalingAmplifierSimulator("ScalingAmplifier1");
		lLightSheetMicroscope.getDeviceLists()
													.addDevice(0, lScalingAmplifier1);

		ScalingAmplifierDeviceInterface lScalingAmplifier2 = new ScalingAmplifierSimulator("ScalingAmplifier2");
		lLightSheetMicroscope.getDeviceLists()
													.addDevice(1, lScalingAmplifier2);

		// Signal generator:

		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();

		lLightSheetMicroscope.getDeviceLists()
													.addDevice(0, lSignalGeneratorDevice);

		// Setting up staging movements:

		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		final ScoreInterface lStagingScore = lSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		// setting up staging score visualization:

		/*final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
																																							lStagingScore);/**/

		// Setting up detection path:

		for (int c = 0; c < 2; c++)
		{
			final DetectionArm lDetectionArm = new DetectionArm("D" + c);

			lLightSheetMicroscope.getDeviceLists()
														.addDevice(c, lDetectionArm);

			lDetectionArm.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
			lDetectionArm.addStavesToExposureMovement(lExposureMovement);
		}

		// Setting up lightsheets:

		for (int l = 0; l < 4; l++)
		{
			final LightSheet lLightSheet = new LightSheet("I" + l,
																										9.4,
																										512,
																										2);
			lLightSheetMicroscope.getDeviceLists()
														.addDevice(l, lLightSheet);

			lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration(TimeUnit.NANOSECONDS),
																					TimeUnit.NANOSECONDS);
			lExposureMovement.setDuration(lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
																		TimeUnit.NANOSECONDS);

			lLightSheet.setBeforeExposureMovement(lBeforeExposureMovement);
			lLightSheet.setExposureMovement(lExposureMovement);

			lLightSheet.getHeightVariable().set(100.0);
			lLightSheet.getEffectiveExposureInMicrosecondsVariable()
									.set(5000.0);

			lLightSheet.getImageHeightVariable().set(cImageResolution);
		}

		// setting up scope GUI:

		LightSheetMicroscopeGUI lMicroscopeGUI = new LightSheetMicroscopeGUI(	lLightSheetMicroscope,
																																					true);

		lMicroscopeGUI.addGroovyScripting("lsm");
		lMicroscopeGUI.addJythonScripting("lsm");

		lMicroscopeGUI.generate();

		if (lMicroscopeGUI != null)
			assertTrue(lMicroscopeGUI.open());
		else
			lLightSheetMicroscope.sendStacksToNull();

		assertTrue(lLightSheetMicroscope.open());
		Thread.sleep(1000);

		// if (lGUI != null)
		// lGUI.connectGUI();

		/*
		if (false)
		{
			System.out.println("Start building queue");

			for (int i = 0; i < 128; i++)
				lLightSheetMicroscope.addCurrentStateToQueue();
			lLightSheetMicroscope.finalizeQueue();
			System.out.println("finished building queue");

			while (lVisualizer.isVisible())
			{
				System.out.println("playQueue!");
				final FutureBooleanList lPlayQueue = lLightSheetMicroscope.playQueue();

				System.out.print("waiting...");
				final Boolean lBoolean = lPlayQueue.get();
				System.out.print(" ...done!");
				// System.out.println(lBoolean);
				// Thread.sleep(4000);
			}
		}
		else/**/
		{
			while (lMicroscopeGUI.isVisible())
			{
				ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
			}
		}

		assertTrue(lLightSheetMicroscope.close());
		if (lMicroscopeGUI != null)
			assertTrue(lMicroscopeGUI.close());

	}

}
