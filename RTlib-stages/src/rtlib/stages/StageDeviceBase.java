package rtlib.stages;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class StageDeviceBase extends NamedVirtualDevice implements
																																StageDeviceInterface,
																																Waiting
{
	protected ArrayList<BooleanVariable> mEnableVariables,
			mReadyVariables, mHomingVariables, mStopVariables,
			mResetVariables;
	protected ArrayList<DoubleVariable> mPositionVariables,
			mMinPositionVariables, mMaxPositionVariables;

	protected final BiMap<Integer, String> mIndexToNameMap = HashBiMap.create();

	public StageDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
		mEnableVariables = new ArrayList<>();
		mReadyVariables = new ArrayList<>();
		mHomingVariables = new ArrayList<>();
		mStopVariables = new ArrayList<>();
		mResetVariables = new ArrayList<>();

		mPositionVariables = new ArrayList<>();
		mMinPositionVariables = new ArrayList<>();
		mMaxPositionVariables = new ArrayList<>();
	}

	@Override
	public double getCurrentPosition(int pIndex)
	{
		return mPositionVariables.get(pIndex).getValue();
	}

	@Override
	public void reset(int pIndex)
	{
		mResetVariables.get(pIndex).setEdge(true);
	}

	@Override
	public void home(int pIndex)
	{
		mHomingVariables.get(pIndex).setEdge(true);
	}

	@Override
	public void enable(int pIndex)
	{
		mEnableVariables.get(pIndex).setEdge(true);
	}

	@Override
	public void goToPosition(int pIndex, double pValue)
	{
		mPositionVariables.get(pIndex).setValue(pValue);
	}

	@Override
	public int getNumberOfDOFs()
	{
		return mPositionVariables.size();
	}

	@Override
	public Boolean waitToBeReady(	int pIndex,
																int pTimeOut,
																TimeUnit pTimeUnit)
	{
		System.out.println("waiting...");
		return waitFor(	pTimeOut,
										pTimeUnit,
										() -> mReadyVariables.get(pIndex)
																					.getBooleanValue());
	}

	@Override
	public DoubleVariable getPositionVariable(int pIndex)
	{
		return mPositionVariables.get(pIndex);
	}

	@Override
	public DoubleVariable getMinPositionVariable(int pIndex)
	{
		return mMinPositionVariables.get(pIndex);
	}

	@Override
	public DoubleVariable getMaxPositionVariable(int pIndex)
	{
		return mMaxPositionVariables.get(pIndex);
	}

	@Override
	public DoubleVariable getEnableVariable(int pIndex)
	{
		return mEnableVariables.get(pIndex);
	}

	@Override
	public DoubleVariable getReadyVariable(int pIndex)
	{
		return mReadyVariables.get(pIndex);
	}

	@Override
	public DoubleVariable getHomingVariable(int pIndex)
	{
		return mHomingVariables.get(pIndex);
	}

	@Override
	public BooleanVariable getStopVariable(int pIndex)
	{
		return mStopVariables.get(pIndex);
	}

	@Override
	public int getDOFIndexByName(String pName)
	{
		return mIndexToNameMap.inverse().get(pName);
	}

	@Override
	public String getDOFNameByIndex(int pIndex)
	{
		return mIndexToNameMap.get(pIndex);
	}

}
