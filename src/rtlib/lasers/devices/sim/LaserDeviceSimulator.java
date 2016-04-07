package rtlib.lasers.devices.sim;

import rtlib.core.variable.Variable;
import rtlib.lasers.LaserDeviceBase;
import rtlib.lasers.LaserDeviceInterface;

public class LaserDeviceSimulator extends LaserDeviceBase	implements
																													LaserDeviceInterface
{

	public LaserDeviceSimulator(String pDeviceName,
															int pDeviceId,
															int pWavelengthInNanoMeter,
															double pMaxPowerInMilliWatt)
	{
		super(pDeviceName);

		mDeviceIdVariable = new Variable<Integer>("DeviceId",
																										pDeviceId);

		mWavelengthVariable = new Variable<Integer>("WavelengthInNanoMeter",
																											pWavelengthInNanoMeter);

		mSpecInMilliWattPowerVariable = new Variable<Number>(	"SpecPowerInMilliWatt",
																																pMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new Variable<Number>("MaxPowerInMilliWatt",
																															pMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new Variable<Integer>("OperatingMode",
																														0);

		mPowerOnVariable = new Variable<Boolean>("PowerOn", false);

		mLaserOnVariable = new Variable<Boolean>("LaserOn", false);

		mWorkingHoursVariable = new Variable<Integer>("WorkingHours",
																												0);

		mTargetPowerInMilliWattVariable = new Variable<Number>(	"TargetPowerMilliWatt",
																																	0.0);

		mCurrentPowerInMilliWattVariable = new Variable<Number>("CurrentPowerInMilliWatt",
																																	0.0);

		mTargetPowerInMilliWattVariable.syncWith(mCurrentPowerInMilliWattVariable);
	}

	/*
	 * timer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				if (now > lastTimerCall + 500_000_000l)
				{

					double v = (2 * RND.nextDouble() - 1);
					// v = (v > 0.5)? v * 0.05 + 1.0d : v * -0.05 + 1.0d;

					actualGauge.setValue(mwMarker.getValue() + v);
					lastTimerCall = now;
				}
			}
		};
	 */

}
