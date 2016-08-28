package clearcontrol.hardware.stages.devices.sim;

import static java.lang.Math.abs;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.stages.StageDeviceBase;
import clearcontrol.hardware.stages.StageDeviceInterface;
import clearcontrol.hardware.stages.StageType;

public class StageDeviceSimulator extends StageDeviceBase	implements
																													StageDeviceInterface,
																													SimulationDeviceInterface,
																													AsynchronousSchedulerServiceAccess,
																													LoggingInterface

{

	private static final double cEpsilon = 0;
	private StageType mStageType;

	public StageDeviceSimulator(String pDeviceName, StageType pStageType)
	{
		super(pDeviceName);
		mStageType = pStageType;

		scheduleAtFixedRate(() -> {
			moveToTarget();
		}, 50, TimeUnit.MILLISECONDS);
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
				if (isSimLogging())
					System.out.println(getName() + ": new enable state: " + n);
			});

		mReadyVariables.add(new Variable<Boolean>("Ready" + pDOFName,
																							false));
		for (Variable<Boolean> lReadyVariable : mReadyVariables)
			lReadyVariable.addSetListener((o, n) -> {
				if (isSimLogging())
					System.out.println(getName() + ": new ready state: " + n);
			});

		mHomingVariables.add(new Variable<Boolean>(	"Homing" + pDOFName,
																								false));
		for (Variable<Boolean> lHomingVariable : mHomingVariables)
			lHomingVariable.addSetListener((o, n) -> {
				if (isSimLogging())
					System.out.println(getName() + ": new homing state: " + n);
			});

		mStopVariables.add(new Variable<Boolean>("Stop" + pDOFName, false));
		for (Variable<Boolean> lStopVariable : mStopVariables)
			lStopVariable.addSetListener((o, n) -> {
				if (isSimLogging())
					System.out.println(getName() + ": new stop state: " + n);
			});

		mResetVariables.add(new Variable<Boolean>("Reset" + pDOFName,
																							false));
		for (Variable<Boolean> lResetVariable : mResetVariables)
			lResetVariable.addSetListener((o, n) -> {
				if (isSimLogging())
					System.out.println(getName() + ": new reset state: " + n);
			});

		mTargetPositionVariables.add(new Variable<Double>("TargetPosition" + pDOFName,
																											0.0));
		for (Variable<Double> lTargetPositionVariable : mTargetPositionVariables)
			lTargetPositionVariable.addSetListener((o, n) -> {
				if (isSimLogging())
					System.out.println(getName() + ": new target position: "
															+ n);
			});

		mCurrentPositionVariables.add(new Variable<Double>(	"CurrentPosition" + pDOFName,
																												0.0));
		for (Variable<Double> lCurrentPositionVariable : mCurrentPositionVariables)
			lCurrentPositionVariable.addSetListener((o, n) -> {
				if (isSimLogging())
					System.out.println(getName() + ": new current position: "
															+ n);
			});

		mMinPositionVariables.add(new Variable<Double>(	"MinPosition" + pDOFName,
																										pMin));
		mMaxPositionVariables.add(new Variable<Double>(	"MaxPosition" + pDOFName,
																										pMax));

	}

	private void moveToTarget()
	{
		for (int i = 0; i < getNumberOfDOFs(); i++)
		{
			double lTarget = mTargetPositionVariables.get(i).get();
			double lCurrent = mCurrentPositionVariables.get(i).get();
			double lError = lTarget - lCurrent;

			double lNewCurrent = lCurrent + 0.1 * Math.signum(lError);

			if (abs(lNewCurrent - lCurrent) < cEpsilon)
				mCurrentPositionVariables.get(i).set(lNewCurrent);
		}

	}

	public void addXYZRDOFs()
	{
		addDOF("X", -100, 100);
		addDOF("Y", -110, 110);
		addDOF("Z", -120, 120);
		addDOF("R", 0, 360);
	}

}
