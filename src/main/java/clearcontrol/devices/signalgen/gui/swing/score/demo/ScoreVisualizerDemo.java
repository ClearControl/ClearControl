package clearcontrol.devices.signalgen.gui.swing.score.demo;

import static java.lang.Math.min;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import clearcontrol.devices.signalgen.measure.Measure;
import net.miginfocom.swing.MigLayout;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizer;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.RampContinuousStave;
import clearcontrol.devices.signalgen.staves.SinusStave;

import org.junit.Test;

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
        lTestFrame.setLayout(new MigLayout("insets 0",
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
      lScoreVisualizer.getScalingVariable().set(1 + 100 * lOmega);
      Thread.sleep(100);
    }

    while (lTestFrame.isVisible())
      Thread.sleep(10);
  }

  private ScoreInterface getTestScores(double pOmega)
  {
    final Score lScore = new Score("SinusScore");

    final Measure lMeasure = new Measure("SinusScoreMeasure");
    lMeasure.setDuration(1, TimeUnit.SECONDS);

    for (int i = 0; i < 8; i++)
    {
      final RampContinuousStave lRampContinuousStave =
                                                     new RampContinuousStave("i="
                                                                             + i,
                                                                             0f,
                                                                             (float) min(1,
                                                                                         100 * pOmega),
                                                                             0f,
                                                                             0.1f + i
                                                                                    / 8f,
                                                                             0);

      lMeasure.setStave(i, lRampContinuousStave);
    }

    for (int i = 8; i < 16; i++)
    {
      final SinusStave lSinusStave =
                                   new SinusStave("i=" + i,
                                                  (float) (pOmega
                                                           * (1 + i)),
                                                  (float) ((1f + i)
                                                           / 16f
                                                           * pOmega),
                                                  0.5f);

      lMeasure.setStave(i, lSinusStave);
    } /**/

    lScore.addMeasure(lMeasure);

    return lScore;
  }
}
