package clearcontrol.microscope.timelapse.timer.fixed;

import java.util.concurrent.TimeUnit;

import clearcontrol.microscope.timelapse.timer.TimelapseTimerBase;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

/**
 * Fixed interval timelapse timer
 *
 * @author royer
 */
public class FixedIntervalTimelapseTimer extends TimelapseTimerBase
                                         implements
                                         TimelapseTimerInterface
{

  /**
   * Instanciates a fixed interval timelapse timer
   */
  public FixedIntervalTimelapseTimer()
  {
    super(1, TimeUnit.SECONDS);
  }

}
