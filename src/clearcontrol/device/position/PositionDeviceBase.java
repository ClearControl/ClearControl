package clearcontrol.device.position;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NamedVirtualDevice;

public abstract class PositionDeviceBase extends NamedVirtualDevice	implements
																																		PositionDeviceInterface
{
	protected Variable<Integer> mPositionVariable = null;
	protected int[] mValidPositions;
	private ConcurrentHashMap<Integer, String> mPositionToNameMap;

	public PositionDeviceBase(String pDeviceName, int[] pValidPositions)
	{
		super(pDeviceName);
		mValidPositions = pValidPositions;
		mPositionVariable = new Variable<Integer>("Position",
																							pValidPositions[0]);
		mPositionToNameMap = new ConcurrentHashMap<>();

		for (int lPosition : mValidPositions)
		{
			mPositionToNameMap.put(lPosition, "" + lPosition);
		}
	}

	public PositionDeviceBase(String pDevicePath,
														String pDeviceName,
														int pDeviceindex)
	{
		super(pDeviceName);
		ArrayList<String> lList = MachineConfiguration.getCurrentMachineConfiguration()
																									.getList(pDevicePath + "."
																														+ pDeviceName.toLowerCase());

		mValidPositions = new int[lList.size()];
		for (int i = 0; i < mValidPositions.length; i++)
		{
			mValidPositions[i] = i;
			mPositionToNameMap.put(i, lList.get(i));
		}

		mPositionVariable = new Variable<Integer>("Position",
																							mValidPositions[0]);

	}

	public void setPositionName(int pPositionIndex, String pPositionName)
	{
		mPositionToNameMap.put(pPositionIndex, pPositionName);
	}
	
	@Override
	public String getPositionName(int pPositionIndex)
	{
		return mPositionToNameMap.get(pPositionIndex);
	}

	@Override
	public final Variable<Integer> getPositionVariable()
	{
		return mPositionVariable;
	}

	@Override
	public int getPosition()
	{
		return mPositionVariable.get();
	}

	@Override
	public void setPosition(final int pPosition)
	{
		mPositionVariable.set(pPosition);
	}

	@Override
	public int[] getValidPositions()
	{
		return mValidPositions;
	}



}
