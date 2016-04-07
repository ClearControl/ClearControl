package rtlib.lasers.devices.sim;

import rtlib.core.variable.ObjectVariable;
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

		mDeviceIdVariable = new ObjectVariable<Integer>("DeviceId",
																										pDeviceId);

		mWavelengthVariable = new ObjectVariable<Integer>("WavelengthInNanoMeter",
																											pWavelengthInNanoMeter);

		mSpecInMilliWattPowerVariable = new ObjectVariable<Number>(	"SpecPowerInMilliWatt",
																																pMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new ObjectVariable<Number>("MaxPowerInMilliWatt",
																															pMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new ObjectVariable<Integer>("OperatingMode",
																														0);

		mPowerOnVariable = new ObjectVariable<Boolean>("PowerOn", false);

		mLaserOnVariable = new ObjectVariable<Boolean>("LaserOn", false);

		mWorkingHoursVariable = new ObjectVariable<Integer>("WorkingHours",
																												0);

		mTargetPowerInMilliWattVariable = new ObjectVariable<Number>(	"TargetPowerMilliWatt",
																																	0.0);

		mCurrentPowerInMilliWattVariable = new ObjectVariable<Number>("CurrentPowerInMilliWatt",
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
