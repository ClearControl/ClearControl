package clearcontrol.microscope.state;

import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.microscope.MicroscopeInterface;

public interface AcquisitionStateInterface<M extends MicroscopeInterface>
                                          extends
                                          NameableInterface,
                                          HasChangeListenerInterface<AcquisitionStateInterface<M>>

{

  void applyAcquisitionState(M pMicroscope);

}
