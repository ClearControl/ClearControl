package rtlib.microscope.lsm.demo;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.python.google.common.collect.Lists;

import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
import rtlib.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.component.detection.DetectionArm;
import rtlib.microscope.lsm.component.lightsheet.LightSheet;
import rtlib.microscope.lsm.gui.LightSheetMicroscopeGUI;
import rtlib.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;
import rtlib.stack.processor.StackIdentityPipeline;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.devices.nirio.NIRIOSignalGenerator;
import rtlib.symphony.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.ScoreInterface;

public class LightSheetMicroscopeDemo
{

	private static final long cImageResolution = 2048;

	@Test
	public void demoOnSimulators() throws InterruptedException,
																ExecutionException
	{
		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();
		final StackCameraDeviceInterface lCamera = new StackCameraDeviceSimulator(null,
																																							lSignalGeneratorDevice.getTriggerVariable());

		demoWith(	true,
							false,
							true,
							Lists.newArrayList(lCamera),
							lSignalGeneratorDevice,
							1);

	}

	@Test
	public void demoOnRealHardwareSingleCamera() throws InterruptedException,
																							ExecutionException
	{
		final SignalGeneratorInterface lSignalGeneratorDevice = new NIRIOSignalGenerator();
		final StackCameraDeviceInterface lCamera = OrcaFlash4StackCamera.buildWithExternalTriggering(	0,
																																																	false);

		demoWith(	false,
							false,
							true,
							Lists.newArrayList(lCamera),
							lSignalGeneratorDevice,
							1);

	}

	@Test
	public void demoOnRealHardwareTwoCamerasFourLightSheets()	throws InterruptedException,
																														ExecutionException
	{
		final SignalGeneratorInterface lSignalGeneratorDevice = new NIRIOSignalGenerator();
		final StackCameraDeviceInterface lCamera1 = OrcaFlash4StackCamera.buildWithExternalTriggering(0,
																																																	false);
		final StackCameraDeviceInterface lCamera2 = OrcaFlash4StackCamera.buildWithExternalTriggering(1,
																																																	false);

		demoWith(	true,
							false,
							true,
							Lists.newArrayList(lCamera1, lCamera2),
							lSignalGeneratorDevice,
							4);

	}

	@Test
	public void demoScriptingOnRealHardwareTwoCamerasFourLightSheets() throws InterruptedException,
																																		ExecutionException
	{
		final SignalGeneratorInterface lSignalGeneratorDevice = new NIRIOSignalGenerator();
		final StackCameraDeviceInterface lCamera1 = OrcaFlash4StackCamera.buildWithExternalTriggering(0,
																																																	false);
		final StackCameraDeviceInterface lCamera2 = OrcaFlash4StackCamera.buildWithExternalTriggering(1,
																																																	false);

		demoWith(	true,
							false,
							false,
							Lists.newArrayList(lCamera1, lCamera2),
							lSignalGeneratorDevice,
							4);

	}

	public void demoWith(	boolean pWithGUI,
												boolean pWith3D,
												boolean pAutoStart,
												ArrayList<StackCameraDeviceInterface> pCameras,
												SignalGeneratorInterface pSignalGeneratorDevice,
												int pNumberOfLightSheets)	throws InterruptedException,
																									ExecutionException
	{

		final LightSheetMicroscope lLightSheetMicroscope = new LightSheetMicroscope("demoscope");

		OptoJenaFiberSwitchDevice lOptoJenaFiberSwitchDevice = new OptoJenaFiberSwitchDevice("COM10");
		lOptoJenaFiberSwitchDevice.setPosition(2);
		lLightSheetMicroscope.getDeviceLists()
													.addOptoMechanicalDevice(lOptoJenaFiberSwitchDevice);

		for (final StackCameraDeviceInterface lCamera : pCameras)
		{
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

			lLightSheetMicroscope.getDeviceLists()
														.addStackCameraDevice(lCamera,
																									lStackIdentityPipeline);
		}

		lLightSheetMicroscope.getDeviceLists()
													.addSignalGeneratorDevice(pSignalGeneratorDevice);

		// Setting up staging movements:

		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		final ScoreInterface lStagingScore = pSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		// setting up staging score visualization:

		final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
																																							lStagingScore);

		// Setting up detection path:

		for (int i = 0; i < pCameras.size(); i++)
		{
			final DetectionArm lDetectionArm = new DetectionArm("D" + i);

			lLightSheetMicroscope.getDeviceLists()
														.addDetectionArmDevice(lDetectionArm);

			lDetectionArm.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
			lDetectionArm.addStavesToExposureMovement(lExposureMovement);
		}

		// Setting up lightsheets:

		for (int i = 0; i < pNumberOfLightSheets; i++)
		{
			final LightSheet lLightSheet = new LightSheet("demolightsheet" + i,
																										9.4,
																										512,
																										2);
			lLightSheetMicroscope.getDeviceLists()
														.addLightSheetDevice(lLightSheet);

			lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration(TimeUnit.NANOSECONDS),
																					TimeUnit.NANOSECONDS);
			lExposureMovement.setDuration(lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
																		TimeUnit.NANOSECONDS);

			lLightSheet.setBeforeExposureMovement(lBeforeExposureMovement);
			lLightSheet.setExposureMovement(lExposureMovement);

			lLightSheet.getHeightVariable().set(100.0);
			lLightSheet.getEffectiveExposureInMicrosecondsVariable()
									.set(5000.0);

			lLightSheet.getImageHeightVariable()
									.set(pCameras.get(0).getStackHeightVariable().get());
		}

		// setting up scope GUI:

		LightSheetMicroscopeGUI lGUI = null;

		if (pWithGUI)
			lGUI = new LightSheetMicroscopeGUI(	lLightSheetMicroscope,
																					pWith3D);

		if (lGUI != null)
			assertTrue(lGUI.open());
		else
			lLightSheetMicroscope.sendStacksToNull();

		assertTrue(lLightSheetMicroscope.open());
		Thread.sleep(1000);

		if (lGUI != null)
			lGUI.connectGUI();

		if (pAutoStart)
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
		else
		{
			while (lVisualizer.isVisible())
			{
				ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
			}
		}

		assertTrue(lLightSheetMicroscope.close());
		if (lGUI != null)
			assertTrue(lGUI.close());

	}

}
