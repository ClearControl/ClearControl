package rtlib.hardware.signalgen.score;

import java.util.concurrent.locks.ReentrantLock;

import rtlib.device.update.UpdatableInterface;

public interface CompiledScoreInterface extends UpdatableInterface
{
	ReentrantLock getLock();
}
