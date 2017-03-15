package clearcontrol.microscope.timelapse.timer;

import java.util.concurrent.TimeUnit;

public interface TimelapseTimerInterface
{

  public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit);

  public boolean enoughTimeFor(long pTimeNeeded,
                               long pReservedTime,
                               TimeUnit pTimeUnit);

  public void waitToAcquire(long pTimeout, TimeUnit pTimeUnit);

  public long getLastAcquisitionTime(TimeUnit pTimeUnit);

  void notifyAcquisition();

}
