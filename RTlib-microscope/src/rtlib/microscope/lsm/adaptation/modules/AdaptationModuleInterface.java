package rtlib.microscope.lsm.adaptation.modules;

import rtlib.microscope.lsm.adaptation.Adaptator;

public interface AdaptationModuleInterface
{

	void setAdaptator(Adaptator pLSMAdaptator);

	Adaptator getAdaptator();

	void setPriority(int pPriority);

	int getPriority();

	boolean step();

	boolean isReady();

	void reset();

}
