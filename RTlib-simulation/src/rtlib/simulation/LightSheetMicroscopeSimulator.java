package rtlib.simulation;

import javafx.stage.Stage;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.python.google.common.collect.Lists;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.lasers.devices.sim.LaserDeviceSimulator;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.LightSheetMicroscopeDeviceLists;
import rtlib.microscope.lsm.StackRecyclerManager;
import rtlib.microscope.lsm.component.detection.DetectionArm;
import rtlib.microscope.lsm.component.lightsheet.LightSheet;
import rtlib.microscope.lsm.gui.LightSheetMicroscopeGUI;
import rtlib.microscope.lsm.gui.halcyon.HalcyonMicroscopeGUI;
import rtlib.stack.processor.StackIdentityPipeline;
import rtlib.stages.devices.sim.StageDeviceSimulator;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.ScoreInterface;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by moon on 11/25/15.
 */
public class LightSheetMicroscopeSimulator extends LightSheetMicroscope
{
	// For the simulation microscope
	// 1. Laser
	// 2. LightSheet
	// 3. StackCamera
	// 4. Stage
	private static final double cImageResolution = 2048;

	public LightSheetMicroscopeSimulator()
	{
		super("Simulator");
	}

	public void init(Stage primaryStage) throws Exception
	{
		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();
		final StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess> lCamera =
				new StackCameraDeviceSimulator<>(	null, new UnsignedShortType(), lSignalGeneratorDevice.getTriggerVariable());

		ArrayList<StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess>> pCameras =
				Lists.newArrayList( lCamera );

		for (final StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess> camera : pCameras)
		{
			final StackIdentityPipeline<UnsignedShortType, ShortOffHeapAccess> lStackIdentityPipeline = new StackIdentityPipeline<UnsignedShortType, ShortOffHeapAccess>();

			lStackIdentityPipeline.getOutputVariable()
					.addSetListener((	pCurrentValue,
							pNewValue) -> {
						System.out.println("StackIdentityPipeline" + camera.getName()
								+ "->"
								+ pNewValue);

					});

			camera.getStackWidthVariable()
					.setValue(cImageResolution);
			camera.getStackHeightVariable()
					.setValue(cImageResolution);
			camera.getExposureInMicrosecondsVariable()
					.setValue(5000);

			this.getDeviceLists()
					.addStackCameraDevice(	camera, lStackIdentityPipeline );
		}


		this.getDeviceLists().addSignalGeneratorDevice( lSignalGeneratorDevice );

		// Setting up staging movements:
		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		final ScoreInterface lStagingScore = lSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

//		final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize( "LightSheetDemo", lStagingScore );

		// Setting up detection path:
		for (int i = 0; i < pCameras.size(); i++)
		{
			final DetectionArm lDetectionArm = new DetectionArm("demodetpath" + i);

			this.getDeviceLists().addDetectionArmDevice( lDetectionArm );

//			lDetectionArm.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
//			lDetectionArm.addStavesToExposureMovement(lExposureMovement);
		}

		int pNumberOfLightSheets = 1;

		// Setting up lightsheets:
		for (int i = 0; i < pNumberOfLightSheets; i++)
		{
			final LightSheet lLightSheet = new LightSheet(	"demolightsheet" + i,
					9.4,
					512,
					2);
			this.getDeviceLists()
					.addLightSheetDevice(lLightSheet);

			lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration( TimeUnit.NANOSECONDS),
					TimeUnit.NANOSECONDS);
			lExposureMovement.setDuration(	lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
					TimeUnit.NANOSECONDS);

			lLightSheet.setBeforeExposureMovement(lBeforeExposureMovement);
			lLightSheet.setExposureMovement(lExposureMovement);

//			lLightSheet.getLightSheetLengthInMicronsVariable()
//					.setValue(100);
//			lLightSheet.getEffectiveExposureInMicrosecondsVariable()
//					.setValue(5000);
//			lLightSheet.getImageHeightVariable()
//					.setValue(pCameras.get(0)
//							.getStackHeightVariable()
//							.getValue());
		}

		LaserDeviceSimulator laser = new LaserDeviceSimulator( "1", 1, 405, 60 );
		this.getDeviceLists().addLaserDevice( laser );

		StageDeviceSimulator stage = new StageDeviceSimulator( "1" );
		this.getDeviceLists().addStageDevice( stage );

		HalcyonMicroscopeGUI manager = new HalcyonMicroscopeGUI( primaryStage, this );

		// setting up scope GUI:
//		LightSheetMicroscopeGUI lGUI = new LightSheetMicroscopeGUI(	this, false );
//
//		lGUI.open();
//
//
//		this.open();
//
//		if (lGUI != null)
//			lGUI.connectGUI();
//
//
////		System.out.println("Start building queue");
////
////		for (int i = 0; i < 128; i++)
////			lLightSheetMicroscope.addCurrentStateToQueue();
////		lLightSheetMicroscope.finalizeQueue();
////		System.out.println("finished building queue");
//
//		while (lVisualizer.isVisible())
//		{
//			System.out.println("playQueue!");
//			final FutureBooleanList lPlayQueue = this.playQueue();
//
//			System.out.print("waiting...");
//			final Boolean lBoolean;
//			try
//			{
//				lBoolean = lPlayQueue.get();
//				System.out.print(" ...done!");
//				System.out.println(lBoolean);
//				Thread.sleep(4000);
//			}
//			catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//			catch (ExecutionException e)
//			{
//				e.printStackTrace();
//			}
//
//		}
//
//
//		this.close();
//		if (lGUI != null)
//			lGUI.close();
	}
}
