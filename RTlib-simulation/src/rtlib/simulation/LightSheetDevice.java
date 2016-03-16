package rtlib.simulation;

import rtlib.microscope.lsm.component.lightsheet.LightSheet;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.devices.sim.SignalGeneratorSimulatorDevice;
import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.ScoreInterface;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by moon on 11/24/15.
 */
public class LightSheetDevice
{
	public void demo() throws ExecutionException, InterruptedException
	{
		final LightSheet lLightSheet = new LightSheet(	"demo",
				9.4,
				512,
				2);
		// The below code has error in runtime
//		lLightSheet.getHeightVariable()
//				.setValue(100);
//		lLightSheet.getEffectiveExposureInMicrosecondsVariable()
//				.setValue(5000);

		final Movement lBeforeExposureMovement = new Movement("BeforeExposure");
		final Movement lExposureMovement = new Movement("Exposure");

		lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration( TimeUnit.NANOSECONDS),
				TimeUnit.NANOSECONDS);
		lExposureMovement.setDuration(	lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
				TimeUnit.NANOSECONDS);

		lLightSheet.setBeforeExposureMovement(lBeforeExposureMovement);
		lLightSheet.setExposureMovement(lExposureMovement);

		final SignalGeneratorInterface lSignalGeneratorDevice = new SignalGeneratorSimulatorDevice();

		final ScoreInterface lStagingScore = lSignalGeneratorDevice.getStagingScore();

		lStagingScore.addMovement(lBeforeExposureMovement);
		lStagingScore.addMovement(lExposureMovement);

		final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize(	"LightSheetDemo",
				lStagingScore);

		assertTrue(lSignalGeneratorDevice.open());

		for (int i = 0; i < 100; i++)
			lSignalGeneratorDevice.addCurrentStateToQueue();

		for (int i = 0; i < 1000000000 && lVisualizer.isVisible(); i++)
		{
			final Future<Boolean> lPlayQueue = lSignalGeneratorDevice.playQueue();
			lPlayQueue.get();
		}

		assertTrue(lSignalGeneratorDevice.close());

		lVisualizer.dispose();
	}

	public static void main(String[] args)
	{
		LightSheetDevice device = new LightSheetDevice();
		try
		{
			device.demo();
		} catch (ExecutionException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
