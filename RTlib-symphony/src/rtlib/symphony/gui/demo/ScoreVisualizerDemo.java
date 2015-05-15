package rtlib.symphony.gui.demo;

import static java.lang.Math.min;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.junit.Test;

import rtlib.symphony.gui.ScoreVisualizer;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.Score;
import rtlib.symphony.score.ScoreInterface;
import rtlib.symphony.staves.RampStave;
import rtlib.symphony.staves.SinusStave;

public class ScoreVisualizerDemo
{

	@Test
	public void demo() throws InvocationTargetException,
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

		for (int i = 0; i < 8; i++)
		{
			final RampStave lRampStave = new RampStave(	"i=" + i,
																									0,
																									min(1, 100 * pOmega),
																									0,
																									0.1 + i / 8f,
																									0.5);
			lRampStave.updateStaveArray();
			lMovement.setStave(i, lRampStave);
		}


		for (int i = 8; i < 16; i++)
		{
			final SinusStave lSinusStave = new SinusStave("i=" + i,
																										(pOmega * (1 + i)),
																										((1 + i) / 16f) * pOmega,
																										0.5);
			lSinusStave.updateStaveArray();
			lMovement.setStave(i, lSinusStave);
		}/**/

		lScore.addMovement(lMovement);

		return lScore;
	}
}
