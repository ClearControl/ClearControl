package clearcontrol.devices.stages.devices.tst;

import java.util.concurrent.TimeUnit;

import aptj.APTJDevice;
import aptj.APTJDeviceFactory;
import aptj.APTJDeviceType;
import aptj.APTJExeption;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceBase;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;
import clearcontrol.devices.stages.devices.tst.variables.EnableVariable;
import clearcontrol.devices.stages.devices.tst.variables.HomingVariable;
import clearcontrol.devices.stages.devices.tst.variables.MaxPositionVariable;
import clearcontrol.devices.stages.devices.tst.variables.MinPositionVariable;
import clearcontrol.devices.stages.devices.tst.variables.PositionTargetVariable;
import clearcontrol.devices.stages.devices.tst.variables.ReadyVariable;
import clearcontrol.devices.stages.devices.tst.variables.ResetVariable;
import clearcontrol.devices.stages.devices.tst.variables.StopVariable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * TST001 stage device
 *
 * @author royer
 */
public class TSTStageDevice extends StageDeviceBase implements
                            StageDeviceInterface,
                            StartStopDeviceInterface,
                            WaitingInterface,
                            LoggingFeature,
                            AsynchronousSchedulerFeature
{

  private final APTJDeviceFactory mAPTJDeviceFactory;

  private final BiMap<Integer, APTJDevice> mIndexToDeviceMap =
                                                             HashBiMap.create();

  private volatile boolean mOpen = false;

  /**
   * Instantiates an APTJ stage device
   * 
   */
  public TSTStageDevice()
  {
    super("TST001", StageType.Multi);
    try
    {
      mAPTJDeviceFactory =
                         new APTJDeviceFactory(APTJDeviceType.TST001);
    }
    catch (APTJExeption e)
    {
      severe("Could not initialize APTJ library!");
      throw new RuntimeException("Could not initialize APTJ library",
                                 e);
    }
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

      final MachineConfiguration lCurrentMachineConfiguration =
                                                              MachineConfiguration.get();

      final int lNumberOfDevices =
                                 mAPTJDeviceFactory.getNumberOfDevices();

      if (lNumberOfDevices == 0)
        return false;

      for (int lDOFIndex =
                         0; lDOFIndex < lNumberOfDevices; lDOFIndex++)
      {

        final APTJDevice lDevice =
                                 mAPTJDeviceFactory.createDeviceFromIndex(lDOFIndex);

        info("Adding DOF %d: %s \n", lDOFIndex, lDevice);

        mIndexToDeviceMap.put(lDOFIndex, lDevice);

        final String lDeviceConfigString = "device.stage.tst001."
                                           + lDevice.getSerialNumber();

        final String lDeviceName =
                                 lCurrentMachineConfiguration.getStringProperty(lDeviceConfigString,
                                                                                "");
        if (!lDeviceName.isEmpty())
        {
          info("Found device in config: DOF index= %d, serial number= %sdevice name= %s",
               lDOFIndex,
               lDevice.getSerialNumber(),
               lDeviceName);

          mIndexToNameMap.put(lDOFIndex, lDeviceName);
        }
      }

      for (int lDOFIndex =
                         0; lDOFIndex < lNumberOfDevices; lDOFIndex++)
      {
        APTJDevice lAPTJDevice = mIndexToDeviceMap.get(lDOFIndex);

        mEnableVariables.add(new EnableVariable("Enable"
                                                + mIndexToNameMap.get(lDOFIndex),
                                                lAPTJDevice));

        mReadyVariables.add(new ReadyVariable("Ready"
                                              + mIndexToNameMap.get(lDOFIndex),
                                              lAPTJDevice));

        mHomingVariables.add(new HomingVariable("Homing"
                                                + mIndexToNameMap.get(lDOFIndex),
                                                lAPTJDevice));

        mStopVariables.add(new StopVariable("Stop"
                                            + mIndexToNameMap.get(lDOFIndex),
                                            lAPTJDevice));

        mResetVariables.add(new ResetVariable("Reset"
                                              + mIndexToNameMap.get(lDOFIndex),
                                              lAPTJDevice));

        mTargetPositionVariables.add(new PositionTargetVariable("TargetPosition"
                                                                + mIndexToNameMap.get(lDOFIndex),
                                                                lAPTJDevice));

        mCurrentPositionVariables.add(new Variable<Double>("CurrentPosition"
                                                           + mIndexToNameMap.get(lDOFIndex),
                                                           0.0));

        mMinPositionVariables.add(new MinPositionVariable("MinPosition"
                                                          + mIndexToNameMap.get(lDOFIndex),
                                                          lAPTJDevice));

        mMaxPositionVariables.add(new MaxPositionVariable("MaxPosition"
                                                          + mIndexToNameMap.get(lDOFIndex),
                                                          lAPTJDevice));

        mGranularityPositionVariables.add(new Variable<Double>("GranularityPosition"
                                                               + mIndexToNameMap.get(lDOFIndex),
                                                               0d));
      }

      mOpen = true;

      Runnable lPolling = () -> {
        if (mOpen)
          for (int lDOFIndex =
                             0; lDOFIndex < lNumberOfDevices; lDOFIndex++)
          {
            try
            {
              APTJDevice lAPTJDevice =
                                     mIndexToDeviceMap.get(lDOFIndex);
              getCurrentPositionVariable(lDOFIndex).set(lAPTJDevice.getCurrentPosition());
            }
            catch (APTJExeption e)
            {
              e.printStackTrace();
            }
          }
      };

      scheduleAtFixedRate(lPolling, 100, TimeUnit.MILLISECONDS);

      return true;
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
    return true;
  }

  @Override
  public boolean stop()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    try
    {
      mOpen = false;
      mAPTJDeviceFactory.close();
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
    return String.format("TSTStageDevice [mAPTJDeviceFactory=%s, mIndexToDeviceMap=%s]",
                         mAPTJDeviceFactory,
                         mIndexToDeviceMap);
  }

}
