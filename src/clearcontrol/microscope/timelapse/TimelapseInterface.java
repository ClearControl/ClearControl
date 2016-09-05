package clearcontrol.microscope.timelapse;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

public interface TimelapseInterface
{

	Variable<TimelapseTimerInterface> getTimelapseTimer();

}
