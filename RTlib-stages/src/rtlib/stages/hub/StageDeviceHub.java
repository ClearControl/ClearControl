package rtlib.stages.hub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.stages.StageDeviceInterface;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class StageDeviceHub extends NamedVirtualDevice	implements
														StageDeviceInterface,
														StartStopDeviceInterface
{

	private final ArrayList<StageDeviceInterface> mStageDeviceInterfaceList = new ArrayList<StageDeviceInterface>();
	private final ArrayList<StageDeviceDOF> mDOFList = new ArrayList<StageDeviceDOF>();
	private final BiMap<String, StageDeviceDOF> mNameToStageDeviceDOFMap = HashBiMap.create();

	public StageDeviceHub(String pDeviceName)
	{
		super(pDeviceName);
	}

	public String addDOF(	StageDeviceInterface pStageDeviceInterface,
							int pDOFIndex)
	{
		mStageDeviceInterfaceList.add(pStageDeviceInterface);
		final String lDOFName = pStageDeviceInterface.getDOFNameByIndex(pDOFIndex);
		final StageDeviceDOF lStageDeviceDOF = new StageDeviceDOF(	pStageDeviceInterface,
																	pDOFIndex);
		mDOFList.add(lStageDeviceDOF);
		mNameToStageDeviceDOFMap.put(lDOFName, lStageDeviceDOF);
		return lDOFName;
	}

	public List<StageDeviceDOF> getDOFList()
	{
		return Collections.unmodifiableList(mDOFList);
	}

	@Override
	public boolean open()
	{
		boolean lOpen = true;
		for (final StageDeviceInterface lStageDeviceInterface : mStageDeviceInterfaceList)
			lOpen &= lStageDeviceInterface.open();
		return lOpen;
	}

	@Override
	public boolean start()
	{
		boolean lStart = true;
		for (final StageDeviceInterface lStageDeviceInterface : mStageDeviceInterfaceList)
			if (lStageDeviceInterface instanceof StartStopDeviceInterface)
			{
				final StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lStageDeviceInterface;
				lStart &= lStartStopDevice.start();
			}
		return lStart;
	}

	@Override
	public boolean stop()
	{
		boolean lStop = true;
		for (final StageDeviceInterface lStageDeviceInterface : mStageDeviceInterfaceList)
			if (lStageDeviceInterface instanceof StartStopDeviceInterface)
			{
				final StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lStageDeviceInterface;
				lStop &= lStartStopDevice.stop();
			}
		return lStop;
	}

	@Override
	public boolean close()
	{
		boolean lClose = true;
		for (final StageDeviceInterface lStageDeviceInterface : mStageDeviceInterfaceList)
			lClose &= lStageDeviceInterface.close();
		return lClose;
	}

	@Override
	public int getNumberOfDOFs()
	{
		final int lNumberOFDOFs = mDOFList.size();
		return lNumberOFDOFs;
	}

	@Override
	public int getDOFIndexByName(String pName)
	{
		final StageDeviceDOF lStageDeviceDOF = mNameToStageDeviceDOFMap.get(pName);
		final int lIndex = mDOFList.indexOf(lStageDeviceDOF);
		return lIndex;
	}

	@Override
	public String getDOFNameByIndex(int pDOFIndex)
	{
		final StageDeviceDOF lStageDeviceDOF = mDOFList.get(pDOFIndex);
		final String lName = lStageDeviceDOF.getName();
		return lName;
	}

	@Override
	public void reset(int pDOFIndex)
	{
		mDOFList.get(pDOFIndex).reset();
	}

	@Override
	public void home(int pDOFIndex)
	{
		mDOFList.get(pDOFIndex).home();
	}

	@Override
	public void enable(int pDOFIndex)
	{
		mDOFList.get(pDOFIndex).enable();
	}

	@Override
	public double getCurrentPosition(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getCurrentPosition();
	}

	@Override
	public void goToPosition(int pDOFIndex, double pValue)
	{
		mDOFList.get(pDOFIndex).goToPosition(pValue);
	}

	@Override
	public Boolean waitToBeReady(	int pDOFIndex,
									int pTimeOut,
									TimeUnit pTimeUnit)
	{
		return mDOFList.get(pDOFIndex).waitToBeReady(	pTimeOut,
														pTimeUnit);
	}

	@Override
	public DoubleVariable getMinPositionVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getMinPositionVariable();
	}

	@Override
	public DoubleVariable getMaxPositionVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getMaxPositionVariable();
	}

	@Override
	public DoubleVariable getEnableVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getEnableVariable();
	}

	@Override
	public DoubleVariable getPositionVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getPositionVariable();
	}

	@Override
	public DoubleVariable getReadyVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getReadyVariable();
	}

	@Override
	public DoubleVariable getHomingVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getHomingVariable();
	}

	@Override
	public BooleanVariable getStopVariable(int pDOFIndex)
	{
		return mDOFList.get(pDOFIndex).getStopVariable();
	}

	@Override
	public String toString()
	{
		return "StageHub [mDOFList=" + mDOFList
				+ ", getNumberOfDOFs()="
				+ getNumberOfDOFs()
				+ "]";
	}

}
