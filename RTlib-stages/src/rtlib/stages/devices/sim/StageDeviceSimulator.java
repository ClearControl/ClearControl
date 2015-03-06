package rtlib.stages.devices.sim;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
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

		mEnableVariables.add(new BooleanVariable(	"Enable" + pDOFName,
																							false));
		mReadyVariables.add(new BooleanVariable("Ready" + pDOFName, false));
		mHomingVariables.add(new BooleanVariable(	"Homing" + pDOFName,
																							false));
		mStopVariables.add(new BooleanVariable("Stop" + pDOFName, false));
		mResetVariables.add(new BooleanVariable("Reset" + pDOFName, false));
		mPositionVariables.add(new DoubleVariable("Position" + pDOFName,
																							0));
		mMinPositionVariables.add(new DoubleVariable(	"MinPosition" + pDOFName,
																									pMin));
		mMinPositionVariables.add(new DoubleVariable(	"MaxPosition" + pDOFName,
																									pMax));

	}

}
