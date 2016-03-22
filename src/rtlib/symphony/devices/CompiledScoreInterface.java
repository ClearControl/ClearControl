package rtlib.symphony.devices;

import java.util.concurrent.locks.ReentrantLock;

import rtlib.core.device.UpdatableInterface;

public interface CompiledScoreInterface extends UpdatableInterface
{
	ReentrantLock getLock();
}
