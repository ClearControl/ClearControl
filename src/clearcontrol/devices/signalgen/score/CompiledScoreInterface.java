package clearcontrol.devices.signalgen.score;

import java.util.concurrent.locks.ReentrantLock;

import clearcontrol.core.device.update.UpdatableInterface;

public interface CompiledScoreInterface extends UpdatableInterface
{
  ReentrantLock getLock();
}
