package clearcontrol.device.task;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.log.LoggingInterface;

public abstract class LoopTaskDevice extends TaskDevice	implements
																												LoggingInterface,
																												WaitingInterface
{

	public LoopTaskDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public void run()
	{
		while (!getStopSignalBooleanVariable().get())
		{

			boolean lResult = loop();

			if (!lResult)
				stopTask();
		}
	};

	public abstract boolean loop();

}
