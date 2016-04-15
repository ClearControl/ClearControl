package rtlib.hardware.signalgen.devices.nirio.compiler.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.hardware.signalgen.devices.nirio.compiler.NIRIOCompiledScore;
import rtlib.hardware.signalgen.devices.nirio.compiler.NIRIOScoreCompiler;
import rtlib.hardware.signalgen.movement.Movement;
import rtlib.hardware.signalgen.score.Score;
import rtlib.hardware.signalgen.staves.RampSteppingStave;
import rtlib.hardware.signalgen.staves.TriggerStave;

public class NIRIOScoreCompilerTests
{

	@Test
	public void testCompilation() throws InterruptedException
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

		lMovement.setDuration(5, TimeUnit.MILLISECONDS);

		final int lNumberOfMovements = 10;
		final int repeats = 100;

		final NIRIOCompiledScore lNIRIOCompiledScore = new NIRIOCompiledScore();

		final long lStartTimeNs = System.nanoTime();
		for (int i = 0; i < repeats; i++)
		{
			lScore.clear();
			lScore.addMovementMultipleTimes(lMovement, lNumberOfMovements);
			NIRIOScoreCompiler.compile(lNIRIOCompiledScore, lScore);
		}
		final long lStoptTimeNs = System.nanoTime();
		final long lElapsedTimeNs = (lStoptTimeNs - lStartTimeNs) / repeats;
		final double lElapsedTimeMs = lElapsedTimeNs * 1e-6;
		System.out.format("elapsed time: total= %g ms, per-movement= %g ms\n",
											lElapsedTimeMs,
											lElapsedTimeMs / lNumberOfMovements);

		System.out.println(lNIRIOCompiledScore.toString());

		assertEquals(	4 * lNumberOfMovements,
									lNIRIOCompiledScore.getDeltaTimeBuffer()
																			.getSizeInBytes());
		assertEquals(	4 * lNumberOfMovements,
									lNIRIOCompiledScore.getSyncBuffer()
																			.getSizeInBytes());
		assertEquals(	4 * lNumberOfMovements,
									lNIRIOCompiledScore.getNumberOfTimePointsBuffer()
																			.getSizeInBytes());
		assertEquals(	2 * 2048 * 16 * lNumberOfMovements,
									lNIRIOCompiledScore.getScoreBuffer()
																			.getSizeInBytes());

		/*final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualizeAndWait("test",
																																										lScore);/**/

	}

	@Test
	public void testQuantization() throws InterruptedException
	{
		final Score lScore = new Score("Test Score");

		final Movement lMovement = new Movement("Test Movement");

		final RampSteppingStave lGalvoScannerStave = new RampSteppingStave("galvo");
		lGalvoScannerStave.setSyncStart(0.1f);
		lGalvoScannerStave.setSyncStop(0.7f);
		lGalvoScannerStave.setStartValue(0f);
		lGalvoScannerStave.setStopValue(1f);
		lGalvoScannerStave.setStepHeight(0.02f);

		lMovement.setStave(1, lGalvoScannerStave);

		lScore.addMovementMultipleTimes(lMovement, 1);

		lMovement.setDuration(1, TimeUnit.SECONDS);

		System.out.println("delta=" + NIRIOScoreCompiler.getDeltaTimeInNs(lMovement));
		System.out.println("nbtp=" + NIRIOScoreCompiler.getNumberOfTimePoints(lMovement));

		assertEquals(	488281,
									NIRIOScoreCompiler.getDeltaTimeInNs(lMovement));
		assertEquals(	2048,
									NIRIOScoreCompiler.getNumberOfTimePoints(lMovement));

		lMovement.setDuration(100, TimeUnit.MICROSECONDS);

		System.out.println("delta=" + NIRIOScoreCompiler.getDeltaTimeInNs(lMovement));
		System.out.println("nbtp=" + NIRIOScoreCompiler.getNumberOfTimePoints(lMovement));

		assertEquals(3000, NIRIOScoreCompiler.getDeltaTimeInNs(lMovement));
		assertEquals(	33,
									NIRIOScoreCompiler.getNumberOfTimePoints(lMovement));

		/*
		ScoreVisualizerJFrame.visualizeAndWait("test", lScore);/**/

	}

}
