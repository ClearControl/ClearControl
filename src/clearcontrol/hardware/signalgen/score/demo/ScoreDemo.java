package clearcontrol.hardware.signalgen.score.demo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.hardware.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.score.Score;
import clearcontrol.hardware.signalgen.staves.RampSteppingStave;
import clearcontrol.hardware.signalgen.staves.TriggerStave;

public class ScoreDemo
{

	@Test
	public void Demo() throws IOException, InterruptedException
	{

		final Score lScore = new Score("Test Score");

		final Movement lMovement = new Movement("Test Movement");

		final TriggerStave lCameraTriggerStave = new TriggerStave("camera trigger");
		lCameraTriggerStave.setStart(0.2f);
		lCameraTriggerStave.setStop(0.6f);

		final RampSteppingStave lGalvoScannerStave = new RampSteppingStave("galvo");
		lGalvoScannerStave.setSyncStart(0.1f);
		lGalvoScannerStave.setSyncStop(0.7f);
		lGalvoScannerStave.setStartValue(0f);
		lGalvoScannerStave.setStopValue(1f);
		lGalvoScannerStave.setStepHeight(0.02f);

		final TriggerStave lLaserTriggerStave = new TriggerStave("laser trigger");
		lLaserTriggerStave.setStart(0.3f);
		lLaserTriggerStave.setStop(0.5f);

		lMovement.setStave(0, lCameraTriggerStave);
		lMovement.setStave(1, lGalvoScannerStave);
		lMovement.setStave(2, lLaserTriggerStave);

		lMovement.setDuration(1, TimeUnit.SECONDS);

		for (int i = 0; i < 10; i++)
		{
			lGalvoScannerStave.setSyncStop((float) (0.5 + 0.03 * i));
			lScore.addMovement(lMovement.copy());
		}

		final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualizeAndWait("test",
																																										lScore);/**/

		while (lVisualize.isVisible())
		{
			ThreadUtils.sleep(1000, TimeUnit.MILLISECONDS);
		}
	}

}
