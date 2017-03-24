package clearcontrol.microscope.timelapse;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

/**
 * Interface implemented by all timelapse devices.
 *
 * @author royer
 */
public interface TimelapseInterface
{

  /**
   * Returns timelapse timer <<<<<<< Updated upstream
   * 
   * ======= >>>>>>> Stashed changes
   * 
   * @return timelapse timer
   */
  Variable<TimelapseTimerInterface> getTimelapseTimer();

}
