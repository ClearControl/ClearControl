package clearcontrol.devices.signalamp.devices.srs.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import clearcontrol.devices.signalamp.devices.srs.SIM900MainframeDevice;
import clearcontrol.devices.signalamp.devices.srs.SIM983ScalingAmplifierDevice;

import org.junit.Test;

public class SIM900MainFrameDeviceDemo
{

  @Test
  public void demo() throws InterruptedException
  {
    final SIM900MainframeDevice lSIM900MainframeDevice =
                                                       new SIM900MainframeDevice("COM1");

    assertTrue(lSIM900MainframeDevice.open());

    final SIM983ScalingAmplifierDevice lScalingAmp =
                                                   new SIM983ScalingAmplifierDevice(lSIM900MainframeDevice,
                                                                                    4);

    assertTrue(lScalingAmp.open());

    lScalingAmp.setGain(1);
    // assertEquals(1, lScalingAmp.getGain(), 0.01);
    lScalingAmp.setGain(4.3);

    assertEquals(4.3, lScalingAmp.getGain(), 0.01);
    assertEquals(4.3, lScalingAmp.getGain(), 0.01);

    lScalingAmp.setOffset(0);
    assertEquals(0, lScalingAmp.getOffset(), 0.01);
    lScalingAmp.setOffset(2.3);

    assertEquals(2.3, lScalingAmp.getOffset(), 0.01);
    assertEquals(2.3, lScalingAmp.getOffset(), 0.01);

    for (int i = 0; i < 50; i++)
    {
      System.out.println(i);
      double o = 0.1 * (Math.random() - 0.5);
      double g = 1 + 0.1 * (Math.random() - 0.5);
      lScalingAmp.setOffset(o);
      lScalingAmp.setGain(g);
    }
    lScalingAmp.setOffset(1);
    lScalingAmp.setGain(2);

    assertEquals(1, lScalingAmp.getOffset(), 0.01);
    assertEquals(2, lScalingAmp.getGain(), 0.01);

    for (int i = 0; i < 20; i++)
    {
      System.out.println(i);
      double g = Math.random() - 0.5;
      double o = Math.random() - 0.5;
      lScalingAmp.setOffset(o);
      lScalingAmp.setGain(g);
      System.out.format("g=%g, o=%g \n", g, o);

      double oo = lScalingAmp.getOffset();
      double og = lScalingAmp.getGain();
      System.out.format("og=%g, oo=%g \n", og, oo);
      assertEquals(o, oo, 0.01);
      // assertEquals(g, og, 0.01);
    }

    assertTrue(lScalingAmp.close());

    assertTrue(lSIM900MainframeDevice.close());
  }

}
