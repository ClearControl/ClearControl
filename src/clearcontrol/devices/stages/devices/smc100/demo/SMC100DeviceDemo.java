package clearcontrol.devices.stages.devices.smc100.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import clearcontrol.devices.stages.devices.smc100.SMC100StageDevice;

import org.junit.Test;

public class SMC100DeviceDemo
{

  @Test
  public void demo() throws InterruptedException
  {
    final SMC100StageDevice lSMC100Device =
                                          new SMC100StageDevice("LightStage",
                                                                "COM1");

    assertTrue(lSMC100Device.open());

    lSMC100Device.enable(0);

    System.out.println("goToPosition(" + 0 + ")");
    lSMC100Device.setTargetPosition(0, 0);

    final double lCurrentPosition =
                                  lSMC100Device.getCurrentPosition(0);
    System.out.println("lCurrentPosition=" + lCurrentPosition);

    lSMC100Device.setMinimumPosition(0);
    lSMC100Device.setMaximumPosition(5000);

    lSMC100Device.waitToBeReady(0, 20, TimeUnit.SECONDS);

    Thread.sleep(250);

    final double lValue = 5100;
    System.out.println("goToPosition(" + lValue + ")");
    lSMC100Device.setTargetPosition(0, lValue);

    Thread.sleep(250);

    System.out.println("goToPosition(" + (lValue + 1) + ")");
    lSMC100Device.setTargetPosition(0, lValue + 1);
    lSMC100Device.waitToBeReady(0, 20, TimeUnit.SECONDS);

    Thread.sleep(250);

    System.out.println("waitToBeReady");
    lSMC100Device.waitToBeReady(0, 20, TimeUnit.SECONDS);
    System.out.println("ready!");

    Thread.sleep(3000);

    assertTrue(lSMC100Device.close());
  }

}
