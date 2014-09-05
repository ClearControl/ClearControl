package rtlib.core.device;

import rtlib.core.variable.doublev.DoubleVariable;

public interface PositioningDevice
{
	int getNumberOfDOFs();

	DoubleVariable getPositionVariable(int pIndex);
}
