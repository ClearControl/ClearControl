package clearcontrol.microscope.lightsheet.component.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.signalgen.devices.nirio.NIRIOSignalGenerator;
import clearcontrol.devices.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorQueue;

import org.junit.Test;

/**
 * Demo for lightsheet
 *
 * @author royer
 */
public class LightSheetDemo
{

  /**
   * Demo on signal generator simulator
   * 
   * @throws InterruptedException
   *           NA
   * @throws ExecutionException
   *           NA
   */
  @Test
  public void demoOnSimulator() throws InterruptedException,
                                ExecutionException
  {

    final SignalGeneratorInterface lSignalGeneratorDevice =
                                                          new SignalGeneratorSimulatorDevice();

    final LightSheetSignalGeneratorDevice lLightSheetSignalGeneratorDevice =
                                                                           LightSheetSignalGeneratorDevice.wrap(lSignalGeneratorDevice,
                                                                                                                false);

    runDemoWith(lLightSheetSignalGeneratorDevice);
  }

  /**
   * Demo on real signal generator device
   * 
   * @throws InterruptedException
   *           NA
   * @throws ExecutionException
   *           NA
   */
  @Test
  public void demoOnNIRIO() throws InterruptedException,
                            ExecutionException
  {

    final SignalGeneratorInterface lSignalGeneratorDevice =
                                                          new NIRIOSignalGenerator();

    final LightSheetSignalGeneratorDevice lLightSheetSignalGeneratorDevice =
                                                                           LightSheetSignalGeneratorDevice.wrap(lSignalGeneratorDevice,
                                                                                                                false);

    runDemoWith(lLightSheetSignalGeneratorDevice);
  }

  private void runDemoWith(final LightSheetSignalGeneratorDevice pSignalGeneratorDevice) throws InterruptedException,
                                                                                         ExecutionException
  {
    final LightSheet lLightSheet = new LightSheet("demo", 9.4, 2);

    lLightSheet.getHeightVariable().set(100.0);
    lLightSheet.getImageHeightVariable().set(512L);
    lLightSheet.getEffectiveExposureInSecondsVariable().set(0.005);

    LightSheetSignalGeneratorQueue lQueue =
                                          pSignalGeneratorDevice.requestQueue();
    lQueue.addLightSheetQueue(lLightSheet.requestQueue());

    final ScoreInterface lStagingScore = lQueue.getStagingScore();

    final ScoreVisualizerJFrame lVisualizer =
                                            ScoreVisualizerJFrame.visualize("LightSheetDemo",
                                                                            lStagingScore);

    assertTrue(pSignalGeneratorDevice.open());

    lQueue.clearQueue();
    for (int i = 0; i < 100; i++)
      lQueue.addCurrentStateToQueue();

    for (int i = 0; i < 1000000000 && lVisualizer.isVisible(); i++)
    {
      final Future<Boolean> lPlayQueue =
                                       pSignalGeneratorDevice.playQueue(lQueue);
      lPlayQueue.get();
    }

    assertTrue(pSignalGeneratorDevice.close());

    lVisualizer.dispose();
  }

}
