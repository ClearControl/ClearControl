package clearcontrol.devices.optomech.opticalswitch.devices.optojena.demo;

import static org.junit.Assert.assertTrue;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;

import org.junit.Test;

/**
 * OptoJena fiber switch demo
 *
 * @author royer
 */
public class OptoJenaFiberSwitchDemo
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
    final OptoJenaFiberSwitchDevice lOptoJenaFiberSwitchDevice =
                                                               new OptoJenaFiberSwitchDevice("COM10");

    assertTrue(lOptoJenaFiberSwitchDevice.open());

    final Variable<Integer> lPositionVariable =
                                              lOptoJenaFiberSwitchDevice.getPositionVariable();

    for (int i = 0; i < 500; i++)
    {
      int lTargetPosition = 0; // i % 4;
      lPositionVariable.set(lTargetPosition);
      Thread.sleep(10);
      System.out.format("i=%d, tp=%d\n", i, lTargetPosition);
    }

    assertTrue(lOptoJenaFiberSwitchDevice.close());

  }

}
