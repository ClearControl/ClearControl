package rtlib.stages.devices.sim;

import rtlib.core.variable.Variable;
import rtlib.stages.StageDeviceBase;
import rtlib.stages.StageDeviceInterface;

public class StageDeviceSimulator extends StageDeviceBase	implements
																													StageDeviceInterface
{

	public StageDeviceSimulator(String pDeviceName)
	{
		super(pDeviceName);
	}

	public void addDOF(String pDOFName, double pMin, double pMax)
	{
		final int lDOFIndex = mIndexToNameMap.size();

		mIndexToNameMap.put(lDOFIndex, pDOFName);

		mEnableVariables.add(new Variable<Boolean>(	"Enable" + pDOFName,
																											false));
		mReadyVariables.add(new Variable<Boolean>("Ready" + pDOFName,
																										false));
		mHomingVariables.add(new Variable<Boolean>(	"Homing" + pDOFName,
																											false));
		mStopVariables.add(new Variable<Boolean>(	"Stop" + pDOFName,
																										false));
		mResetVariables.add(new Variable<Boolean>("Reset" + pDOFName,
																										false));
		mPositionVariables.add(new Variable<Double>("Position" + pDOFName,
																											0.0));
		mMinPositionVariables.add(new Variable<Double>(	"MinPosition" + pDOFName,
																													pMin));
		mMinPositionVariables.add(new Variable<Double>(	"MaxPosition" + pDOFName,
																													pMax));

	}

}
