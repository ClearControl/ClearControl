package clearcontrol.hardware.signalgen.score;

import java.util.concurrent.locks.ReentrantLock;

import clearcontrol.device.update.UpdatableInterface;

public interface CompiledScoreInterface extends UpdatableInterface
{
  ReentrantLock getLock();
}
