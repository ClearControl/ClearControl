package clearcontrol.devices.signalgen.devices.nirio.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.devices.nirio.NIRIOSignalGenerator;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.devices.signalgen.measure.Measure;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.SinusStave;

import org.junit.Test;

/**
 * NIRIO signal generator demos
 *
 * @author royer
 */
public class NIRIOSignalGeneratorDemo
{

  /**
   * Plays back a simple sample harmonic signals
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void demo1() throws InterruptedException
  {
    final NIRIOSignalGenerator lNIRIOSignalGenerator =
                                                     new NIRIOSignalGenerator();

    assertTrue(lNIRIOSignalGenerator.open());

    final ScoreInterface lScore = buildScore();

    final ScoreVisualizerJFrame lVisualize =
                                           ScoreVisualizerJFrame.visualize("test",
                                                                           lScore);/**/

    for (int i = 0; i < 1000000000 && lVisualize.isVisible(); i++)
    {
      lNIRIOSignalGenerator.playScore(lScore);
      System.out.println(i);
    }

    lVisualize.dispose();

    assertTrue(lNIRIOSignalGenerator.close());

  }

  /**
   * More complex demo
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void demo2() throws InterruptedException
  {
    final NIRIOSignalGenerator lNIRIOSignalGenerator =
                                                     new NIRIOSignalGenerator();

    assertTrue(lNIRIOSignalGenerator.open());

    final ScoreInterface lScore = new Score("Test Score");

    final Measure lMeasure = new Measure("Test Measure");
    lMeasure.setDuration(1, TimeUnit.MILLISECONDS);

    final SinusStave lSinusStave1 = new SinusStave("sinus1",
                                                   1f,
                                                   0f,
                                                   0.5f);

    final SinusStave lSinusStave2 = new SinusStave("sinus2",
                                                   1f,
                                                   0f,
                                                   0.5f);

    lMeasure.setStave(0, lSinusStave1);
    lMeasure.setStave(1, lSinusStave2);

    lScore.addMeasureMultipleTimes(lMeasure, 10);

    final ScoreVisualizerJFrame lVisualize =
                                           ScoreVisualizerJFrame.visualize("test",
                                                                           lScore);/**/

    for (int i = 0; i < 100000 && lVisualize.isVisible(); i++)
    {
      lSinusStave1.setSinusPeriod((float) (lSinusStave1.getSinusPeriod()
                                           + 0.01));
      lSinusStave2.setSinusPhase(((float) (lSinusStave1.getSinusPhase()
                                           + 0.01)));
      lNIRIOSignalGenerator.playScore(lScore);
      System.out.println(i);
    }

    lVisualize.dispose();

    assertTrue(lNIRIOSignalGenerator.close());

  }

  private final ScoreInterface buildScore()
  {
    final Score lScore = new Score("Test Score");

    final Measure lMeasure = new Measure("Test Measure");

    final SinusStave lSinusStave1 = new SinusStave("sinus1",
                                                   1f,
                                                   0f,
                                                   0.5f);
    final SinusStave lSinusStave2 = new SinusStave("sinus2",
                                                   0.25f,
                                                   0f,
                                                   0.25f);
    final SinusStave lSinusStave3 = new SinusStave("sinus3",
                                                   0.125f,
                                                   0f,
                                                   1f);

    /*for (int i = 0; i < 1; i++)
    	lMeasure.setStave(i, lSinusStave1);
    for (int i = 1; i < 2; i++)
    	lMeasure.setStave(i, lSinusStave2);/**/
    for (int i = 1; i < 8; i++)
      lMeasure.setStave(i, lSinusStave3);/**/

    lScore.addMeasureMultipleTimes(lMeasure, 10);

    lMeasure.setDuration(1, TimeUnit.MILLISECONDS);

    return lScore;
  }

}
