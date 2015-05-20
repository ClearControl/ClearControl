package rtlib.symphony.devices.nirio.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.symphony.devices.nirio.NIRIOSignalGenerator;
import rtlib.symphony.gui.ScoreVisualizerJFrame;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.Score;
import rtlib.symphony.score.ScoreInterface;
import rtlib.symphony.staves.SinusStave;

public class NIRIOSignalGeneratorDemo
{

	@Test
	public void demo1() throws InterruptedException
	{
		final NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();

		assertTrue(lNIRIOSignalGenerator.open());
		assertTrue(lNIRIOSignalGenerator.start());

		final ScoreInterface lScore = buildScore();

		final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualize(	"test",
																																										lScore);/**/

		for (int i = 0; i < 100000 && lVisualize.isVisible(); i++)
		{
			lNIRIOSignalGenerator.playScore(lScore);
			System.out.println(i);
		}

		lVisualize.dispose();

		assertTrue(lNIRIOSignalGenerator.stop());
		assertTrue(lNIRIOSignalGenerator.close());


	}

	@Test
	public void demo2() throws InterruptedException
	{
		final NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();

		assertTrue(lNIRIOSignalGenerator.open());
		assertTrue(lNIRIOSignalGenerator.start());

		final ScoreInterface lScore = new Score("Test Score");

		final Movement lMovement = new Movement("Test Movement");
		lMovement.setDuration(1, TimeUnit.MILLISECONDS);

		final SinusStave lSinusStave1 = new SinusStave(	"sinus1",
																										1f,
																										0f,
																										0.5f);

		final SinusStave lSinusStave2 = new SinusStave(	"sinus2",
																									1f,
																									0f,
																									0.5f);

		lMovement.setStave(0, lSinusStave1);
		lMovement.setStave(1, lSinusStave2);

		lScore.addMovementMultipleTimes(lMovement, 10);

		final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualize(	"test",
																																							lScore);/**/

		for (int i = 0; i < 100000 && lVisualize.isVisible(); i++)
		{
			lSinusStave1.setSinusPeriod((float) (lSinusStave1.getSinusPeriod() + 0.01));
			lSinusStave2.setSinusPhase(((float) (lSinusStave1.getSinusPhase() + 0.01)));
			lNIRIOSignalGenerator.playScore(lScore);
			System.out.println(i);
		}

		lVisualize.dispose();

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

		for (int i = 0; i < 1; i++)
			lMovement.setStave(i, lSinusStave1);
		for (int i = 1; i < 2; i++)
			lMovement.setStave(i, lSinusStave2);
		for (int i = 2; i < 8; i++)
			lMovement.setStave(i, lSinusStave3);/**/

		lScore.addMovementMultipleTimes(lMovement, 10);

		lMovement.setDuration(1, TimeUnit.MILLISECONDS);

		return lScore;
	}

}
