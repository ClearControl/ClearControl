package rtlib.core.device.queue;

import java.util.concurrent.Future;

public interface StateQueueDeviceInterface
{
	void clearQueue();

	void addCurrentStateToQueueNotCounting();

	void addCurrentStateToQueue();

	void setQueueProvider(QueueProvider<?> pQueueProvider);

	void ensureQueueIsUpToDate();

	int getQueueLength();

	Future<Boolean> playQueue();

}
