package rtlib.microscope.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.offheap.ByteOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.core.variable.VariableListenerAdapter;
import rtlib.gui.video.video2d.Stack2DDisplay;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.illumination.LightSheet;
import rtlib.stack.StackInterface;
import rtlib.stack.processor.StackIdentityPipeline;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.ScoreInterface;

public class LightSheetMicroscopeDemo
{

	@Test
	public void demoOnSimulators() throws InterruptedException
	{
		StackCameraDeviceSimulator<UnsignedShortType, ShortOffHeapAccess> lCamera = new StackCameraDeviceSimulator<>(	null,
																																																									new UnsignedShortType(),
																																																									null);

		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();

		demoWith(lCamera, lSignalGeneratorDevice);

	}

	public void demoWith(	StackCameraDeviceSimulator<UnsignedShortType, ShortOffHeapAccess> pCamera,
												SignalGeneratorInterface pSignalGeneratorDevice) throws InterruptedException
	{
		final Stack2DDisplay<UnsignedByteType, ByteOffHeapAccess> lVideoDisplayDevice = new Stack2DDisplay<UnsignedByteType, ByteOffHeapAccess>(new UnsignedByteType(),
																																																																						512,
																																																																						512);

		lVideoDisplayDevice.getManualMinMaxIntensityOnVariable()
												.setValue(true);


		LightSheetMicroscope lLightSheetMicroscope = new LightSheetMicroscope("demoscope");

		lLightSheetMicroscope.Add(lVideoDisplayDevice);
		lLightSheetMicroscope.addSignalGeneratorDevice(pSignalGeneratorDevice);

		StackIdentityPipeline<UnsignedShortType, ShortOffHeapAccess> lStackIdentityPipeline = new StackIdentityPipeline<UnsignedShortType, ShortOffHeapAccess>();

		lStackIdentityPipeline.getOutputVariable()
													.addListener(new VariableListenerAdapter<StackInterface<UnsignedShortType, ShortOffHeapAccess>>()
													{

														@Override
														public void setEvent(	StackInterface<UnsignedShortType, ShortOffHeapAccess> pCurrentValue,
																									StackInterface<UnsignedShortType, ShortOffHeapAccess> pNewValue)
														{
															System.out.println(pNewValue);
														}

													});

		lLightSheetMicroscope.addStackCameraDevice(	pCamera,
																								lStackIdentityPipeline);

		final LightSheet lLightSheet = new LightSheet("demolightsheet",
																									9.4,
																									512,
																									2);
		lLightSheetMicroscope.addLightSheetDevice(lLightSheet);

		lLightSheet.getLightSheetLengthInMicronsVariable().setValue(100);
		lLightSheet.getEffectiveExposureInMicrosecondsVariable()
								.setValue(5000);

		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration(TimeUnit.NANOSECONDS),
																				TimeUnit.NANOSECONDS);
		lExposureMovement.setDuration(lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
																	TimeUnit.NANOSECONDS);

		lLightSheet.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
		lLightSheet.addStavesToExposureMovement(lExposureMovement);

		final ScoreInterface lStagingScore = pSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
																																							lStagingScore);

		assertTrue(lLightSheetMicroscope.open());

		lLightSheetMicroscope.addCurrentStateToQueue();
		lLightSheetMicroscope.addCurrentStateToQueue();
		lLightSheetMicroscope.addCurrentStateToQueue();

		for (int i = 0; i < 100; i++)
			lLightSheetMicroscope.playQueue();

		assertTrue(lLightSheetMicroscope.close());

		while (lVisualizer.isVisible())
			Thread.sleep(100);

	}

}
