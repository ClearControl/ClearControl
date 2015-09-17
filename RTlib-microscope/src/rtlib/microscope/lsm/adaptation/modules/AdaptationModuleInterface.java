package rtlib.microscope.lsm.adaptation.modules;

import rtlib.microscope.lsm.adaptation.LSMAdaptator;

public interface AdaptationModuleInterface
{

	void setAdaptator(LSMAdaptator pLSMAdaptator);

	LSMAdaptator getAdaptator();

	void setPriority(int pPriority);

	int getPriority();

	boolean step();

	boolean isReady();

	void reset();

}
