package rtlib.lasers.devices.sim;

import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
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

		mDeviceIdVariable = new DoubleVariable("DeviceId", pDeviceId);

		mWavelengthVariable = new DoubleVariable(	"WavelengthInNanoMeter",
													pWavelengthInNanoMeter);

		mSpecInMilliWattPowerVariable = new DoubleVariable(	"SpecPowerInMilliWatt",
															pMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new DoubleVariable(	"MaxPowerInMilliWatt",
															pMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new DoubleVariable(	"OperatingMode",
														0);

		mPowerOnVariable = new BooleanVariable("PowerOn", false);

		mLaserOnVariable = new BooleanVariable("LaserOn", false);

		mWorkingHoursVariable = new DoubleVariable("WorkingHours", 0);

		mTargetPowerInMilliWattVariable = new DoubleVariable(	"TargetPowerMilliWatt",
																0);

		mCurrentPowerInMilliWattVariable = new DoubleVariable(	"CurrentPowerInMilliWatt",
																0);

		mTargetPowerInMilliWattVariable.syncWith(mCurrentPowerInMilliWattVariable);
	}

}
