package clearcontrol.microscope.lightsheet.timelapse;

import java.util.concurrent.TimeUnit;

public interface TimelapseInterface
{

	public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit);

	public boolean enoughTimeFor(	long pTimeNeeded,
																long pReservedTime,
																TimeUnit pTimeUnit);

	public void waitToAcquire(long pTimeStamp, TimeUnit pTimeUnit);

	public long getLastAcquisitionTime(TimeUnit pTimeUnit);

	void notifyAcquisition();

}
