package clearcontrol.microscope.state;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.variable.bounded.BoundedVariable;
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
   * @param pName
   * @return copy of this state
   */
  AcquisitionStateInterface<M, Q> duplicate(String pName);

  /**
   * Returns the variable holding the exposure in seconds for this acquisition
   * state
   * 
   * @return exposure in seconds variable
   */
  BoundedVariable<Number> getExposureInSecondsVariable();

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
