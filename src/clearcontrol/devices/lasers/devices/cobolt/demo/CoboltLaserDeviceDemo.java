package clearcontrol.devices.lasers.devices.cobolt.demo;

import static org.junit.Assert.assertTrue;

import clearcontrol.devices.lasers.devices.cobolt.CoboltLaserDevice;

import org.junit.Test;

/**
 * Cobolt laser demo
 *
 * @author royer
 */
public class CoboltLaserDeviceDemo
{

  private static final String cCOMPORT = "COM8";

  /**
   * tests turning on/off laser
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testOn() throws InterruptedException
  {
    final CoboltLaserDevice lCoboltLaserDevice =
                                               new CoboltLaserDevice("Mambo",
                                                                     100,
                                                                     cCOMPORT);

    assertTrue(lCoboltLaserDevice.open());

    System.out.println("device id: "
                       + lCoboltLaserDevice.getDeviceId());
    System.out.println("working hours: "
                       + lCoboltLaserDevice.getWorkingHours());
    System.out.println("wavelength: "
                       + lCoboltLaserDevice.getWavelengthInNanoMeter());
    System.out.println("spec power (mW): "
                       + lCoboltLaserDevice.getSpecPowerInMilliWatt());
    System.out.println("max power (mW): "
                       + lCoboltLaserDevice.getMaxPowerInMilliWatt());/**/

    assertTrue(lCoboltLaserDevice.start());

    lCoboltLaserDevice.setLaserOn(true);

