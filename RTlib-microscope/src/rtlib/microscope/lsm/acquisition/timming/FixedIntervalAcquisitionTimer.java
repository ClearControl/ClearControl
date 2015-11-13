package rtlib.microscope.lsm.acquisition.timming;

import java.util.concurrent.TimeUnit;

public class FixedIntervalAcquisitionTimer extends AcquisitionTimerBase	implements
											AcquisitionTimerInterface
{

	public FixedIntervalAcquisitionTimer(	long pAcquisitionInterval,
											TimeUnit pTimeUnit)
	{
		super();
		setAcquisitionInterval(pAcquisitionInterval, pTimeUnit);
	}


}
