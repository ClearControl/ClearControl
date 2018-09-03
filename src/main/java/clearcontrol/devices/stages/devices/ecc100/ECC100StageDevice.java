package clearcontrol.devices.stages.devices.ecc100;

import java.util.Collection;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceBase;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;
import clearcontrol.devices.stages.devices.ecc100.variables.EnableVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.HomingVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.MaxPositionVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.MinPositionVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.PositionCurrentVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.PositionTargetVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.ReadyVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.ResetVariable;
import clearcontrol.devices.stages.devices.ecc100.variables.StopVariable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import ecc100.ECC100Axis;
import ecc100.ECC100Controller;

/**
 * ECC100 stage device
 *
 * @author royer
 */
public class ECC100StageDevice extends StageDeviceBase implements
                               StageDeviceInterface,
                               StartStopDeviceInterface,
                               WaitingInterface,
                               LoggingFeature
{

  final ECC100Controller mECC100Controller;
  private final BiMap<Integer, ECC100Axis> mIndexToAxisMap =
                                                           HashBiMap.create();
  private final BiMap<String, ECC100Axis> mNameToAxisMap =
                                                         HashBiMap.create();

  /**
   * Instantiates an ECC100 stage device
   */
  public ECC100StageDevice()
  {
    super("ECC100", StageType.Multi);
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
        final MachineConfiguration lCurrentMachineConfiguration =
                                                                MachineConfiguration.get();

        final Collection<Integer> lDeviceIdList =
                                                mECC100Controller.getDeviceIdList();
        if (lDeviceIdList.size() == 0)
          return false;

        int lDOFIndex = 0;

        for (final int lDeviceId : lDeviceIdList)
        {
          for (int axis = 0; axis < 3; axis++)
          {
            final ECC100Axis lAxis =
                                   mECC100Controller.getAxis(lDeviceId,
                                                             axis);

            final String lDeviceConfigString = "device.stage.ecc100."
                                               + lDeviceId
                                               + "."
                                               + axis;
            info("Found device: " + lDeviceConfigString);
            final String lDeviceName =
                                     lCurrentMachineConfiguration.getStringProperty(lDeviceConfigString,
                                                                                    "");
            if (!lDeviceName.isEmpty())
            {
              info("DOF index: %d, device name: %s, axis: %s",
                   lDOFIndex,
                   lDeviceName,
                   lAxis);
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

          mEnableVariables.add(new EnableVariable("Enable"
                                                  + mIndexToNameMap.get(dof),
                                                  lEcc100Axis));

          mReadyVariables.add(new ReadyVariable("Ready"
                                                + mIndexToNameMap.get(dof),
                                                lEcc100Axis));

          mHomingVariables.add(new HomingVariable("Homing"
                                                  + mIndexToNameMap.get(dof),
                                                  lEcc100Axis));

          mStopVariables.add(new StopVariable("Stop"
                                              + mIndexToNameMap.get(dof),
                                              lEcc100Axis));

          mResetVariables.add(new ResetVariable("Reset"
                                                + mIndexToNameMap.get(dof),
                                                lEcc100Axis));

          mTargetPositionVariables.add(new PositionTargetVariable("TargetPosition"
                                                                  + mIndexToNameMap.get(dof),
                                                                  lEcc100Axis));

          mCurrentPositionVariables.add(new PositionCurrentVariable("CurrentPosition"
                                                                    + mIndexToNameMap.get(dof),
                                                                    lEcc100Axis));

          mMinPositionVariables.add(new MinPositionVariable("MinPosition"
                                                            + mIndexToNameMap.get(dof),
                                                            lEcc100Axis));

          mMaxPositionVariables.add(new MaxPositionVariable("MaxPosition"
                                                            + mIndexToNameMap.get(dof),
                                                            lEcc100Axis));

          mGranularityPositionVariables.add(new Variable<Double>("GranularityPosition"
                                                                 + mIndexToNameMap.get(dof),
                                                                 0d));
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
