package clearcontrol.device.queue;

public interface QueueProviderUsingDeviceInterface
{
	void setQueueProvider(QueueProvider<?> pQueueProvider);

	void buildQueueFromProvider();
}
