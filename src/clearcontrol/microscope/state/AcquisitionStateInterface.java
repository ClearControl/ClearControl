package clearcontrol.microscope.state;

import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.queue.RealTimeQueueInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;

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
public interface AcquisitionStateInterface<M extends MicroscopeInterface<Q>, Q extends RealTimeQueueInterface>
                                          extends
                                          NameableInterface,
                                          HasChangeListenerInterface<AcquisitionStateInterface<M, Q>>

{

  /**
   * Updates the queue for this state using the current microscope settings and
   * state details
   * 
   * @param pMicroscope
   *          microscope
   */
  void updateQueue(M pMicroscope);

  /**
   * Returns the microscope queue for this state
   * 
   * @return queue
   */
  Q getQueue();


}
