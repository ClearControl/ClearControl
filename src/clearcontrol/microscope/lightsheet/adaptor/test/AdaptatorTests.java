package clearcontrol.microscope.lightsheet.adaptor.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.microscope.lightsheet.adaptor.Adaptator;

import org.junit.Test;

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

    Adaptator<TestState> lAdaptator = new Adaptator<TestState>(null);
    lAdaptator.getNewAcquisitionStateVariable()
              .set(new TestState("initial state"));

    AdaptationTestModule lAdaptationTests =
                                          new AdaptationTestModule("A",
                                                                   10);

    lAdaptator.add(lAdaptationTests);

    assertEquals(0, lAdaptator.estimateNextStepInSeconds(), 0.001);

    while (lAdaptator.step())
    {
      double lEstimatedTimeInSeconds =
                                     lAdaptator.estimateNextStepInSeconds();
      System.out.format("step: estimated-time=%gs \n",
                        lEstimatedTimeInSeconds);
      ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
    }

  }

}
