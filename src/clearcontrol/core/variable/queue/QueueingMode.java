package clearcontrol.core.variable.queue;

/**
 * Queueing mode. For some variable one can choose that the all enqueued states
 * should be one and the same (constant).
 *
 * @author royer
 */
public enum QueueingMode
{
 /**
  * Normal queueing mode, variables can take any value
  */
 Normal,
 /**
  * Constant queuing mode. All variable values in the queue must be one and the
  * same.
  */
 Constant
}
