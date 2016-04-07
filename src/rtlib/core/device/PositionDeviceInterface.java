package rtlib.core.device;

import rtlib.core.variable.types.objectv.ObjectVariable;


public interface PositionDeviceInterface
{
	ObjectVariable<Integer> getPositionVariable();

	int getPosition();

	void setPosition(int pPosition);

	int[] getValidPositions();
}
