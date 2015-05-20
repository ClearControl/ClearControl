package rtlib.symphony.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.Score;
import rtlib.symphony.staves.RampSteppingStave;
import rtlib.symphony.staves.TriggerStave;

public class ScoreTests
{

	@Test
	public void test() throws IOException, InterruptedException
	{

		final Score lScore = new Score("Test Score");

		final Movement lMovement = new Movement("Test Movement");

		final TriggerStave lCameraTriggerStave = new TriggerStave("camera trigger");
		lCameraTriggerStave.setSyncStart(0.2f);
		lCameraTriggerStave.setSyncStop(0.6f);

		final RampSteppingStave lGalvoScannerStave = new RampSteppingStave("galvo");
		lGalvoScannerStave.setSyncStart(0.1f);
		lGalvoScannerStave.setSyncStop(0.7f);
		lGalvoScannerStave.setStartValue(0f);
		lGalvoScannerStave.setStopValue(1f);
		lGalvoScannerStave.setStepHeight(0.02f);

		final TriggerStave lLaserTriggerStave = new TriggerStave("laser trigger");
		lLaserTriggerStave.setSyncStart(0.3f);
		lLaserTriggerStave.setSyncStop(0.5f);

		lMovement.setStave(0, lCameraTriggerStave);
		lMovement.setStave(1, lGalvoScannerStave);
		lMovement.setStave(2, lLaserTriggerStave);

		lMovement.setDuration(1, TimeUnit.SECONDS);

		lScore.addMovementMultipleTimes(lMovement, 10);

		final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualizeAndWait("test",
																																							lScore);/**/


	}

}
