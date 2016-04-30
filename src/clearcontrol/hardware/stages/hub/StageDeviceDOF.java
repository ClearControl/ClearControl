package clearcontrol.hardware.stages.hub;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.stages.StageDeviceInterface;

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

	public Boolean waitToBeReady(int pTimeOut, TimeUnit pTimeUnit)
	{
		return mStageDeviceInterface.waitToBeReady(	mDOFIndex,
																								pTimeOut,
																								pTimeUnit);
	}

	public Variable<Double> getMinPositionVariable()
	{
		return mStageDeviceInterface.getMinPositionVariable(mDOFIndex);
	}

	public Variable<Double> getMaxPositionVariable()
	{
		return mStageDeviceInterface.getMaxPositionVariable(mDOFIndex);
	}

	public Variable<Boolean> getEnableVariable()
	{
		return mStageDeviceInterface.getEnableVariable(mDOFIndex);
	}

	public Variable<Double> getTargetPositionVariable()
	{
		return mStageDeviceInterface.getTargetPositionVariable(mDOFIndex);
	}

	public Variable<Double> getCurrentPositionVariable()
	{
		return mStageDeviceInterface.getCurrentPositionVariable(mDOFIndex);
	}

	public Variable<Boolean> getReadyVariable()
	{
		return mStageDeviceInterface.getReadyVariable(mDOFIndex);
	}

	public Variable<Boolean> getHomingVariable()
	{
		return mStageDeviceInterface.getHomingVariable(mDOFIndex);
	}

	public Variable<Boolean> getStopVariable()
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
