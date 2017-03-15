package clearcontrol.microscope.lightsheet.component.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.hardware.signalgen.SignalGeneratorInterface;
import clearcontrol.hardware.signalgen.devices.nirio.NIRIOSignalGenerator;
import clearcontrol.hardware.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import clearcontrol.hardware.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.score.ScoreInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;

import org.junit.Test;

public class LightSheetDemo
{

  @Test
  public void demoOnSimulator() throws InterruptedException,
                                ExecutionException
  {

    final SignalGeneratorInterface lSignalGeneratorDevice =
                                                          new SignalGeneratorSimulatorDevice();

    runDemoWith(lSignalGeneratorDevice);
  }

  @Test
  public void demoOnNIRIO() throws InterruptedException,
                            ExecutionException
  {

    final SignalGeneratorInterface lSignalGeneratorDevice =
                                                          new NIRIOSignalGenerator();

    runDemoWith(lSignalGeneratorDevice);
  }

  public void runDemoWith(final SignalGeneratorInterface lSignalGeneratorDevice) throws InterruptedException,
                                                                                 ExecutionException
  {
    final LightSheet lLightSheet =
                                 new LightSheet("demo", 9.4, 512, 2);
    lLightSheet.getHeightVariable().set(100.0);
    lLightSheet.getEffectiveExposureInMicrosecondsVariable()
               .set(5000.0);

    final Movement lBeforeExposureMovement =
                                           new Movement("BeforeExposure");
    final Movement lExposureMovement = new Movement("Exposure");

    lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration(TimeUnit.NANOSECONDS),
                                        TimeUnit.NANOSECONDS);
    lExposureMovement.setDuration(lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
                                  TimeUnit.NANOSECONDS);

    lLightSheet.setBeforeExposureMovement(lBeforeExposureMovement);
    lLightSheet.setExposureMovement(lExposureMovement);

    final ScoreInterface lStagingScore =
                                       lSignalGeneratorDevice.getStagingScore();

    lStagingScore.addMovement(lBeforeExposureMovement);
    lStagingScore.addMovement(lExposureMovement);

    final ScoreVisualizerJFrame lVisualizer =
                                            ScoreVisualizerJFrame.visualize("LightSheetDemo",
                                                                            lStagingScore);

    assertTrue(lSignalGeneratorDevice.open());

    for (int i = 0; i < 100; i++)
      lSignalGeneratorDevice.addCurrentStateToQueue();

    for (int i = 0; i < 1000000000 && lVisualizer.isVisible(); i++)
    {
      final Future<Boolean> lPlayQueue =
                                       lSignalGeneratorDevice.playQueue();
      lPlayQueue.get();
    }

    assertTrue(lSignalGeneratorDevice.close());

    lVisualizer.dispose();
  }

}
