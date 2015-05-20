package rtlib.microscope.lightsheetmicroscope.illumination.demo;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.microscope.lightsheetmicroscope.illumination.LightSheet;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.ScoreInterface;

public class LightSheetDemo
{

	@Test
	public void demo() throws InterruptedException
	{
		final LightSheet lLightSheet = new LightSheet("demo", 9.4, 512, 2);
		lLightSheet.getLightSheetLengthInMicronsVariable().setValue(100);


		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration(TimeUnit.NANOSECONDS),
																				TimeUnit.NANOSECONDS);
		lExposureMovement.setDuration(lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
																	TimeUnit.NANOSECONDS);

		lLightSheet.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
		lLightSheet.addStavesToExposureMovement(lExposureMovement);

		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();

		final ScoreInterface lStagingScore = lSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		for (int i = 0; i < 100; i++)
			lSignalGeneratorDevice.addCurrentStateToQueue();

		lSignalGeneratorDevice.playQueue();

		final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
																																							lStagingScore);

		while (lVisualizer.isVisible())
		{
			Thread.sleep(100);
		}

	}

}
