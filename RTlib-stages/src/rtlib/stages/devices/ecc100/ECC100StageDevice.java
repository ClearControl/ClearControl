package rtlib.stages.devices.ecc100;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.stages.StageDeviceInterface;
import rtlib.stages.devices.ecc100.variables.EnableVariable;
import rtlib.stages.devices.ecc100.variables.HomingVariable;
import rtlib.stages.devices.ecc100.variables.MaxPositionVariable;
import rtlib.stages.devices.ecc100.variables.MinPositionVariable;
import rtlib.stages.devices.ecc100.variables.PositionVariable;
import rtlib.stages.devices.ecc100.variables.ReadyVariable;
import rtlib.stages.devices.ecc100.variables.ResetVariable;
import rtlib.stages.devices.ecc100.variables.StopVariable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ecc100.ECC100Axis;
import ecc100.ECC100Controller;

public class ECC100StageDevice extends NamedVirtualDevice	implements
																													StageDeviceInterface,
																													Waiting
{

	private BooleanVariable[] mEnableVariables, mReadyVariables,
			mHomingVariables, mStopVariables, mResetVariables;
	private DoubleVariable[] mPositionVariables, mMinPositionVariables,
			mMaxPositionVariables;

	ECC100Controller mECC100Controller;
	private BiMap<Integer, ECC100Axis> mIndexToAxisMap = HashBiMap.create();
	private BiMap<String, ECC100Axis> mNameToAxisMap = HashBiMap.create();
	private BiMap<Integer, String> mIndexToNameMap = HashBiMap.create();


	public ECC100StageDevice()
	{
		super("ECC100");
		mECC100Controller = new ECC100Controller();
	}

	@Override
	public boolean open()
	{
		boolean lStart = super.open();

		if (lStart)
		{

			if (mECC100Controller.open())
			{
				MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

				Collection<Integer> lDeviceIdList = mECC100Controller.getDeviceIdList();
				if (lDeviceIdList.size() == 0)
					return false;

				int lDOFIndex = 0;

				for (int lDeviceId : lDeviceIdList)
				{
					for (int axis = 0; axis < 3; axis++)
					{
						ECC100Axis lAxis = mECC100Controller.getAxis(	lDeviceId,
																													axis);

						String lDeviceConfigString = "device.stage.ecc100." + lDeviceId
																					+ "."
																					+ axis;
						String lDeviceName = lCurrentMachineConfiguration.getStringProperty(lDeviceConfigString,
																																								"");
						if (!lDeviceName.isEmpty())
						{
							mIndexToAxisMap.put(lDOFIndex, lAxis);
							mNameToAxisMap.put(lDeviceName, lAxis);
							mIndexToNameMap.put(lDOFIndex, lDeviceName);
							lDOFIndex++;
						}

					}
				}

				final int lNumberOfDofs = lDOFIndex;

				mEnableVariables = new BooleanVariable[lNumberOfDofs];
				mReadyVariables = new BooleanVariable[lNumberOfDofs];
				mHomingVariables = new BooleanVariable[lNumberOfDofs];
				mStopVariables = new BooleanVariable[lNumberOfDofs];
				mResetVariables = new BooleanVariable[lNumberOfDofs];
				mPositionVariables = new DoubleVariable[lNumberOfDofs];
				mMinPositionVariables = new DoubleVariable[lNumberOfDofs];
				mMaxPositionVariables = new DoubleVariable[lNumberOfDofs];

				for (int dof = 0; dof < lNumberOfDofs; dof++)
				{
					ECC100Axis lEcc100Axis = mIndexToAxisMap.get(dof);

					mEnableVariables[dof] = new EnableVariable(	"Enable" + mIndexToNameMap.get(dof),
																											lEcc100Axis);

					mReadyVariables[dof] = new ReadyVariable(	"Ready" + mIndexToNameMap.get(dof),
																										lEcc100Axis);

					mHomingVariables[dof] = new HomingVariable(	"Homing" + mIndexToNameMap.get(dof),
																											lEcc100Axis);

					mStopVariables[dof] = new StopVariable(	"Stop" + mIndexToNameMap.get(dof),
																									lEcc100Axis);

					mResetVariables[dof] = new ResetVariable(	"Reset" + mIndexToNameMap.get(dof),
																										lEcc100Axis);

					mPositionVariables[dof] = new PositionVariable(	"Position" + mIndexToNameMap.get(dof),
																													lEcc100Axis);

					mMinPositionVariables[dof] = new MinPositionVariable(	"MinPosition" + mIndexToNameMap.get(dof),
																																lEcc100Axis);

					mMinPositionVariables[dof] = new MaxPositionVariable(	"MaxPosition" + mIndexToNameMap.get(dof),
																																lEcc100Axis);
				}


				return true;
			}
			return false;
		}
		return lStart;
	}

	@Override
	public int getNumberOfDOFs()
	{
		return mPositionVariables.length;
	}

	@Override
	public boolean start()
	{
		return mECC100Controller.start();
	}

	@Override
	public boolean stop()
	{
		return mECC100Controller.stop();
	}

	@Override
	public boolean close()
	{
		try
		{
			mECC100Controller.close();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public double getCurrentPosition(int pIndex)
	{
		return mPositionVariables[pIndex].getValue();
	}

	private void reset(int pIndex)
	{
		mResetVariables[pIndex].setEdge(true);
	}

	public void home(int pIndex)
	{
		mHomingVariables[pIndex].setEdge(true);
	}

	public void enable(int pIndex)
	{
		mEnableVariables[pIndex].setEdge(true);
	}

	public void goToPosition(int pIndex, double pValue)
	{
		mPositionVariables[pIndex].setValue(pValue);
	}

	public Boolean waitToBeReady(	int pIndex,
																int pTimeOut,
																TimeUnit pTimeUnit)
	{
		System.out.println("waiting...");
		return waitFor(	pTimeOut,
										pTimeUnit,
										() -> mReadyVariables[pIndex].getBooleanValue());
	}

	@Override
	public DoubleVariable getPositionVariable(int pIndex)
	{
		return mPositionVariables[pIndex];
	}

	@Override
	public DoubleVariable getMinPositionVariable(int pIndex)
	{
		return mMinPositionVariables[pIndex];
	}

	@Override
	public DoubleVariable getMaxPositionVariable(int pIndex)
	{
		return mMaxPositionVariables[pIndex];
	}

	@Override
	public DoubleVariable getEnableVariable(int pIndex)
	{
		return mEnableVariables[pIndex];
	}

	@Override
	public DoubleVariable getReadyVariable(int pIndex)
	{
		return mReadyVariables[pIndex];
	}

	@Override
	public DoubleVariable getHomingVariable(int pIndex)
	{
		return mHomingVariables[pIndex];
	}

	@Override
	public BooleanVariable getStopVariable(int pIndex)
	{
		return mStopVariables[pIndex];
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

	@Override
	public String toString()
	{
		return "ECC100StageDevice [mECC100Controller=" + mECC100Controller
						+ ", getNumberOfDOFs()="
						+ getNumberOfDOFs()
						+ ", getDeviceName()="
						+ getDeviceName()
						+ "]";
	}

}
