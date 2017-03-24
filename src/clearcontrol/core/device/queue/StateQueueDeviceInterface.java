package clearcontrol.core.device.queue;

import java.util.concurrent.Future;

/**
 * Interface implemented by all devices that suppport a state queue.
 *
 * @author royer
 */
public interface StateQueueDeviceInterface extends StateQueueInterface
{

  /**
   * Plays back the state queue.
   * 
   * @return future that represents the execution of the queue.
   */
  Future<Boolean> playQueue();

}
