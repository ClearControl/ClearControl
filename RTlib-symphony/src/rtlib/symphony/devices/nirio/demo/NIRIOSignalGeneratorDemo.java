package rtlib.symphony.devices.nirio.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.symphony.devices.nirio.NIRIOSignalGenerator;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.Score;
import rtlib.symphony.score.ScoreInterface;
import rtlib.symphony.staves.SinusStave;

public class NIRIOSignalGeneratorDemo
{

	@Test
	public void demo() throws InterruptedException
	{
		final NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();

		assertTrue(lNIRIOSignalGenerator.open());
		assertTrue(lNIRIOSignalGenerator.start());

		final ScoreInterface lCompiledScore = buildScore();

		for (int i = 0; i < 10000; i++)
		{
			lNIRIOSignalGenerator.playScore(lCompiledScore);
			System.out.println(i);
		}

		assertTrue(lNIRIOSignalGenerator.stop());
		assertTrue(lNIRIOSignalGenerator.close());

	}

	private final ScoreInterface buildScore()
	{
		final Score lScore = new Score("Test Score");

		final Movement lMovement = new Movement("Test Movement");

		final SinusStave lSinusStave1 = new SinusStave(	"sinus1",
																										1f,
																										0f,
																										0.5f);
		final SinusStave lSinusStave2 = new SinusStave(	"sinus2",
																										0.25f,
																										0f,
																										0.25f);
		final SinusStave lSinusStave3 = new SinusStave(	"sinus3",
																										0.125f,
																										0f,
																										0.125f);

		for (int i = 0; i < 2; i++)
			lMovement.setStave(i, lSinusStave1);
		for (int i = 2; i < 4; i++)
			lMovement.setStave(i, lSinusStave2);
		for (int i = 4; i < 8; i++)
			lMovement.setStave(i, lSinusStave3);/**/

		lScore.addMovementMultipleTimes(lMovement, 100);

		lMovement.setDuration(10000, TimeUnit.MICROSECONDS);

		return lScore;
	}
}
