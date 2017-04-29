package clearcontrol.microscope.state;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.microscope.MicroscopeInterface;

/**
 * Acquisition state interface
 *
 * @param <M>
 *          microscope type
 * @param <Q>
 *          queue type
 * @author royer
 * 
 */
public interface AcquisitionStateInterface<M extends MicroscopeInterface<Q>, Q extends QueueInterface>
                                          extends
                                          NameableInterface,
                                          HasChangeListenerInterface<AcquisitionStateInterface<M, Q>>

{

  /**
   * Executes (asynchronously) any actions that cannot be queue and that needs
   * to happen before an acquisition (such as moving the stage, ...)
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          time unit.
   * 
   */
  void prepareAcquisition(long pTimeOut, TimeUnit pTimeUnit);

  /**
   * Returns the microscope queue for this state
   * 
   * @return queue
   */
  Q getQueue();

}
