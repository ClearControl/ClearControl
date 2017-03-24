package clearcontrol.core.device.queue;

import clearcontrol.core.variable.queue.VariableStateQueues;

/**
 * Interface implemented by classes that can mannage variable state queues.
 *
 * @author royer
 */
public interface HasVariableStateQueues
{

  /**
   * Returns the variables state queues.
   * 
   * @return variable state queues.
   */
  VariableStateQueues getVariableStateQueues();

}
