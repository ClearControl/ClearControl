package clearcontrol.core.device.queue;

import java.util.concurrent.Future;

/**
 * Interface implemented by all devices that suppport state queues.
 *
 * @author royer
 * @param <Q>
 *          queue type
 */
public interface RealTimeQueueDeviceInterface<Q extends RealTimeQueueInterface>

{

  /**
   * Creates a queue
   * 
   * @return queue
   */
  Q requestQueue();

  /**
   * Plays back a state queue.
   * 
   * @param pQueue
   * 
   * @return future that represents the execution of the queue.
   */
  Future<Boolean> playQueue(Q pQueue);

}
