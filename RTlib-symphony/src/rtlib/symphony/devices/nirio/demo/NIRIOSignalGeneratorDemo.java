package rtlib.symphony.devices.nirio.demo;

import static org.junit.Assert.assertTrue;
import nirioj.direttore.Direttore;

import org.junit.Test;

import rtlib.symphony.devices.nirio.NIRIOSignalGenerator;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.CompiledScore;
import rtlib.symphony.score.Score;
import rtlib.symphony.staves.SinusStave;

public class NIRIOSignalGeneratorDemo
{

	@Test
	public void demo() throws InterruptedException
	{
		NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();

		assertTrue(lNIRIOSignalGenerator.open());
		assertTrue(lNIRIOSignalGenerator.start());

		CompiledScore lCompiledScore = buildScore();

		for (int i = 0; i < 10000; i++)
		{
			lNIRIOSignalGenerator.playScore(lCompiledScore);
			System.out.println(i);
		}

		assertTrue(lNIRIOSignalGenerator.stop());
		assertTrue(lNIRIOSignalGenerator.close());

	}

	private CompiledScore buildScore()
	{
		Score lScore = new Score("Test Score");

		Movement lMovement = new Movement("Test Movement");

		SinusStave lSinusStave1 = new SinusStave("sinus1", 1, 0, 0.5);
		SinusStave lSinusStave2 = new SinusStave("sinus2", 0.25, 0, 0.25);
		SinusStave lSinusStave3 = new SinusStave(	"sinus3",
																							0.125,
																							0,
																							0.125);

		for (int i = 0; i < 2; i++)
			lMovement.setStave(i, lSinusStave1);
		for (int i = 2; i < 4; i++)
			lMovement.setStave(i, lSinusStave2);
		for (int i = 4; i < 8; i++)
			lMovement.setStave(i, lSinusStave3);/**/

		lScore.addMovementMultipleTimes(lMovement, 100);

		lMovement.setTotalDurationAndGranularityInMicroseconds(	10000,
																														3,
																														2048);

		System.out.println(lScore.getScoreBuffer());

		CompiledScore lCompiledScore = new CompiledScore(	lScore,
																											Direttore.cNanosecondsPerTicks);

		return lCompiledScore;
	}

}
