package clearcontrol.hardware.stages;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import clearcontrol.core.concurrent.timing.Waiting;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;

public abstract class StageDeviceBase extends VirtualDevice implements
																																StageDeviceInterface,
																																Waiting
{
	protected ArrayList<Variable<Boolean>> mEnableVariables,
			mReadyVariables, mHomingVariables, mStopVariables,
			mResetVariables;
	protected ArrayList<Variable<Double>> mTargetPositionVariables,mCurrentPositionVariables,
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

		mTargetPositionVariables = new ArrayList<>();
		mCurrentPositionVariables = new ArrayList<>();
		mMinPositionVariables = new ArrayList<>();
		mMaxPositionVariables = new ArrayList<>();
	}

	public abstract StageType getStageType();

	@Override
	public void setTargetPosition(int pIndex, double pPosition)
	{
		mTargetPositionVariables.get(pIndex).set(pPosition);
	}
	
	@Override
	public double getTargetPosition(int pIndex)
	{
		return mTargetPositionVariables.get(pIndex).get();
	}
	
	@Override
	public double getCurrentPosition(int pIndex)
	{
		return mCurrentPositionVariables.get(pIndex).get();
	}

	@Override
	public void reset(int pIndex)
	{
		mResetVariables.get(pIndex).setEdge(false, true);
	}

	@Override
	public void home(int pIndex)
	{
		mHomingVariables.get(pIndex).setEdge(false, true);
	}

	@Override
	public void enable(int pIndex)
	{
		mEnableVariables.get(pIndex).setEdge(false, true);
	}

	@Override
	public int getNumberOfDOFs()
	{
		return mTargetPositionVariables.size();
	}

	@Override
	public Boolean waitToBeReady(	int pIndex,
																long pTimeOut,
																TimeUnit pTimeUnit)
	{
		System.out.println("waiting...");
		return waitFor(	pTimeOut,
										pTimeUnit,
										() -> mReadyVariables.get(pIndex).get());
	}

	@Override
	public Variable<Double> getTargetPositionVariable(int pIndex)
	{
		return mTargetPositionVariables.get(pIndex);
	}

	@Override
	public Variable<Double> getCurrentPositionVariable(int pDOFIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Variable<Double> getMinPositionVariable(int pIndex)
	{
		return mMinPositionVariables.get(pIndex);
	}

	@Override
	public Variable<Double> getMaxPositionVariable(int pIndex)
	{
		return mMaxPositionVariables.get(pIndex);
	}

	@Override
	public Variable<Boolean> getEnableVariable(int pIndex)
	{
		return mEnableVariables.get(pIndex);
	}

	@Override
	public Variable<Boolean> getReadyVariable(int pIndex)
	{
		return mReadyVariables.get(pIndex);
	}

	@Override
	public Variable<Boolean> getHomingVariable(int pIndex)
	{
		return mHomingVariables.get(pIndex);
	}

	@Override
	public Variable<Boolean> getStopVariable(int pIndex)
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
