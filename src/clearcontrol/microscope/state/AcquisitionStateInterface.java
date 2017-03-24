package clearcontrol.microscope.state;

import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.microscope.MicroscopeInterface;

/**
 * Acquisition state interface
 *
 * @param <M>
 *          microscope type
 * @author royer
 */
public interface AcquisitionStateInterface<M extends MicroscopeInterface>
                                          extends
                                          NameableInterface,
                                          HasChangeListenerInterface<AcquisitionStateInterface<M>>

{

  /**
   * Aplies microscope state to a given microscope
   * 
   * @param pMicroscope
   *          microscope
   */
  void applyAcquisitionState(M pMicroscope);

}
