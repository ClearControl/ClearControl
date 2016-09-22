package clearcontrol.hardware.stages.devices.sim;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

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

	private static final double cEpsilon = 0.5;
	private static final double cSpeed = 0.05;
	private StageType mStageType;

	public StageDeviceSimulator(String pDeviceName, StageType pStageType)
	{
		super(pDeviceName);
		mStageType = pStageType;

		scheduleAtFixedRate(() -> {
			try
			{
				moveToTarget();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}, 1, TimeUnit.MILLISECONDS);
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
		final Variable<Boolean> lEnableVariable = mEnableVariables.get(lDOFIndex);
		lEnableVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new enable state: " + n);
		});

		mReadyVariables.add(new Variable<Boolean>("Ready" + pDOFName,
																							true));
		final Variable<Boolean> lReadyVariable = mReadyVariables.get(lDOFIndex);
		lReadyVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new ready state: " + n);
		});

		mHomingVariables.add(new Variable<Boolean>(	"Homing" + pDOFName,
																								false));
		final Variable<Boolean> lHomingVariable = mHomingVariables.get(lDOFIndex);
		lHomingVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new homing state: " + n);
		});

		mStopVariables.add(new Variable<Boolean>("Stop" + pDOFName, false));
		final Variable<Boolean> lStopVariable = mStopVariables.get(lDOFIndex);
		lStopVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new stop state: " + n);
		});

		mResetVariables.add(new Variable<Boolean>("Reset" + pDOFName,
																							false));
		final Variable<Boolean> lResetVariable = mResetVariables.get(lDOFIndex);
		lResetVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new reset state: " + n);
		});

		mTargetPositionVariables.add(new Variable<Double>("TargetPosition" + pDOFName,
																											0.0));
		final Variable<Double> lTargetPositionVariable = mTargetPositionVariables.get(lDOFIndex);
		lTargetPositionVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new target position: " + n);
		});

		mCurrentPositionVariables.add(new Variable<Double>(	"CurrentPosition" + pDOFName,
																												0.0));
		final Variable<Double> lCurrentPositionVariable = mCurrentPositionVariables.get(lDOFIndex);
		lCurrentPositionVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info("new current position: " + n);
		});

		mMinPositionVariables.add(new Variable<Double>(	"MinPosition" + pDOFName,
																										pMin));
		mMaxPositionVariables.add(new Variable<Double>(	"MaxPosition" + pDOFName,
																										pMax));
		mGranularityPositionVariables.add(new Variable<Double>(	"GranularityPosition" + pDOFName,
																														0.1*cSpeed));


		for (int i = 0; i < getNumberOfDOFs(); i++)
		{
			final int fi = i;

			mTargetPositionVariables.get(fi).addSetListener((o, n) -> {
				mReadyVariables.get(fi).set(false);
			});

			mHomingVariables.get(fi).addEdgeListener(n -> {
				if (n && mEnableVariables.get(fi).get() && mReadyVariables.get(fi).get())
				{
					mTargetPositionVariables.get(fi).set(0.0);
					mReadyVariables.get(fi).set(false);
				}
			});

			mResetVariables.get(fi).addEdgeListener(n -> {
				if (n)
					mReadyVariables.get(fi).set(true);
			});

			mStopVariables.get(fi).addEdgeListener(n -> {
				if (n)
					{
						mReadyVariables.get(fi).set(true);
						mTargetPositionVariables.get(fi).set(mCurrentPositionVariables.get(fi).get());
					}
			});
		}
	}

	private void moveToTarget()
	{
		try
		{
			// int i=0;
			for (int i = 0; i < getNumberOfDOFs(); i++)
				if (mEnableVariables.get(i).get())
				{
					double lTarget = mTargetPositionVariables.get(i).get();
					double lCurrent = mCurrentPositionVariables.get(i).get();
					double lErrorLinear = lTarget - lCurrent;

					double lNewCurrent = lCurrent + cSpeed
																* signum(lErrorLinear);

					if (abs(lErrorLinear) > cEpsilon)
						mCurrentPositionVariables.get(i).set(lNewCurrent);
					else if (!mReadyVariables.get(i).get())
						mReadyVariables.get(i).set(true);

				}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
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
