package clearcontrol.devices.signalgen.devices.nirio.compiler.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.devices.nirio.compiler.NIRIOCompiledScore;
import clearcontrol.devices.signalgen.devices.nirio.compiler.NIRIOScoreCompiler;
import clearcontrol.devices.signalgen.measure.Measure;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.staves.RampSteppingStave;
import clearcontrol.devices.signalgen.staves.TriggerStave;

import org.junit.Test;

public class NIRIOScoreCompilerTests
{

  @Test
  public void testCompilation() throws InterruptedException
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

    lMeasure.setDuration(5, TimeUnit.MILLISECONDS);

    final int lNumberOfMeasures = 10;
    final int repeats = 100;

    final NIRIOCompiledScore lNIRIOCompiledScore =
                                                 new NIRIOCompiledScore();

    final long lStartTimeNs = System.nanoTime();
    for (int i = 0; i < repeats; i++)
    {
      lScore.clear();
      lScore.addMeasureMultipleTimes(lMeasure, lNumberOfMeasures);
      NIRIOScoreCompiler.compile(lNIRIOCompiledScore, lScore);
    }
    final long lStoptTimeNs = System.nanoTime();
    final long lElapsedTimeNs =
                              (lStoptTimeNs - lStartTimeNs) / repeats;
    final double lElapsedTimeMs = lElapsedTimeNs * 1e-6;
    System.out.format("elapsed time: total= %g ms, per-measure= %g ms\n",
                      lElapsedTimeMs,
                      lElapsedTimeMs / lNumberOfMeasures);

    System.out.println(lNIRIOCompiledScore.toString());

    assertEquals(4 * lNumberOfMeasures,
                 lNIRIOCompiledScore.getDeltaTimeBuffer()
                                    .getSizeInBytes());
    assertEquals(4 * lNumberOfMeasures,
                 lNIRIOCompiledScore.getSyncBuffer()
                                    .getSizeInBytes());
    assertEquals(4 * lNumberOfMeasures,
                 lNIRIOCompiledScore.getNumberOfTimePointsBuffer()
                                    .getSizeInBytes());
    assertEquals(2 * 2048
                 * 16
                 * lNumberOfMeasures,
                 lNIRIOCompiledScore.getScoreBuffer()
                                    .getSizeInBytes());

    /*final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualizeAndWait("test",
    																																								lScore);/**/

  }

  @Test
  public void testQuantization() throws InterruptedException
  {
    final Score lScore = new Score("Test Score");

    final Measure lMeasure = new Measure("Test Measure");

    final RampSteppingStave lGalvoScannerStave =
                                               new RampSteppingStave("galvo");
    lGalvoScannerStave.setSyncStart(0.1f);
    lGalvoScannerStave.setSyncStop(0.7f);
    lGalvoScannerStave.setStartValue(0f);
    lGalvoScannerStave.setStopValue(1f);
    lGalvoScannerStave.setStepHeight(0.02f);

    lMeasure.setStave(1, lGalvoScannerStave);

    lScore.addMeasureMultipleTimes(lMeasure, 1);

    lMeasure.setDuration(1, TimeUnit.SECONDS);

    System.out.println("delta="
                       + NIRIOScoreCompiler.getDeltaTimeInNs(lMeasure));
    System.out.println("nbtp="
                       + NIRIOScoreCompiler.getNumberOfTimePoints(lMeasure));

    assertEquals(488281,
                 NIRIOScoreCompiler.getDeltaTimeInNs(lMeasure));
    assertEquals(2048,
                 NIRIOScoreCompiler.getNumberOfTimePoints(lMeasure));

    lMeasure.setDuration(100, TimeUnit.MICROSECONDS);

    System.out.println("delta="
                       + NIRIOScoreCompiler.getDeltaTimeInNs(lMeasure));
    System.out.println("nbtp="
                       + NIRIOScoreCompiler.getNumberOfTimePoints(lMeasure));

    assertEquals(3000,
                 NIRIOScoreCompiler.getDeltaTimeInNs(lMeasure));
    assertEquals(33,
                 NIRIOScoreCompiler.getNumberOfTimePoints(lMeasure));

    /*
    ScoreVisualizerJFrame.visualizeAndWait("test", lScore);/**/

  }

}
