package clearcontrol.microscope.lightsheet.autopilot.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.autopilot.Adaptator;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;

/**
 * AutoPilot tests
 *
 * @author royer
 */
public class AdaptatorTests
{

  /**
   * tests
   */
  @Test
  public void test()
  {
    LightSheetMicroscope lLightSheetMicroscope =
                                               new LightSheetMicroscope("dummy",
                                                                        null,
                                                                        1,
                                                                        1);

    lLightSheetMicroscope.addDevice(0, new DetectionArm("D0"));
    lLightSheetMicroscope.addDevice(0, new DetectionArm("D1"));
    lLightSheetMicroscope.addDevice(0, new LightSheet("L0", 1, 1, 1));
    lLightSheetMicroscope.addDevice(0, new LightSheet("L1", 1, 1, 1));

    Adaptator lAdaptator = new Adaptator(lLightSheetMicroscope);

    InterpolatedAcquisitionState lState =
                                        new InterpolatedAcquisitionState("state0",
                                                                         1,
                                                                         1,
                                                                         1);
    lState.setupDefault(lLightSheetMicroscope);

    lAdaptator.setCurrentAcquisitionState(lState);

    AdaptationTest lAdaptationTests = new AdaptationTest(10, 0.1);

    lAdaptator.add(lAdaptationTests);

    assertEquals(0, lAdaptator.estimateStepInSeconds(),0.001);

    while (lAdaptator.step())
    {
      double lEstimatedTimeInSeconds = lAdaptator.estimateStepInSeconds();
      System.out.format("step: estimated-time=%gs \n", lEstimatedTimeInSeconds);
    }

  }

}
