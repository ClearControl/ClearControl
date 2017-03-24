package clearcontrol.devices.optomech.filterwheels.devices.fli.demo;

import static org.junit.Assert.assertTrue;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.optomech.filterwheels.devices.fli.FLIFilterWheelDevice;

import org.junit.Test;

/**
 * FLI filter wheel demo
 *
 * @author royer
 */
public class FLIFilterWheelDemo
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
    final FLIFilterWheelDevice lFLIFilterWheelDevice =
                                                     new FLIFilterWheelDevice("COM25");

    assertTrue(lFLIFilterWheelDevice.open());

    final Variable<Integer> lPositionVariable =
                                              lFLIFilterWheelDevice.getPositionVariable();
    final Variable<Integer> lSpeedVariable =
                                           lFLIFilterWheelDevice.getSpeedVariable();

    for (int i = 0; i < 10; i++)
    {
      int lTargetPosition = i % 10;
      lPositionVariable.set(lTargetPosition);
      lSpeedVariable.set((i / 30));
      Thread.sleep(30);
      int lCurrentPosition = lPositionVariable.get();
      System.out.format("i=%d, tp=%d, cp=%d\n",
                        i,
                        lTargetPosition,
                        lCurrentPosition);
    }

    assertTrue(lFLIFilterWheelDevice.close());

  }

}
