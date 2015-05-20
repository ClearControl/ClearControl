package rtlib.core.device.queue;

import java.util.concurrent.Future;

public interface StateQueueDeviceInterface
{
	void clearQueue();

	void addCurrentStateToQueueNotCounting();

	void addCurrentStateToQueue();

	void setQueueProvider(QueueProvider<?> pQueueProvider);

	void buildQueueFromProvider();

	int getQueueLength();

	Future<Boolean> playQueue();

}
