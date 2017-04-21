package clearcontrol.devices.stages.hub.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import clearcontrol.devices.stages.devices.ecc100.ECC100StageDevice;
import clearcontrol.devices.stages.devices.smc100.SMC100StageDevice;
import clearcontrol.devices.stages.hub.StageHubDevice;

import org.junit.Test;

/**
 * Stage hub demo
 *
 * @author royer
 */
public class StageHubDemo
{

  /**
   * Test
   */
  @Test
  public void test()
  {
    ECC100StageDevice lECC100StageDevice = new ECC100StageDevice();
    SMC100StageDevice lSMC100StageDevice =
                                         new SMC100StageDevice("SMC100",
                                                               "COM1");

    StageHubDevice lStageHub = new StageHubDevice("Hub");

    lStageHub.addDOF(lECC100StageDevice, 1);
    lStageHub.addDOF(lSMC100StageDevice, 0);

    assertTrue(lStageHub.open());

    assertEquals(2, lStageHub.getNumberOfDOFs());

    assertTrue(lStageHub.start());

    lStageHub.home(0);
    lStageHub.home(1);

    assertTrue(lStageHub.stop());

    lStageHub.close();

  }

}
