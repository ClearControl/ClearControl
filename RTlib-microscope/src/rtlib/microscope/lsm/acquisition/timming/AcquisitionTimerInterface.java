package rtlib.microscope.lsm.acquisition.timming;

import java.util.concurrent.TimeUnit;

public interface AcquisitionTimerInterface
{

	public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit);

	public boolean enoughTimeFor(long pTimeNeeded, TimeUnit pTimeUnit);

	public void waitToAcquire();

	public void notifyAcquisition();

	public long getLastAcquisitionTime(TimeUnit pTimeUnit);

}
