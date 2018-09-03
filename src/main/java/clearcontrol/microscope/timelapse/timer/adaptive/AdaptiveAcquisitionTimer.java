package clearcontrol.microscope.timelapse.timer.adaptive;

import java.util.concurrent.TimeUnit;

import clearcontrol.microscope.timelapse.timer.TimelapseTimerBase;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

/**
 * Adaptive acquisition timer
 * 
 * TODO: finish
 *
 * @author royer
 */
public class AdaptiveAcquisitionTimer extends TimelapseTimerBase
                                      implements
                                      TimelapseTimerInterface
{

  /**
   * @param pAcquisitionInterval
   *          ..
   * @param pTimeUnit
   *          ..
   */
  public AdaptiveAcquisitionTimer(long pAcquisitionInterval,
                                  TimeUnit pTimeUnit)
  {
    super(pAcquisitionInterval, pTimeUnit);
    // TODO Auto-generated constructor stub
  }

}
