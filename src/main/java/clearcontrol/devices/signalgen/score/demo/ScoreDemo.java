package clearcontrol.devices.signalgen.score.demo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.devices.signalgen.measure.Measure;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.staves.RampSteppingStave;
import clearcontrol.devices.signalgen.staves.TriggerStave;

import org.junit.Test;

/**
 * Score demo
 *
 * @author royer
 */
public class ScoreDemo
{

  /**
   * Demo
   * 
   * @throws IOException
   *           NA
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void demo() throws IOException, InterruptedException
  {

    final Score lScore = new Score("Test Score");

    final Measure lMeasure = new Measure("Test Measure");

    final TriggerStave lCameraTriggerStave =
                                           new TriggerStave("camera trigger");
    lCameraTriggerStave.setStart(0.2f);
    lCameraTriggerStave.setStop(0.6f);

    final RampSteppingStave lGalvoScannerStave =
                                               new RampSteppingStave("galvo");
    lGalvoScannerStave.setSyncStart(0.1f);
    lGalvoScannerStave.setSyncStop(0.7f);
    lGalvoScannerStave.setStartValue(0f);
    lGalvoScannerStave.setStopValue(1f);
    lGalvoScannerStave.setStepHeight(0.02f);

    final TriggerStave lLaserTriggerStave =
                                          new TriggerStave("laser trigger");
    lLaserTriggerStave.setStart(0.3f);
    lLaserTriggerStave.setStop(0.5f);

    lMeasure.setStave(0, lCameraTriggerStave);
    lMeasure.setStave(1, lGalvoScannerStave);
    lMeasure.setStave(2, lLaserTriggerStave);

    lMeasure.setDuration(1, TimeUnit.SECONDS);

    for (int i = 0; i < 10; i++)
    {
      lGalvoScannerStave.setSyncStop((float) (0.5 + 0.03 * i));
      lScore.addMeasure(lMeasure.duplicate());
    }

    final ScoreVisualizerJFrame lVisualize =
                                           ScoreVisualizerJFrame.visualizeAndWait("test",
                                                                                  lScore);/**/

    while (lVisualize.isVisible())
    {
      ThreadSleep.sleep(1000, TimeUnit.MILLISECONDS);
    }
  }

}
