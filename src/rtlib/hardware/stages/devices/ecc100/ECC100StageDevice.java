package rtlib.hardware.stages.devices.ecc100;

import java.util.Collection;

import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.hardware.stages.StageDeviceBase;
import rtlib.hardware.stages.StageDeviceInterface;
import rtlib.hardware.stages.StageType;
import rtlib.hardware.stages.devices.ecc100.variables.EnableVariable;
import rtlib.hardware.stages.devices.ecc100.variables.HomingVariable;
import rtlib.hardware.stages.devices.ecc100.variables.MaxPositionVariable;
import rtlib.hardware.stages.devices.ecc100.variables.MinPositionVariable;
import rtlib.hardware.stages.devices.ecc100.variables.PositionCurrentVariable;
import rtlib.hardware.stages.devices.ecc100.variables.PositionTargetVariable;
import rtlib.hardware.stages.devices.ecc100.variables.ReadyVariable;
import rtlib.hardware.stages.devices.ecc100.variables.ResetVariable;
import rtlib.hardware.stages.devices.ecc100.variables.StopVariable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ecc100.ECC100Axis;
import ecc100.ECC100Controller;

public class ECC100StageDevice extends StageDeviceBase implements
																											StageDeviceInterface,
																											StartStopDeviceInterface,
																											Waiting
{

	final ECC100Controller mECC100Controller;
	private final BiMap<Integer, ECC100Axis> mIndexToAxisMap = HashBiMap.create();
	private final BiMap<String, ECC100Axis> mNameToAxisMap = HashBiMap.create();

	public ECC100StageDevice()
	{
		super("ECC100");
		mECC100Controller = new ECC100Controller();
	}

	@Override
	public StageType getStageType()
	{
		return StageType.Multi;
	}

	@Override
	public boolean open()
	{
		try
		{
			final boolean lStart = super.open();

			if (!lStart)
				return false;

			if (mECC100Controller.open())
			{
				final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

				final Collection<Integer> lDeviceIdList = mECC100Controller.getDeviceIdList();
				if (lDeviceIdList.size() == 0)
					return false;

				int lDOFIndex = 0;

				for (final int lDeviceId : lDeviceIdList)
				{
					for (int axis = 0; axis < 3; axis++)
					{
						final ECC100Axis lAxis = mECC100Controller.getAxis(	lDeviceId,
																																axis);

						final String lDeviceConfigString = "device.stage.ecc100." + lDeviceId
																								+ "."
																								+ axis;
						System.out.println("Found device: " + lDeviceConfigString);
						final String lDeviceName = lCurrentMachineConfiguration.getStringProperty(lDeviceConfigString,
																																											"");
						if (!lDeviceName.isEmpty())
						{
							System.out.println(lDOFIndex);
							mIndexToAxisMap.put(lDOFIndex, lAxis);
							mNameToAxisMap.put(lDeviceName, lAxis);
							mIndexToNameMap.put(lDOFIndex, lDeviceName);
							lDOFIndex++;

						}

					}
				}

				final int lNumberOfDofs = lDOFIndex;

				for (int dof = 0; dof < lNumberOfDofs; dof++)
				{
					final ECC100Axis lEcc100Axis = mIndexToAxisMap.get(dof);

					mEnableVariables.add(new EnableVariable("Enable" + mIndexToNameMap.get(dof),
																									lEcc100Axis));

					mReadyVariables.add(new ReadyVariable("Ready" + mIndexToNameMap.get(dof),
																								lEcc100Axis));

					mHomingVariables.add(new HomingVariable("Homing" + mIndexToNameMap.get(dof),
																									lEcc100Axis));

					mStopVariables.add(new StopVariable("Stop" + mIndexToNameMap.get(dof),
																							lEcc100Axis));

					mResetVariables.add(new ResetVariable("Reset" + mIndexToNameMap.get(dof),
																								lEcc100Axis));

					mTargetPositionVariables.add(new PositionTargetVariable("TargetPosition" + mIndexToNameMap.get(dof),
																														lEcc100Axis));

					mCurrentPositionVariables.add(new PositionCurrentVariable(	"CurrentPosition" + mIndexToNameMap.get(dof),
																															lEcc100Axis));

					mMinPositionVariables.add(new MinPositionVariable("MinPosition" + mIndexToNameMap.get(dof),
																														lEcc100Axis));

					mMinPositionVariables.add(new MaxPositionVariable("MaxPosition" + mIndexToNameMap.get(dof),
																														lEcc100Axis));
				}

				return true;
			}
			return false;
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return false;
		}

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
		catch (final Exception e)
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return "ECC100StageDevice [mECC100Controller=" + mECC100Controller
						+ ", getNumberOfDOFs()="
						+ getNumberOfDOFs()
						+ ", getDeviceName()="
						+ getName()
						+ "]";
	}

}
