package rtlib.hardware.stages.devices.sim;

import rtlib.core.variable.Variable;
import rtlib.hardware.stages.StageDeviceBase;
import rtlib.hardware.stages.StageDeviceInterface;
import rtlib.hardware.stages.StageType;

public class StageDeviceSimulator extends StageDeviceBase	implements
																													StageDeviceInterface
{

	private StageType mStageType;

	public StageDeviceSimulator(String pDeviceName, StageType pStageType)
	{
		super(pDeviceName);
		mStageType = pStageType;
	}

	@Override
	public StageType getStageType()
	{
		return mStageType;
	}

	public void addDOF(String pDOFName, double pMin, double pMax)
	{
		final int lDOFIndex = mIndexToNameMap.size();

		mIndexToNameMap.put(lDOFIndex, pDOFName);

		mEnableVariables.add(new Variable<Boolean>(	"Enable" + pDOFName,
																								false));
		for (Variable<Boolean> lEnableVariable : mEnableVariables)
			lEnableVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new enable state: " + n);
			});

		mReadyVariables.add(new Variable<Boolean>("Ready" + pDOFName,
																							false));
		for (Variable<Boolean> lReadyVariable : mReadyVariables)
			lReadyVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new ready state: " + n);
			});

		mHomingVariables.add(new Variable<Boolean>(	"Homing" + pDOFName,
																								false));
		for (Variable<Boolean> lHomingVariable : mHomingVariables)
			lHomingVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new homing state: " + n);
			});

		mStopVariables.add(new Variable<Boolean>("Stop" + pDOFName, false));
		for (Variable<Boolean> lStopVariable : mStopVariables)
			lStopVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new stop state: " + n);
			});

		mResetVariables.add(new Variable<Boolean>("Reset" + pDOFName,
																							false));
		for (Variable<Boolean> lResetVariable : mResetVariables)
			lResetVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new reset state: " + n);
			});

		mTargetPositionVariables.add(new Variable<Double>("TargetPosition" + pDOFName,
																											0.0));
		for (Variable<Double> lTargetPositionVariable : mTargetPositionVariables)
			lTargetPositionVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new target position: " + n);
			});

		mCurrentPositionVariables.add(new Variable<Double>(	"CurrentPosition" + pDOFName,
																												0.0));
		for (Variable<Double> lCurrentPositionVariable : mCurrentPositionVariables)
			lCurrentPositionVariable.addSetListener((o, n) -> {
				System.out.println(getName() + ": new current position: " + n);
			});

		mMinPositionVariables.add(new Variable<Double>(	"MinPosition" + pDOFName,
																										pMin));
		mMaxPositionVariables.add(new Variable<Double>(	"MaxPosition" + pDOFName,
																										pMax));

	}

	public void addXYZRDOFs()
	{
		addDOF("X", -100, 100);
		addDOF("Y", -110, 110);
		addDOF("Z", -120, 120);
		addDOF("R", 0, 360);
	}

}
