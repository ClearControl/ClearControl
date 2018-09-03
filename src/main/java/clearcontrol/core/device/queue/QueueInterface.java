package clearcontrol.core.device.queue;

/**
 * Interface for Queues
 *
 * @author royer
 */
public interface QueueInterface
{

  /**
   * Clears the queue
   */
  void clearQueue();

  /**
   * adds the objects state to the state queue
   */
  void addCurrentStateToQueue();

  /**
   * Finalize queue. This should be called after all states have been enqueued.
   * This method can be used if anything need sto be done after all states have
   * been added.
   */
  void finalizeQueue();

  /**
   * Returns the state queue current length.
   * 
   * @return current state queue length
   */
  int getQueueLength();

}
