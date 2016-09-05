package clearcontrol.hardware.lasers.devices.sim;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.lasers.LaserDeviceBase;
import clearcontrol.hardware.lasers.LaserDeviceInterface;

public class LaserDeviceSimulator extends LaserDeviceBase	implements
																													LaserDeviceInterface,
																													AsynchronousSchedulerServiceAccess,
																													LoggingInterface,
																													SimulationDeviceInterface
{

	public LaserDeviceSimulator(String pDeviceName,
															int pDeviceId,
															int pWavelengthInNanoMeter,
															double pMaxPowerInMilliWatt)
	{
		super(pDeviceName);

		mDeviceIdVariable = new Variable<Integer>("DeviceId", pDeviceId);

		mWavelengthVariable = new Variable<Integer>("WavelengthInNanoMeter",
																								pWavelengthInNanoMeter);

		mSpecInMilliWattPowerVariable = new Variable<Number>(	"SpecPowerInMilliWatt",
																													pMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new Variable<Number>("MaxPowerInMilliWatt",
																												pMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new Variable<Integer>("OperatingMode",
																											0);

		mPowerOnVariable = new Variable<Boolean>("PowerOn", false);
		mPowerOnVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info(getName() + ":New power on state: " + n);
		});

		mLaserOnVariable = new Variable<Boolean>("LaserOn", false);
		mLaserOnVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info(getName() + ":New laser on state: " + n);
		});

		mWorkingHoursVariable = new Variable<Integer>("WorkingHours", 0);

		mTargetPowerInMilliWattVariable = new Variable<Number>(	"TargetPowerMilliWatt",
																														0.0);
		mTargetPowerInMilliWattVariable.addSetListener((o, n) -> {
			if (isSimLogging())
				info(getName() + ":New target power: " + n);
		});

		mCurrentPowerInMilliWattVariable = new Variable<Number>("CurrentPowerInMilliWatt",
																														0.0);

		Runnable lRunnable = () -> {
			double lCurrentValue = mCurrentPowerInMilliWattVariable.get()
																															.doubleValue();
			double lTargetValue = mTargetPowerInMilliWattVariable.get()
																														.doubleValue();

			if (mLaserOnVariable.get() && mTargetPowerInMilliWattVariable.get()
																																		.doubleValue() > 0)

				mCurrentPowerInMilliWattVariable.set(0.8 * lCurrentValue
																							+ 0.2
																							* lTargetValue
																							+ Math.random()
																							* 5);
			else
				mCurrentPowerInMilliWattVariable.set(0.8 * lCurrentValue);
		};

		scheduleAtFixedRate(lRunnable, 200, TimeUnit.MILLISECONDS);

	}

}
