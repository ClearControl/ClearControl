package rtlib.stages.devices.sim;

import rtlib.core.variable.ObjectVariable;
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

		mEnableVariables.add(new ObjectVariable<Boolean>(	"Enable" + pDOFName,
																											false));
		mReadyVariables.add(new ObjectVariable<Boolean>("Ready" + pDOFName,
																										false));
		mHomingVariables.add(new ObjectVariable<Boolean>(	"Homing" + pDOFName,
																											false));
		mStopVariables.add(new ObjectVariable<Boolean>(	"Stop" + pDOFName,
																										false));
		mResetVariables.add(new ObjectVariable<Boolean>("Reset" + pDOFName,
																										false));
		mPositionVariables.add(new ObjectVariable<Double>("Position" + pDOFName,
																											0.0));
		mMinPositionVariables.add(new ObjectVariable<Double>(	"MinPosition" + pDOFName,
																													pMin));
		mMinPositionVariables.add(new ObjectVariable<Double>(	"MaxPosition" + pDOFName,
																													pMax));

	}

}
