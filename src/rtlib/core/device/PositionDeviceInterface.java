package rtlib.core.device;

import rtlib.core.variable.Variable;

public interface PositionDeviceInterface
{
	Variable<Integer> getPositionVariable();

	int getPosition();

	void setPosition(int pPosition);

	int[] getValidPositions();
}
