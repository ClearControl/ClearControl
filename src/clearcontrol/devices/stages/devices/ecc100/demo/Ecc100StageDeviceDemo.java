package clearcontrol.devices.stages.devices.ecc100.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import clearcontrol.devices.stages.devices.ecc100.ECC100StageDevice;

import org.junit.Test;

public class Ecc100StageDeviceDemo
{

  @Test
  public void test() throws InterruptedException
  {
    ECC100StageDevice lECC100StageDevice = new ECC100StageDevice();

    assertTrue(lECC100StageDevice.open());

    assertTrue(lECC100StageDevice.start());

    int lNumberOfDOFs = lECC100StageDevice.getNumberOfDOFs();
    assertTrue(lNumberOfDOFs > 0);

    for (int dof = 0; dof < lNumberOfDOFs; dof++)
    {
      double lCurrentPosition =
                              lECC100StageDevice.getCurrentPosition(dof);
      System.out.println("lCurrentPosition" + dof
                         + "="
                         + lCurrentPosition);
    }

    for (int dof = 0; dof < lNumberOfDOFs; dof++)
    {
      lECC100StageDevice.setTargetPosition(dof, 1000);
    }

    for (int dof = 0; dof < lNumberOfDOFs; dof++)
    {
      lECC100StageDevice.waitToBeReady(dof, 1, TimeUnit.MINUTES);

    }
    for (int dof = 0; dof < lNumberOfDOFs; dof++)
    {
      double lCurrentPosition =
                              lECC100StageDevice.getCurrentPosition(dof);
      System.out.println("lCurrentPosition" + dof
                         + "="
                         + lCurrentPosition);
    }

    assertTrue(lECC100StageDevice.stop());

    lECC100StageDevice.close();
  }

}
