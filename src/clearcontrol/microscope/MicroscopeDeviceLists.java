package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.hardware.cameras.StackCameraDeviceInterface;

public class MicroscopeDeviceLists
{
	private final MicroscopeInterface mMicroscope;

	private final ArrayList<Object> mAllDeviceList = new ArrayList<Object>();
	private final ConcurrentHashMap<Object,Integer> mDeviceIndexMap = new ConcurrentHashMap<>();

	public MicroscopeDeviceLists(MicroscopeInterface pMicroscope)
	{
		mMicroscope = pMicroscope;
	}

	public <T> void addDevice(int pDeviceIndex, T pDevice)
	{
		mDeviceIndexMap.put(pDevice, pDeviceIndex);
		mAllDeviceList.add(pDevice);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getDevice(Class<T> pClass, int pIndex)
	{
		for(Object lDevice : mAllDeviceList)
			if(pClass.isInstance(lDevice))
				if(mDeviceIndexMap.get(lDevice)==pIndex)
					return (T) lDevice;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getDevices(Class<T> pClass)
	{
		ArrayList<T> lFoundDevices = new ArrayList<>();
		for(Object lDevice : mAllDeviceList)
			if(pClass.isInstance(lDevice))
				lFoundDevices.add((T) lDevice);
		
		return lFoundDevices;
	}
	
	@SuppressWarnings("unchecked")
	public <T> int getNumberOfDevices(Class<T> pClass)
	{
		int lCount=0;
		for(Object lDevice : mAllDeviceList)
			if(pClass.isInstance(lDevice))
				lCount++;
		
		return lCount;
	}

	public ArrayList<Object> getAllDeviceList()
	{
		return mAllDeviceList;
	}

	/**
	 * Interface method implementation
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder lBuilder = new StringBuilder();
		for (final Object lDevice : mAllDeviceList)
		{
			lBuilder.append(lDevice.toString());
			lBuilder.append("\n");
		}
		return lBuilder.toString();
	}





}
