package clearcontrol.devices.cameras.devices.sim.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProvider;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationQueue;
import clearcontrol.devices.cameras.devices.sim.providers.FractalStackProvider;
import clearcontrol.stack.StackInterface;

import org.junit.Test;

/**
 * Stack camera simulator tests.
 *
 * @author royer
 */
public class StackCameraDeviceSimulatorTests
{

  /**
   * 
   * Basic test acquisition with
   * 
   * @throws IOException
   *           NA
   * @throws InterruptedException
   *           NA
   * @throws ExecutionException
   *           NA
   * @throws TimeoutException
   *           NA
   */
  @Test
  public void test() throws IOException,
                     InterruptedException,
                     ExecutionException,
                     TimeoutException
  {

    Variable<Boolean> lTrigger =
                               new Variable<Boolean>("CameraTrigger",
                                                     false);

    StackCameraSimulationProvider lStackCameraSimulationProvider =
                                                                 new FractalStackProvider();

    StackCameraDeviceSimulator lStackCameraDeviceSimulator =
                                                           new StackCameraDeviceSimulator("StackCamera",
                                                                                          lStackCameraSimulationProvider,
                                                                                          lTrigger);

    Variable<StackInterface> lStackVariable =
                                            lStackCameraDeviceSimulator.getStackVariable();

    lStackVariable.addSetListener((StackInterface pOldStack,
                                   StackInterface pNewStack) -> {
      // System.out.println("Arrived: " + pNewStack);
      pNewStack.release();
    });

    lStackCameraDeviceSimulator.getExposureInSecondsVariable()
                               .set(0.001);

    lStackCameraDeviceSimulator.open();

    StackCameraSimulationQueue lQueue =
                                      lStackCameraDeviceSimulator.requestQueue();

    lQueue.clearQueue();

    int lNumberOfImages = 20;

    for (int i = 0; i < lNumberOfImages; i++)
    {
      lQueue.addCurrentStateToQueue();
    }

    lQueue.finalizeQueue();

    for (int j = 0; j < 10; j++)
    {
      Future<Boolean> lPlayQueue =
                                 lStackCameraDeviceSimulator.playQueue(lQueue);

      for (int i = 0; i < lQueue.getQueueLength(); i++)
      {
        lTrigger.setEdge(false, true);
      }

      // System.out.println("waiting...");
      lPlayQueue.get(20L, TimeUnit.SECONDS);
      // System.out.println(" ...done waiting.");

      assertEquals(lNumberOfImages, lStackVariable.get().getDepth());
    }

    lStackCameraDeviceSimulator.close();

  }

}
