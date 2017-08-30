package clearcontrol.devices.signalgen.score;

import java.util.concurrent.locks.ReentrantLock;

import clearcontrol.core.device.update.UpdatableInterface;

/**
 * Compiled score interface
 *
 * @author royer
 */
public interface CompiledScoreInterface extends UpdatableInterface
{
  /**
   * Returns lock
   * 
   * @return lock
   */
  ReentrantLock getLock();
}
