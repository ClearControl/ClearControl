package rtlib.core.score.test;

import java.io.IOException;

import org.junit.Test;

import rtlib.core.score.Movement;
import rtlib.core.score.Score;
import rtlib.core.score.staves.CameraTriggerStave;
import rtlib.core.score.staves.GalvoScannerStave;
import rtlib.core.score.staves.LaserTriggerStave;

public class ScoreTests
{

	@Test
	public void test() throws IOException
	{

		final Score lScore = new Score("Test Score");

		final Movement lMovement = new Movement("Test Movement");

		final CameraTriggerStave lCameraTriggerStave = new CameraTriggerStave("test");
		lCameraTriggerStave.mSyncStart = 0.2;
		lCameraTriggerStave.mSyncStop = 0.6;

		final GalvoScannerStave lGalvoScannerStave = new GalvoScannerStave("test");
		lGalvoScannerStave.mSyncStart = 0.1;
		lGalvoScannerStave.mSyncStop = 0.7;
		lGalvoScannerStave.mStartValue = 0;
		lGalvoScannerStave.mStopValue = 1;

		final LaserTriggerStave lLaserTriggerStave = new LaserTriggerStave("test");
		lLaserTriggerStave.mSyncStart = 0.3;
		lLaserTriggerStave.mSyncStop = 0.5;

		lMovement.setStave(0, lCameraTriggerStave);
		lMovement.setStave(1, lGalvoScannerStave);
		lMovement.setStave(2, lLaserTriggerStave);

		lScore.addMovementMultipleTimes(lMovement, 1);

		System.out.println(lScore.getScoreBuffer());

	}

}
