package rtlib.serialdevice.laser.hub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import rtlib.core.device.LaserDevice;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.doublev.DoubleVariable;

public class LasertHubDevice extends NamedVirtualDevice
{

	ArrayList<LaserDevice> mAddedLaserDeviceList = new ArrayList<LaserDevice>();
	HashMap<Integer, LaserDevice> mWavelengthToOpenedLaserDeviceMap = new HashMap<Integer, LaserDevice>();

	public LasertHubDevice()
	{
		super("LasertHubDevice");
	}

	public void addLaser(final LaserDevice pLaserDevice)
	{
		mAddedLaserDeviceList.add(pLaserDevice);
	}

	public Collection<LaserDevice> getLaserDeviceList()
	{
		return mWavelengthToOpenedLaserDeviceMap.values();
	}

	public LaserDevice getLaserDeviceByWavelength(final int pWavelengthInNanometer)
	{
		return mWavelengthToOpenedLaserDeviceMap.get(pWavelengthInNanometer);
	}

	public DoubleVariable getOnVariableByWavelength(final int pWaveLengthInNanometer)
	{
		LaserDevice lLaserDeviceByWavelength = getLaserDeviceByWavelength(pWaveLengthInNanometer);
		if (lLaserDeviceByWavelength == null)
			return null;
		return lLaserDeviceByWavelength.getLaserOnVariable();
	}

	public DoubleVariable getTargetPowerInMilliWattVariableByWavelength(final int pWavelengthInNanometer)
	{
		LaserDevice lLaserDeviceByWavelength = getLaserDeviceByWavelength(pWavelengthInNanometer);
		if (lLaserDeviceByWavelength == null)
			return null;
		return lLaserDeviceByWavelength.getTargetPowerInMilliWattVariable();
	}

	public DoubleVariable getCurrentPowerInMilliWattVariableByWavelength(final int pWavelengthInNanometer)
	{
		LaserDevice lLaserDeviceByWavelength = getLaserDeviceByWavelength(pWavelengthInNanometer);
		if (lLaserDeviceByWavelength == null)
			return null;
		return lLaserDeviceByWavelength.getCurrentPowerInMilliWattVariable();
	}

	@Override
	public boolean open()
	{
		boolean lAllLasersOpen = true;
		// Parallel
		for (final LaserDevice lLaserDevice : mAddedLaserDeviceList)
		{
			final boolean lLaserDeviceOpened = lLaserDevice.open();
			lAllLasersOpen &= lLaserDeviceOpened;
			if (lLaserDeviceOpened)
			{
				final int lWavelengthInNanoMeter = lLaserDevice.getWavelengthInNanoMeter();
				mWavelengthToOpenedLaserDeviceMap.put(lWavelengthInNanoMeter,
																							lLaserDevice);
			}
			else
			{
				System.out.println(LasertHubDevice.class.getSimpleName() + ": could not open: "
														+ lLaserDevice.getDeviceName());
			}
		}
		return lAllLasersOpen;
	}

	@Override
	public boolean start()
	{
		boolean lAllLasersStarted = true;
		for (final Entry<Integer, LaserDevice> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
		{
			final LaserDevice lLaserDevice = lEntry.getValue();
			lAllLasersStarted &= lLaserDevice.start();
		}
		return lAllLasersStarted;
	}

	@Override
	public boolean stop()
	{
		boolean lAllLasersStopped = true;
		for (final Entry<Integer, LaserDevice> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
		{
			final LaserDevice lLaserDevice = lEntry.getValue();
			lAllLasersStopped &= lLaserDevice.stop();
		}
		return lAllLasersStopped;
	}

	@Override
	public boolean close()
	{
		boolean lAllLasersClosed = true;
		for (final Entry<Integer, LaserDevice> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
		{
			final LaserDevice lLaserDevice = lEntry.getValue();
			lAllLasersClosed &= lLaserDevice.close();
		}
		return lAllLasersClosed;
	}

	public void setTargetPowerInMilliWatt(final double pTargetPowerInMilliWat)
	{
		for (final Entry<Integer, LaserDevice> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
		{
			final LaserDevice lLaserDevice = lEntry.getValue();
			lLaserDevice.setTargetPowerInMilliWatt(pTargetPowerInMilliWat);
		}
	}

	public void setTargetPowerInPercent(final double pTargetPowerInPercent)
	{
		for (final Entry<Integer, LaserDevice> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
		{
			final LaserDevice lLaserDevice = lEntry.getValue();
			lLaserDevice.setTargetPowerInPercent(pTargetPowerInPercent);
		}
	}

	public double[] getCurrentPowersInMilliWatt()
	{
		final double[] lCurrentPowersInMilliWatt = new double[mWavelengthToOpenedLaserDeviceMap.size()];
		int i = 0;
		for (final Entry<Integer, LaserDevice> lEntry : mWavelengthToOpenedLaserDeviceMap.entrySet())
		{
			final LaserDevice lLaserDevice = lEntry.getValue();
			lCurrentPowersInMilliWatt[i++] = lLaserDevice.getCurrentPowerInMilliWatt();
		}
		return lCurrentPowersInMilliWatt;
	}

}
