package clearcontrol.devices.stages.devices.tst.demo;

import org.junit.Test;

import aptj.APTJExeption;
import clearcontrol.devices.stages.devices.tst.TSTStageDevice;

/**
 * TST001 stage device demo
 *
 * @author royer
 */
public class TSTStageDeviceDemo
{

  /**
   * Test
   * @throws InterruptedException NA
   * @throws APTJExeption NA
   */
  @Test
  public void test() throws InterruptedException, APTJExeption
  {
    TSTStageDevice lTSTStageDevice = new TSTStageDevice();
    
    lTSTStageDevice.getHomingVariable(0).setEdge(false, true);
  }

}
