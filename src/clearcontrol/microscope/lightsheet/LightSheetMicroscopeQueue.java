package clearcontrol.microscope.lightsheet;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.StackCameraQueue;
import clearcontrol.microscope.MicroscopeQueueBase;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitchQueue;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorQueue;
import clearcontrol.stack.metadata.MetaDataEntryInterface;
import clearcontrol.stack.metadata.MetaDataVoxelDim;
import clearcontrol.stack.metadata.StackMetaData;

/**
 * Lightsheet microscope queue
 *
 * @author royer
 */
public class LightSheetMicroscopeQueue extends
                                       MicroscopeQueueBase<LightSheetMicroscope, LightSheetMicroscopeQueue>
                                       implements
                                       QueueInterface,
                                       LightSheetMicroscopeParameterInterface
{

  /**
   * Instanciates a lightsheet microscope
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
  public LightSheetMicroscopeQueue(LightSheetMicroscope pLightSheetMicroscope)
  {
    super(pLightSheetMicroscope);

    LightSheetSignalGeneratorQueue lSignalGeneratorQueue =
                                                         (LightSheetSignalGeneratorQueue) getDeviceQueue(LightSheetSignalGeneratorDevice.class,
                                                                                                         0);

    int lNumberOfDetectionArmDevices =
                                     pLightSheetMicroscope.getNumberOfDevices(DetectionArm.class);
    int lNumberOfLightSheetDevices =
                                   pLightSheetMicroscope.getNumberOfDevices(LightSheet.class);

    for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
      lSignalGeneratorQueue.addDetectionArmQueue(getDetectionArmDeviceQueue(i));

    for (int i = 0; i < lNumberOfLightSheetDevices; i++)
      lSignalGeneratorQueue.addLightSheetQueue(getLightSheetDeviceQueue(i));

    lSignalGeneratorQueue.addOpticalSwitchQueue(getLightSheetOpticalSwitchQueue());

  }

  private int getNumberOfLaserDevices()
  {
    return getMicroscope().getNumberOfLaserLines();
  }

  private int getNumberOfLightSheets()
  {
    return getMicroscope().getNumberOfDevices(LightSheet.class);
  }

  private int getNumberOfDetectionArms()
  {
    return getMicroscope().getNumberOfDevices(DetectionArm.class);
  }

  private int getNumberOfStackCameras()
  {
    return getMicroscope().getNumberOfDevices(StackCameraDeviceInterface.class);
  }

  /**
   * Returns the stack camera queue for a given index
   * 
   * @param pCameraIndex
   *          camera index
   * @return stack camera queue
   */
  public StackCameraQueue<?> getStackCameraDeviceQueue(int pCameraIndex)
  {
    return (StackCameraQueue<?>) getDeviceQueue(StackCameraDeviceInterface.class,
                                                pCameraIndex);
  }

  /**
   * Returns the detection arm device queue for a given index
   * 
   * @param pDetectionArmIndex
   *          detection arm index
   * @return detection arm queue
   */
  public DetectionArmQueue getDetectionArmDeviceQueue(int pDetectionArmIndex)
  {
    return (DetectionArmQueue) getDeviceQueue(DetectionArm.class,
                                              pDetectionArmIndex);
  }

  /**
   * Returns the lightsheet device queue for a given index
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @return lightsheet device queue
   */
  public LightSheetQueue getLightSheetDeviceQueue(int pLightSheetIndex)
  {
    return (LightSheetQueue) getDeviceQueue(LightSheet.class,
                                            pLightSheetIndex);
  }

  /**
   * Returns the lightsheet optical switch queue
   * 
   * @return lightsheet optical switch queue
   */
  public LightSheetOpticalSwitchQueue getLightSheetOpticalSwitchQueue()
  {
    return (LightSheetOpticalSwitchQueue) getDeviceQueue(LightSheetOpticalSwitch.class,
                                                         0);
  }

  /**
   * Returns the lightsheet signal generator queue
   * 
   * @return lightsheet signal generator queue
   */
  public LightSheetSignalGeneratorQueue getLightSheetSignalGeneratorQueue()
  {
    return (LightSheetSignalGeneratorQueue) getDeviceQueue(LightSheetSignalGeneratorDevice.class,
                                                           0);
  }

  /**
   * Adds metadata entries that specify the voxel dimensions
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   * @param pVoxelDepthInMicrons
   *          voxel depth in microns
   */
  public void addVoxelDimMetaData(LightSheetMicroscopeInterface pLightSheetMicroscope,
                                  double pVoxelDepthInMicrons)
  {
    int lNumberOfDetectionArms =
                               pLightSheetMicroscope.getNumberOfDetectionArms();
    for (int c = 0; c < lNumberOfDetectionArms; c++)
    {
      StackCameraQueue<?> lCameraQueue;
      try
      {
        lCameraQueue = this.getCameraDeviceQueue(c);
      }
      catch (IllegalArgumentException e)
      {
        e.printStackTrace();
        continue;
      }
      if (lCameraQueue == null)
        continue;

      StackMetaData lMetaData = lCameraQueue.getMetaDataVariable()
                                            .get();

      DetectionArmInterface lDetectionArm =
                                          pLightSheetMicroscope.getDetectionArm(c);

      double lPixelSizeInMicrons =
                                 lDetectionArm.getPixelSizeInMicrometerVariable()
                                              .get();

      lMetaData.addEntry(MetaDataVoxelDim.VoxelDimX,
                         lPixelSizeInMicrons);
      lMetaData.addEntry(MetaDataVoxelDim.VoxelDimY,
                         lPixelSizeInMicrons);
      lMetaData.addEntry(MetaDataVoxelDim.VoxelDimZ,
                         pVoxelDepthInMicrons);
    }
  }

  /**
   * Adds the given metadata entry to all stack cameras
   * 
   * @param pEntryKey
   *          entry
   * @param pValue
   *          value
   */
  public <T> void addMetaDataEntry(MetaDataEntryInterface<T> pEntryKey,
                                   T pValue)
  {
    for (int c = 0; c < getNumberOfDetectionArms(); c++)
    {
      StackMetaData lMetaData =
                              getCameraDeviceQueue(c).getMetaDataVariable()
                                                     .get();
      lMetaData.addEntry(pEntryKey, pValue);
    }
  }

  @Override
  public void clearQueue()
  {
    super.clearQueue();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    super.addCurrentStateToQueue();
  }

  @Override
  public void finalizeQueue()
  {
    super.finalizeQueue();
  }

  @Override
  public int getQueueLength()
  {
    return super.getQueueLength();
  }

  /**
   * Sets to zero (default) all lightsheet microscope parameters.
   */
  public void zero()
  {
    for (int i = 0; i < getNumberOfDetectionArms(); i++)
    {
      setDZ(i, 0);
      setC(i, true);
    }

    for (int i = 0; i < getNumberOfLightSheets(); i++)
    {
      setIX(i, 0);
      setIY(i, 0);
      setIZ(i, 0);
      setIA(i, 0);
      setIB(i, 0);
      setIZ(i, 0);
      setIH(i, 0);

      for (int j = 0; j < getNumberOfLaserDevices(); j++)
      {
        setIPatternOnOff(i, j, false);
      }
    }

  }

  @Override
  public void setFullROI()
  {

    int lNumberOfStackCameraDevices = getNumberOfStackCameras();

    for (int c = 0; c < lNumberOfStackCameraDevices; c++)
    {
      getStackCameraDeviceQueue(c).getStackWidthVariable()
                                  .set(getStackCameraDeviceQueue(c).getStackCamera()
                                                                   .getMaxWidthVariable()
                                                                   .get());
      getStackCameraDeviceQueue(c).getStackHeightVariable()
                                  .set(getStackCameraDeviceQueue(c).getStackCamera()
                                                                   .getMaxHeightVariable()
                                                                   .get());
    }
  };

  @Override
  public void setCenteredROI(int pWidth, int pHeight)
  {

    int lNumberOfStackCameraDevices = getNumberOfStackCameras();

    for (int c = 0; c < lNumberOfStackCameraDevices; c++)
    {
      getStackCameraDeviceQueue(c).getStackWidthVariable()
                                  .set((long) pWidth);
      getStackCameraDeviceQueue(c).getStackHeightVariable()
                                  .set((long) pHeight);
    }
  };

  @Override
  public void setExp(double pExpsoureISeconds)
  {
    int lNumberOfLightsheets = getNumberOfLightSheets();
    for (int i = 0; i < lNumberOfLightsheets; i++)
      getLightSheetDeviceQueue(i).getEffectiveExposureInSecondsVariable()
                                 .set(pExpsoureISeconds);

    int lNumberOfStackCameraDevices = getNumberOfStackCameras();

    for (int c = 0; c < lNumberOfStackCameraDevices; c++)
      getStackCameraDeviceQueue(c).getExposureInSecondsVariable()
                                  .set(pExpsoureISeconds);
  };

  @Override
  public void setTransitionTime(double pTransitionTimeInSeconds)
  {
    int lNumberOfLightsheets = getNumberOfLightSheets();
    for (int i = 0; i < lNumberOfLightsheets; i++)
      getLightSheetSignalGeneratorQueue().getTransitionDurationInSecondsVariable()
                                         .set(pTransitionTimeInSeconds);
  }

  @Override
  public void setFinalisationTime(double pFinalisationTimeInSeconds)
  {
    int lNumberOfLightsheets = getNumberOfLightSheets();
    for (int i = 0; i < lNumberOfLightsheets; i++)
      getLightSheetDeviceQueue(i).getFinalisationTimeInSecondsVariable()
                                 .set(pFinalisationTimeInSeconds);
  }

  @Override
  public void setC(int pCameraIndex, boolean pKeepImage)
  {
    getStackCameraDeviceQueue(pCameraIndex).getKeepPlaneVariable()
                                           .set(pKeepImage);
  }

  @Override
  public boolean getC(int pCameraIndex)
  {
    return getStackCameraDeviceQueue(pCameraIndex).getKeepPlaneVariable()
                                                  .get();
  }

  @Override
  public void setC(boolean pKeepImage)
  {
    int lNumberOfStackCameraDevices = getNumberOfStackCameras();
    for (int c = 0; c < lNumberOfStackCameraDevices; c++)
      getStackCameraDeviceQueue(c).getKeepPlaneVariable()
                                  .set(pKeepImage);

  }

  @Override
  public void setDZ(int pDetectionArmIndex, double pValue)
  {
    getDetectionArmDeviceQueue(pDetectionArmIndex).getZVariable()
                                                  .set(pValue);
  }

  @Override
  public void setDZ(double pValue)
  {
    int lNumberOfDetectionArms = getNumberOfDetectionArms();
    for (int d = 0; d < lNumberOfDetectionArms; d++)
      getDetectionArmDeviceQueue(d).getZVariable().set(pValue);
  }

  @Override
  public double getDZ(int pDetectionArmIndex)
  {
    return getDetectionArmDeviceQueue(pDetectionArmIndex).getZVariable()
                                                         .get()
                                                         .doubleValue();
  }

  @Override
  public void setI(int pLightSheetIndex)
  {
    int lNumberOfLightsheets = getNumberOfLightSheets();
    for (int i = 0; i < lNumberOfLightsheets; i++)
      setI(i, i == pLightSheetIndex);
    getLightSheetSignalGeneratorQueue().getSelectedLightSheetIndexVariable()
                                       .set(pLightSheetIndex);
  };

  @Override
  public void setI(int pLightSheetIndex, boolean pOnOff)
  {
    getLightSheetOpticalSwitchQueue().getSwitchVariable(pLightSheetIndex)
                                     .set(pOnOff);
    if (pOnOff)
      getLightSheetSignalGeneratorQueue().getSelectedLightSheetIndexVariable()
                                         .set(pLightSheetIndex);
  };

  @Override
  public void setI(boolean pOnOff)
  {
    int lNumberOfLightsheets = getNumberOfLightSheets();
    for (int i = 0; i < lNumberOfLightsheets; i++)
      setI(i, pOnOff);
  };

  @Override
  public boolean getI(int pLightSheetIndex)
  {
    return getLightSheetOpticalSwitchQueue().getSwitchVariable(pLightSheetIndex)
                                            .get();
  }

  @Override
  public void setIX(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getXVariable()
                                              .set(pValue);
  };

  @Override
  public double getIX(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getXVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIY(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getYVariable()
                                              .set(pValue);
  };

  @Override
  public double getIY(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getYVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIZ(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getZVariable()
                                              .set(pValue);
  };

  @Override
  public void setIZ(double pValue)
  {
    int lNumberOfLightsheets = getNumberOfLightSheets();
    for (int i = 0; i < lNumberOfLightsheets; i++)
      getLightSheetDeviceQueue(i).getZVariable().set(pValue);
  };

  @Override
  public double getIZ(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getZVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIA(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getAlphaInDegreesVariable()
                                              .set(pValue);
  };

  @Override
  public double getIA(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getAlphaInDegreesVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIB(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getBetaInDegreesVariable()
                                              .set(pValue);
  };

  @Override
  public double getIB(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getBetaInDegreesVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIW(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getWidthVariable()
                                              .set(pValue);
  };

  @Override
  public double getIW(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getWidthVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIH(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getHeightVariable()
                                              .set(pValue);
  }

  @Override
  public double getIH(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getHeightVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setILO(boolean pOn)
  {
    int lNumberOfLightSheets = getNumberOfLightSheets();

    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      LightSheetQueue lLightSheetQueue = getLightSheetDeviceQueue(l);
      for (int i =
                 0; i < lLightSheetQueue.getNumberOfLaserDigitalControls(); i++)
        lLightSheetQueue.getLaserOnOffArrayVariable(i).set(pOn);
    }
  };

  @Override
  public void setILO(int pLightSheetIndex, boolean pOn)
  {
    LightSheetQueue lLightSheetQueue =
                                     getLightSheetDeviceQueue(pLightSheetIndex);
    for (int i =
               0; i < lLightSheetQueue.getNumberOfLaserDigitalControls(); i++)
      lLightSheetQueue.getLaserOnOffArrayVariable(i).set(pOn);
  };

  @Override
  public void setILO(int pLightSheetIndex,
                     int pLaserIndex,
                     boolean pOn)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getLaserOnOffArrayVariable(pLaserIndex)
                                              .set(pOn);
  };

  @Override
  public boolean getILO(int pLightSheetIndex, int pLaserIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getLaserOnOffArrayVariable(pLaserIndex)
                                                     .get();
  }

  @Override
  public void setIP(int pLightSheetIndex, double pValue)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getPowerVariable()
                                              .set(pValue);
  }

  @Override
  public double getIP(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getPowerVariable()
                                                     .get()
                                                     .doubleValue();
  }

  @Override
  public void setIPA(boolean pAdapt)
  {
    int lNumberOfLightSheets = getNumberOfLightSheets();

    for (int i = 0; i < lNumberOfLightSheets; i++)
      getLightSheetDeviceQueue(i).getAdaptPowerToWidthHeightVariable()
                                 .set(pAdapt);

  }

  @Override
  public void setIPA(int pLightSheetIndex, boolean pAdapt)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getAdaptPowerToWidthHeightVariable()
                                              .set(pAdapt);

  }

  @Override
  public boolean getIPA(int pLightSheetIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getAdaptPowerToWidthHeightVariable()
                                                     .get();
  }

  @Override
  public void setIPatternOnOff(int pLightSheetIndex,
                               int pLaserIndex,
                               boolean pOnOff)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getSIPatternOnOffVariable(pLaserIndex)
                                              .set(pOnOff);
  }

  @Override
  public boolean getIPatternOnOff(int pLightSheetIndex,
                                  int pLaserIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getSIPatternOnOffVariable(pLaserIndex)
                                                     .get();
  }

  @Override
  public void setIPattern(int pLightSheetIndex,
                          int pLaserIndex,
                          StructuredIlluminationPatternInterface pPattern)
  {
    getLightSheetDeviceQueue(pLightSheetIndex).getSIPatternVariable(pLaserIndex)
                                              .set(pPattern);
  }

  @Override
  public StructuredIlluminationPatternInterface getIPattern(int pLightSheetIndex,
                                                            int pLaserIndex)
  {
    return getLightSheetDeviceQueue(pLightSheetIndex).getSIPatternVariable(pLaserIndex)
                                                     .get();
  }

}
