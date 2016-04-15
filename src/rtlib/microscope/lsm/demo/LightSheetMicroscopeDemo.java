package rtlib.microscope.lsm.demo;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.python.google.common.collect.Lists;

import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
import rtlib.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.variable.Variable;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.devices.sim.LaserDeviceSimulator;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.component.detection.DetectionArm;
import rtlib.microscope.lsm.component.lightsheet.LightSheet;
import rtlib.microscope.lsm.gui.LightSheetMicroscopeGUI;
import rtlib.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackIdentityPipeline;
import rtlib.stack.sourcesink.RandomStackSource;
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
		int[] lLaserWavelengths = new int[]{405,488,561,594};
		for(int l=0; l<3; l++)
		{
			LaserDeviceInterface lLaser = new LaserDeviceSimulator("Laser"+l,l,lLaserWavelengths[l],100+10*l);
			lLightSheetMicroscope.getDeviceLists().addLaserDevice(lLaser);
		}
		
		
		
		// Setting up cameras:
		for (int c=0; c<2 ; c++)
		{
			final StackCameraDeviceInterface lCamera = new StackCameraDeviceSimulator(	lRandomStackSource,
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

			lLightSheetMicroscope.getDeviceLists()
														.addStackCameraDevice(lCamera,
																									lStackIdentityPipeline);
		}

		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();

		lLightSheetMicroscope.getDeviceLists()
													.addSignalGeneratorDevice(lSignalGeneratorDevice);

		// Setting up staging movements:

		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		final ScoreInterface lStagingScore = lSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		// setting up staging score visualization:

		final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
																																							lStagingScore);

		// Setting up detection path:

		for (int c = 0; c < 2; c++)
		{
			final DetectionArm lDetectionArm = new DetectionArm("D" + c);

			lLightSheetMicroscope.getDeviceLists()
														.addDetectionArmDevice(lDetectionArm);

			lDetectionArm.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
			lDetectionArm.addStavesToExposureMovement(lExposureMovement);
		}

		// Setting up lightsheets:

		for (int i = 0; i < 4; i++)
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
									.set(cImageResolution);
		}

		// setting up scope GUI:

		LightSheetMicroscopeGUI lGUI = new LightSheetMicroscopeGUI(	lLightSheetMicroscope,
																																true);

		if (lGUI != null)
			assertTrue(lGUI.open());
		else
			lLightSheetMicroscope.sendStacksToNull();

		assertTrue(lLightSheetMicroscope.open());
		Thread.sleep(1000);

		if (lGUI != null)
			lGUI.connectGUI();

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
