package rtlib.stages.hub;

import java.util.concurrent.TimeUnit;

import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.stages.StageDeviceInterface;

public class StageDeviceDOF
{
	private StageDeviceInterface mStageDeviceInterface;
	private int mDOFIndex;

	public StageDeviceDOF(StageDeviceInterface pStageDeviceInterface,
												int pDOFIndex)
	{
		super();
		setStageDeviceInterface(pStageDeviceInterface);
		setDOFIndex(pDOFIndex);
	}

	public StageDeviceInterface getStageDeviceInterface()
	{
		return mStageDeviceInterface;
	}

	public void setStageDeviceInterface(StageDeviceInterface pStageDeviceInterface)
	{
		mStageDeviceInterface = pStageDeviceInterface;
	}

	public int getDOFIndex()
	{
		return mDOFIndex;
	}

	public void setDOFIndex(int pDOFIndex)
	{
		mDOFIndex = pDOFIndex;
	}

	public String getName()
	{
		return mStageDeviceInterface.getDOFNameByIndex(mDOFIndex);
	}

	public void reset()
	{
		mStageDeviceInterface.reset(mDOFIndex);
	}

	public void home()
	{
		mStageDeviceInterface.home(mDOFIndex);
	}

	public void enable()
	{
		mStageDeviceInterface.enable(mDOFIndex);
	}

	public double getCurrentPosition()
	{
		return mStageDeviceInterface.getCurrentPosition(mDOFIndex);
	}

	public void goToPosition(double pValue)
	{
		mStageDeviceInterface.goToPosition(mDOFIndex, pValue);
	}

	public Boolean waitToBeReady(int pTimeOut, TimeUnit pTimeUnit)
	{
		return mStageDeviceInterface.waitToBeReady(	mDOFIndex,
																								pTimeOut,
																								pTimeUnit);
	}

	public DoubleVariable getMinPositionVariable()
	{
		return mStageDeviceInterface.getMinPositionVariable(mDOFIndex);
	}

	public DoubleVariable getMaxPositionVariable()
	{
		return mStageDeviceInterface.getMaxPositionVariable(mDOFIndex);
	}

	public DoubleVariable getEnableVariable()
	{
		return mStageDeviceInterface.getEnableVariable(mDOFIndex);
	}

	public DoubleVariable getPositionVariable()
	{
		return mStageDeviceInterface.getPositionVariable(mDOFIndex);
	}

	public DoubleVariable getReadyVariable()
	{
		return mStageDeviceInterface.getReadyVariable(mDOFIndex);
	}

	public DoubleVariable getHomingVariable()
	{
		return mStageDeviceInterface.getHomingVariable(mDOFIndex);
	}

	public BooleanVariable getStopVariable()
	{
		return mStageDeviceInterface.getStopVariable(mDOFIndex);
	}

	@Override
	public String toString()
	{
		return "StageDeviceDOF [mStageDeviceInterface=" + mStageDeviceInterface
						+ ", mDOFIndex="
						+ mDOFIndex
						+ "]";
	}



}
