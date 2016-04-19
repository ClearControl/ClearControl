package rtlib.device.position;

import rtlib.core.variable.Variable;

public interface PositionDeviceInterface
{
	Variable<Integer> getPositionVariable();

	int getPosition();

	void setPosition(int pPositionIndex);

	int[] getValidPositions();

	void setPositionName(int pPositionIndex, String pPositionName);

	String getPositionName(int pPositionIndex);
}
