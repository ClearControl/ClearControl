package clearcontrol.devices.stages.devices.tst.demo;

import java.util.concurrent.TimeUnit;

import aptj.APTJExeption;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.devices.stages.devices.tst.TSTStageDevice;

import org.junit.Test;

/**
 * TST001 stage device demo
 *
 * @author royer
 */
public class TSTStageDeviceDemo implements WaitingInterface
{

  /**
   * Test
   * 
   * @throws InterruptedException
   *           NA
   * @throws APTJExeption
   *           NA
   */
  @Test
  public void test() throws InterruptedException, APTJExeption
  {
    TSTStageDevice lTSTStageDevice = new TSTStageDevice();

    lTSTStageDevice.open();

    int lNumberOfDOFs = lTSTStageDevice.getNumberOfDOFs();
    System.out.println("lNumberOfDOFs=" + lNumberOfDOFs);

    System.out.println("Homing");
    for (int i = 0; i < lNumberOfDOFs; i++)
      lTSTStageDevice.getHomingVariable(i).setEdge(false, true);

    ThreadSleep.sleep(100, TimeUnit.MILLISECONDS);

    System.out.print("Waiting...");
    lTSTStageDevice.waitToBeReady(30, TimeUnit.SECONDS);
    System.out.println(" ...done!");

    System.out.println("Closing.");
    lTSTStageDevice.close();
  }

}
