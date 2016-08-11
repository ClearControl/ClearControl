package clearcontrol.microscope.state;

import clearcontrol.device.name.NameableInterface;
import clearcontrol.microscope.MicroscopeInterface;

public interface AcquisitionStateInterface<M extends MicroscopeInterface> extends NameableInterface
																																					
{

	void applyState(M pMicroscope);

}
