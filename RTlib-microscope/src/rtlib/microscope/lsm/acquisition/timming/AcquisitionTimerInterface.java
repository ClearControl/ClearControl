package rtlib.microscope.lsm.acquisition.timming;

import java.util.concurrent.TimeUnit;

public interface AcquisitionTimerInterface
{

	public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit);

	public boolean enoughTimeFor(	long pTimeNeeded,
																long pReservedTime,
																TimeUnit pTimeUnit);

	public void waitToAcquire();

	public void notifyAcquisition(long pTimeStamp);

	public long getLastAcquisitionTime(TimeUnit pTimeUnit);

}
