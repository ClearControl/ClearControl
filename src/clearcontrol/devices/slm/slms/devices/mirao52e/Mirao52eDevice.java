package clearcontrol.devices.slm.slms.devices.mirao52e;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import mirao52e.Mirao52eDeformableMirror;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.slm.slms.DeformableMirrorDevice;

import org.ejml.data.DenseMatrix64F;

public class Mirao52eDevice extends DeformableMirrorDevice
                            implements LoggingFeature
{
  private static final int cFullMatrixWidthHeight = 8;
  private static final int cActuatorResolution = 2 << 14;

  private Mirao52eDeformableMirror mMirao52eDeformableMirror;
  private String mHostname;
  private int mPort;

  public Mirao52eDevice(int pDeviceIndex)
  {
    super("MIRAO52e_" + pDeviceIndex,
          cFullMatrixWidthHeight,
          cActuatorResolution);

    mMirao52eDeformableMirror = new Mirao52eDeformableMirror();

    final MachineConfiguration lCurrentMachineConfiguration =
                                                            MachineConfiguration.get();
    File lFlatCalibrationFile =
                              lCurrentMachineConfiguration.getFileProperty("device.ao.mirao."
                                                                           + pDeviceIndex
                                                                           + ".flat",
                                                                           null);
    if (lFlatCalibrationFile != null && lFlatCalibrationFile.exists())
      try
      {
        mMirao52eDeformableMirror.loadFlatCalibrationMatrix(lFlatCalibrationFile);
        info("Loaded flat calibration info");
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
      }

    mMatrixVariable = new Variable<DenseMatrix64F>("MatrixReference")
    {
      @Override
      public DenseMatrix64F setEventHook(final DenseMatrix64F pOldValue,
                                         final DenseMatrix64F pNewValue)
      {
        if (mMirao52eDeformableMirror.isOpen())
          mMirao52eDeformableMirror.sendFullMatrixMirrorShapeVector(pNewValue.data);

        return super.setEventHook(pOldValue, pNewValue);
      }

    };

  }

  @Override
  public int getMatrixWidth()
  {
    return cFullMatrixWidthHeight;
  }

  @Override
  public boolean open()
  {
    try
    {
      mMirao52eDeformableMirror.open();
      return true;
    }
    catch (final Throwable e)
    {
      final String lErrorString =
                                "Could not open connection to Mirao52e DM - "
                                  + e.getLocalizedMessage();
      severe("AO", lErrorString);
      return false;
    }

  }

  @Override
  public boolean start()
  {
    zero();
    return true;
  }

  @Override
  public void zero()
  {
    mMirao52eDeformableMirror.sendFlatMirrorShapeVector();
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
      mMirao52eDeformableMirror.close();
      return true;
    }
    catch (final IOException e)
    {
      final String lErrorString =
                                "Could not close connection to Mirao52e DM - "
                                  + e.getLocalizedMessage();
      severe("AO", lErrorString);
      return false;
    }
  }

  @Override
  public long getRelaxationTimeInMilliseconds()
  {
    return 5;
  }

}
