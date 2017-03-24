package clearcontrol.devices.lasers.devices.hub.demo;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import clearcontrol.devices.lasers.devices.cobolt.CoboltLaserDevice;
import clearcontrol.devices.lasers.devices.hub.LasertHubDevice;
import clearcontrol.devices.lasers.devices.omicron.OmicronLaserDevice;

import org.junit.Test;

/**
 * Laser hub demo
 *
 * @author royer
 */
public class LaserHubDeviceDemo
{

  /**
   * Basic demo.
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void test() throws InterruptedException
  {
    final LasertHubDevice lLaserHubDevice = new LasertHubDevice();

    final OmicronLaserDevice lLaser1 = new OmicronLaserDevice("COM4");
    lLaserHubDevice.addLaser(lLaser1);

    final OmicronLaserDevice lLaser2 = new OmicronLaserDevice("COM5");
    lLaserHubDevice.addLaser(lLaser2);

    final OmicronLaserDevice lLaser3 = new OmicronLaserDevice("COM6");
    lLaserHubDevice.addLaser(lLaser3);

    final CoboltLaserDevice lLaser4 = new CoboltLaserDevice("Jive",
                                                            100,
                                                            "COM7");
    lLaserHubDevice.addLaser(lLaser4);

    assertTrue(lLaserHubDevice.open());

    assertTrue(lLaserHubDevice.start());

    System.out.println(lLaserHubDevice.getLaserDeviceList());

    for (int r = 0; r < 1; r++)
    {
      for (int i = 0; i < 100; i++)
      {
        final int lTargetPower = i;
        System.out.format("setting target power to: \t%d mW \n",
                          lTargetPower);
        lLaserHubDevice.setTargetPowerInMilliWatt(lTargetPower);
        System.out.format("       current power at: \t%s mW \n",
                          Arrays.toString(lLaserHubDevice.getCurrentPowersInMilliWatt()));
        // Thread.sleep(1);
      }
    }

    assertTrue(lLaserHubDevice.stop());

    assertTrue(lLaserHubDevice.close());

  }

}
