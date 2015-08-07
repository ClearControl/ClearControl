package rtlib.symphony.gui.demo;

import static java.lang.Math.min;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.Test;

import net.miginfocom.swing.MigLayout;
import rtlib.symphony.gui.ScoreVisualizer;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.Score;
import rtlib.symphony.score.ScoreInterface;
import rtlib.symphony.staves.RampContinuousStave;
import rtlib.symphony.staves.SinusStave;

public class ScoreVisualizerDemo
{

	@Test
	public void demo()	throws InvocationTargetException,
						InterruptedException
	{

		final ScoreVisualizer lScoreVisualizer = new ScoreVisualizer();

		final JFrame lTestFrame = new JFrame("Demo");
		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				lTestFrame.setSize(768, 768);
				lTestFrame.setLayout(new MigLayout(	"insets 0",
													"[grow,fill]",
													"[grow,fill]"));
				lTestFrame.add(lScoreVisualizer, "cell 0 0 ");
				lTestFrame.validate();
				lTestFrame.setVisible(true);
			}
		});

		double lOmega = 0;

		for (int i = 0; i < 100 && lTestFrame.isVisible(); i++)
		{
			System.out.println("set(lScore)");
			final ScoreInterface lScore = getTestScores(lOmega);
			lScoreVisualizer.getScoreVariable().set(lScore);

			lOmega += 0.0001;
			lScoreVisualizer.getScalingVariable()
							.setValue(1 + 100 * lOmega);
			Thread.sleep(100);
		}

		while (lTestFrame.isVisible())
			Thread.sleep(10);
	}

	private ScoreInterface getTestScores(double pOmega)
	{
		final Score lScore = new Score("SinusScore");

		final Movement lMovement = new Movement("SinusScoreMovement");
		lMovement.setDuration(1, TimeUnit.SECONDS);

		for (int i = 0; i < 8; i++)
		{
			final RampContinuousStave lRampContinuousStave = new RampContinuousStave(	"i="	+ i,
																						0f,
																						(float) min(1,
																									100 * pOmega),
																						0f,
																						0.1f + i / 8f,
																						0);

			lMovement.setStave(i, lRampContinuousStave);
		}

		for (int i = 8; i < 16; i++)
		{
			final SinusStave lSinusStave = new SinusStave(	"i=" + i,
															(float) (pOmega * (1 + i)),
															(float) ((1f + i) / 16f * pOmega),
															0.5f);

			lMovement.setStave(i, lSinusStave);
		}/**/

		lScore.addMovement(lMovement);

		return lScore;
	}
}