    System.out.println("setting target power to 0mW ");
    lCoboltLaserDevice.setTargetPowerInMilliWatt(0);
    System.out.println("target power (mW): "
                       + lCoboltLaserDevice.getTargetPowerInMilliWatt());
    System.out.println("target power (%): "
                       + lCoboltLaserDevice.getTargetPowerInPercent());
    System.out.println("current power (mW): "
                       + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
    System.out.println("current power (%): "
                       + lCoboltLaserDevice.getCurrentPowerInPercent());

    System.out.println("setting target power to 10mW ");
    lCoboltLaserDevice.setTargetPowerInMilliWatt(10);
    System.out.println("target power (mW): "
                       + lCoboltLaserDevice.getTargetPowerInMilliWatt());
    System.out.println("target power (%): "
                       + lCoboltLaserDevice.getTargetPowerInPercent());
    System.out.println("current power (mW): "
                       + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
    System.out.println("current power (%): "
                       + lCoboltLaserDevice.getCurrentPowerInPercent());

    System.out.println("setting target power to 20mW ");
    lCoboltLaserDevice.setTargetPowerInMilliWatt(20);
    System.out.println("target power (mW): "
                       + lCoboltLaserDevice.getTargetPowerInMilliWatt());
    System.out.println("target power (%): "
                       + lCoboltLaserDevice.getTargetPowerInPercent());
    System.out.println("current power (mW): "
                       + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
    System.out.println("current power (%): "
                       + lCoboltLaserDevice.getCurrentPowerInPercent());

    final int lTargetPower = 76;
    System.out.format("setting target power to: \t%d mW \n",
                      lTargetPower);
    lCoboltLaserDevice.setTargetPowerInMilliWatt(lTargetPower);

    for (int i = 0; i < 20; i++)
    {
      System.out.format("       current power at: \t%g mW \n",
                        lCoboltLaserDevice.getCurrentPowerInMilliWatt());
      Thread.sleep(500);
    }

    lCoboltLaserDevice.setLaserOn(false);

    assertTrue(lCoboltLaserDevice.stop());

    lCoboltLaserDevice.setTargetPowerInMilliWatt(0);

    assertTrue(lCoboltLaserDevice.close());

  }

  /**
   * Tests start/stop cycle.
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testStartStopCycle() throws InterruptedException
  {
    final CoboltLaserDevice lCoboltLaserDevice =
                                               new CoboltLaserDevice("Mambo",
                                                                     100,
                                                                     cCOMPORT);

    assertTrue(lCoboltLaserDevice.open());

    System.out.println("device id: "
                       + lCoboltLaserDevice.getDeviceId());
    System.out.println("working hours: "
                       + lCoboltLaserDevice.getWorkingHours());
    System.out.println("wavelength: "
                       + lCoboltLaserDevice.getWavelengthInNanoMeter());
    System.out.println("spec power (mW): "
                       + lCoboltLaserDevice.getSpecPowerInMilliWatt());
    System.out.println("max power (mW): "
                       + lCoboltLaserDevice.getMaxPowerInMilliWatt());/**/

    {
      assertTrue(lCoboltLaserDevice.start());

      lCoboltLaserDevice.getLaserOnVariable().set(true);

      final int lTargetPower = 76;
      System.out.format("setting target power to: \t%d mW \n",
                        lTargetPower);
      lCoboltLaserDevice.setTargetPowerInMilliWatt(lTargetPower);

      for (int i = 0; i < 10; i++)
      {
        System.out.format("       current power at: \t%g mW \n",
                          lCoboltLaserDevice.getCurrentPowerInMilliWatt());
        Thread.sleep(500);
      }

      assertTrue(lCoboltLaserDevice.stop());

      lCoboltLaserDevice.setTargetPowerInMilliWatt(0);
    }

    {
      assertTrue(lCoboltLaserDevice.start());

      lCoboltLaserDevice.getLaserOnVariable().set(true);

      final int lTargetPower = 76;
      System.out.format("setting target power to: \t%d mW \n",
                        lTargetPower);
      lCoboltLaserDevice.setTargetPowerInMilliWatt(lTargetPower);

      for (int i = 0; i < 10; i++)
      {
        System.out.format("       current power at: \t%g mW \n",
                          lCoboltLaserDevice.getCurrentPowerInMilliWatt());
        Thread.sleep(500);
      }

      assertTrue(lCoboltLaserDevice.stop());

      lCoboltLaserDevice.setTargetPowerInMilliWatt(0);
    }

    assertTrue(lCoboltLaserDevice.close());

  }

  /**
   * tests ramp
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void testRamp() throws InterruptedException
  {
    final CoboltLaserDevice lCoboltLaserDevice =
                                               new CoboltLaserDevice("Mambo",
                                                                     100,
                                                                     cCOMPORT);

    assertTrue(lCoboltLaserDevice.open());

    System.out.println("device id: "
                       + lCoboltLaserDevice.getDeviceId());
    System.out.println("working hours: "
                       + lCoboltLaserDevice.getWorkingHours());
    System.out.println("wavelength: "
                       + lCoboltLaserDevice.getWavelengthInNanoMeter());
    System.out.println("spec power (mW): "
                       + lCoboltLaserDevice.getSpecPowerInMilliWatt());
    System.out.println("max power (mW): "
                       + lCoboltLaserDevice.getMaxPowerInMilliWatt());/**/

    for (int i = 0; i < 20; i++)
    {
      System.out.format("       current power at: \t%g mW \n",
                        lCoboltLaserDevice.getCurrentPowerInMilliWatt());
      Thread.sleep(100);
    }

    assertTrue(lCoboltLaserDevice.start());

    lCoboltLaserDevice.setLaserOn(true);

    System.out.println("setting target power to 0mW ");
    lCoboltLaserDevice.setTargetPowerInMilliWatt(0);
    System.out.println("target power (mW): "
                       + lCoboltLaserDevice.getTargetPowerInMilliWatt());
    System.out.println("target power (%): "
                       + lCoboltLaserDevice.getTargetPowerInPercent());
    System.out.println("current power (mW): "
                       + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
    System.out.println("current power (%): "
                       + lCoboltLaserDevice.getCurrentPowerInPercent());

    System.out.println("setting target power to 10mW ");
    lCoboltLaserDevice.setTargetPowerInMilliWatt(10);
    System.out.println("target power (mW): "
                       + lCoboltLaserDevice.getTargetPowerInMilliWatt());
    System.out.println("target power (%): "
                       + lCoboltLaserDevice.getTargetPowerInPercent());
    System.out.println("current power (mW): "
                       + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
    System.out.println("current power (%): "
                       + lCoboltLaserDevice.getCurrentPowerInPercent());

    System.out.println("setting target power to 20mW ");
    lCoboltLaserDevice.setTargetPowerInMilliWatt(20);
    System.out.println("target power (mW): "
                       + lCoboltLaserDevice.getTargetPowerInMilliWatt());
    System.out.println("target power (%): "
                       + lCoboltLaserDevice.getTargetPowerInPercent());
    System.out.println("current power (mW): "
                       + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
    System.out.println("current power (%): "
                       + lCoboltLaserDevice.getCurrentPowerInPercent());

    for (int r = 0; r < 30; r++)
    {
      for (int i = 0; i < 100; i++)
      {
        final int lTargetPower = i;
        System.out.format("setting target power to: \t%d mW \n",
                          lTargetPower);
        lCoboltLaserDevice.setTargetPowerInMilliWatt(lTargetPower);
        System.out.format("       current power at: \t%g mW \n",
                          lCoboltLaserDevice.getCurrentPowerInMilliWatt());
        Thread.sleep(10);
      }
    }

    lCoboltLaserDevice.setLaserOn(false);

    assertTrue(lCoboltLaserDevice.stop());

    lCoboltLaserDevice.setTargetPowerInMilliWatt(0);

    assertTrue(lCoboltLaserDevice.close());

  }

}
