package clearcontrol.devices.optomech.opticalswitch.devices.arduino.demo;

import static org.junit.Assert.assertTrue;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.optomech.opticalswitch.devices.arduino.ArduinoOpticalSwitchDevice;

import org.junit.Test;

/**
 * Arduino optical switch demo
 *
 * @author royer
 */
public class ArduinoOpticalSwitchDeviceDemo
{

  /**
   * Demo
   * 
   * @throws InterruptedException
   *           NA
   */
  @Test
  public void test() throws InterruptedException
  {
    final ArduinoOpticalSwitchDevice lArduinoOpticalSwitchDevice =
                                                                 new ArduinoOpticalSwitchDevice("COM14");

    assertTrue(lArduinoOpticalSwitchDevice.open());

    int lNumberOfSwitches =
                          lArduinoOpticalSwitchDevice.getNumberOfSwitches();

    for (int i = 0; i < 500; i++)
    {
      for (int j = 0; j < lNumberOfSwitches; j++)
      {
        final Variable<Boolean> lSwitchVariable =
                                                lArduinoOpticalSwitchDevice.getSwitchVariable(j);

        lSwitchVariable.set(i % 2 == 0);
        Thread.sleep(300);
        System.out.format("i=%d, j=%d\n", i, j);
      }
    }

    assertTrue(lArduinoOpticalSwitchDevice.close());

  }

}
