package clearcontrol.microscope.lightsheet.simulation;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clearcl.ClearCLBuffer;
import clearcl.util.ElapsedTime;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProvider;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProviderBase;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationQueue;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetQueue;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitchQueue;
import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import simbryo.synthoscopy.microscope.lightsheet.LightSheetMicroscopeSimulator;
import simbryo.synthoscopy.microscope.parameters.CameraParameter;
import simbryo.synthoscopy.microscope.parameters.DetectionParameter;
import simbryo.synthoscopy.microscope.parameters.IlluminationParameter;

/**
 * Lightsheet microscope simulation stack provider
 *
 * @author royer
 */
public class LightSheetSimulationStackProvider extends
                                               StackCameraSimulationProviderBase
                                               implements
                                               StackCameraSimulationProvider,
                                               LoggingInterface
{

  private LightSheetMicroscopeInterface mLightSheetMicroscope;
  private LightSheetMicroscopeSimulator mLightSheetMicroscopeSimulator;
  private int mCameraIndex;

  private DetectionArmInterface mDetectionArmDevice;
  private LightSheetOpticalSwitch mOpticalSwitch;
  private ArrayList<LightSheetInterface> mLightSheetList =
                                                         new ArrayList<>();

  private DetectionArmQueue mDetectionStateQueue;
  private LightSheetOpticalSwitchQueue mOpticalSwitchStateQueue;
  private ConcurrentHashMap<Integer, LightSheetQueue> mLightSheetStateQueuesMap =
                                                                                new ConcurrentHashMap<>();

  /**
   * Instanciates a lightsheet simulation stack provider.
   * 
   * @param pLightSheetMicroscope
   *          light sheet sample simulation device
   * @param pLightSheetMicroscopeSimulator
   *          light sheet microscope simulator
   * @param pCameraIndex
   *          camera index
   */
  public LightSheetSimulationStackProvider(LightSheetMicroscopeInterface pLightSheetMicroscope,
                                           LightSheetMicroscopeSimulator pLightSheetMicroscopeSimulator,
                                           int pCameraIndex)
  {
    mLightSheetMicroscope = pLightSheetMicroscope;
    mLightSheetMicroscopeSimulator = pLightSheetMicroscopeSimulator;
    mCameraIndex = pCameraIndex;

    mDetectionArmDevice =
                        mLightSheetMicroscope.getDevice(DetectionArmInterface.class,
                                                        mCameraIndex);

    mOpticalSwitch =
                   mLightSheetMicroscope.getDevice(LightSheetOpticalSwitch.class,
                                                   0);

    int lNumberOfLightSheets =
                             mLightSheetMicroscope.getNumberOfDevices(LightSheetInterface.class);

    for (int l = 0; l < lNumberOfLightSheets; l++)
      mLightSheetList.add(mLightSheetMicroscope.getDevice(LightSheetInterface.class,
                                                          l));

  }

  @Override
  protected void fillStackData(StackCameraSimulationQueue pQueue,
                               ArrayList<Boolean> pKeepPlaneList,
                               long pWidth,
                               long pHeight,
                               long pDepth,
                               StackInterface pStack)
  {
    synchronized (mLightSheetMicroscopeSimulator)
    {

      int lWidth = (int) pStack.getWidth();
      int lHeight = (int) pStack.getHeight();

      float lExposureInSeconds = pQueue.getExposureInSecondsVariable()
                                       .get()
                                       .floatValue();

      mLightSheetMicroscopeSimulator.setNumberParameter(CameraParameter.Exposure,
                                                        mCameraIndex,
                                                        lExposureInSeconds);

      mLightSheetMicroscopeSimulator.setNumberParameter(CameraParameter.ROIWidth,
                                                        mCameraIndex,
                                                        lWidth);
      mLightSheetMicroscopeSimulator.setNumberParameter(CameraParameter.ROIHeight,
                                                        mCameraIndex,
                                                        lHeight);

      final ContiguousMemoryInterface lContiguousMemory =
                                                        pStack.getContiguousMemory();

      int lLastZiKept = getLast(pKeepPlaneList);

      int lQueueLength = pQueue.getQueueLength();

      LightSheetMicroscopeQueue lLightSheetMicroscopeQueue =
                                                           mLightSheetMicroscope.getPlayedQueueVariable()
                                                                                .get();

      collectDetectionStateQueues(lLightSheetMicroscopeQueue);

      collectOpticalSwitchStateQueues(lLightSheetMicroscopeQueue);

      for (int l = 0; l < mLightSheetList.size(); l++)
        collectIluminationStateQueues(lLightSheetMicroscopeQueue, l);
      /**/

      for (int zi = 0, i = 0; zi < lQueueLength; zi++)
      {

        passDetectionParameters(zi);

        for (int l = 0; l < mLightSheetList.size(); l++)
        {
          passIlluminationParameters(l, zi);
        } /**/

        @SuppressWarnings("unused")
        double lMilliseconds =
                             ElapsedTime.measure("!!renderplane",
                                                 () -> mLightSheetMicroscopeSimulator.render(mCameraIndex,
                                                                                             true));
        // info("Rendering plane %d in %g ms \n",zi,lMilliseconds);

        if (pKeepPlaneList.get(zi))
        {
          ClearCLBuffer lCameraImageBuffer =
                                           mLightSheetMicroscopeSimulator.getCameraImageBuffer(mCameraIndex);

          long lOffset = i++ * lCameraImageBuffer.getSizeInBytes();

          ContiguousMemoryInterface lImagePlane =
                                                lContiguousMemory.subRegion(lOffset,
                                                                            lCameraImageBuffer.getSizeInBytes());

          final int fzi = zi;
          lMilliseconds =
                        ElapsedTime.measure("!!copyplane",
                                            () -> lCameraImageBuffer.writeTo(lImagePlane,
                                                                             fzi == lLastZiKept)); //
          // info("Copying plane %d in %g ms \n",zi,lMilliseconds);

        }
      }
    }
  }

  private void collectDetectionStateQueues(LightSheetMicroscopeQueue pLightSheetMicroscopeQueue)
  {
    mDetectionStateQueue =
                         (DetectionArmQueue) pLightSheetMicroscopeQueue.getDeviceQueue(mDetectionArmDevice);
  }

  private void collectOpticalSwitchStateQueues(LightSheetMicroscopeQueue pLightSheetMicroscopeQueue)
  {
    mOpticalSwitchStateQueue =
                             (LightSheetOpticalSwitchQueue) pLightSheetMicroscopeQueue.getDeviceQueue(mOpticalSwitch);

  }

  private void collectIluminationStateQueues(LightSheetMicroscopeQueue pLightSheetMicroscopeQueue,
                                             int pLightSheetIndex)
  {
    LightSheetInterface lLightSheetDevice =
                                          mLightSheetList.get(pLightSheetIndex);
    mLightSheetStateQueuesMap.put(pLightSheetIndex,
                                  (LightSheetQueue) pLightSheetMicroscopeQueue.getDeviceQueue(lLightSheetDevice));
  }

  private void passDetectionParameters(int zi)
  {

    float z = mDetectionStateQueue.getQueuedValue(
                                                  mDetectionArmDevice.getZFunction()
                                                                     .get(),
                                                  mDetectionArmDevice.getZVariable(),
                                                  zi)
                                  .floatValue();
    mLightSheetMicroscopeSimulator.setNumberParameter(DetectionParameter.Z,
                                                      mCameraIndex,
                                                      z);

  }

  private void passIlluminationParameters(int pLightSheetIndex,
                                          int zi)
  {
    LightSheetInterface lLightSheet =
                                    mLightSheetList.get(pLightSheetIndex);

    float x =
            mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                     .getQueuedValue(lLightSheet.getXFunction()
                                                                .get(),
                                                     lLightSheet.getXVariable(),
                                                     zi)
                                     .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.X,
                                                      pLightSheetIndex,
                                                      x);

    float y =
            mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                     .getQueuedValue(lLightSheet.getYFunction()
                                                                .get(),
                                                     lLightSheet.getYVariable(),
                                                     zi)
                                     .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Y,
                                                      pLightSheetIndex,
                                                      y);

    float z =
            mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                     .getQueuedValue(lLightSheet.getZFunction()
                                                                .get(),
                                                     lLightSheet.getZVariable(),
                                                     zi)
                                     .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Z,
                                                      pLightSheetIndex,
                                                      z);

    float alpha = mLightSheetStateQueuesMap.get(pLightSheetIndex)

                                           .getQueuedValue(lLightSheet.getAlphaFunction()
                                                                      .get(),
                                                           lLightSheet.getAlphaInDegreesVariable(),
                                                           zi)
                                           .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Alpha,
                                                      pLightSheetIndex,
                                                      alpha);

    float beta = mLightSheetStateQueuesMap.get(pLightSheetIndex)

                                          .getQueuedValue(lLightSheet.getBetaFunction()
                                                                     .get(),
                                                          lLightSheet.getBetaInDegreesVariable(),
                                                          zi)
                                          .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Beta,
                                                      pLightSheetIndex,
                                                      beta);

    float height = mLightSheetStateQueuesMap.get(pLightSheetIndex)

                                            .getQueuedValue(lLightSheet.getHeightFunction()
                                                                       .get(),
                                                            lLightSheet.getHeightVariable(),
                                                            zi)
                                            .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Height,
                                                      pLightSheetIndex,
                                                      height);

    float lLightSheetPower =

                           mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                                    .getQueuedValue(lLightSheet.getPowerFunction()
                                                                               .get(),
                                                                    lLightSheet.getPowerVariable(),
                                                                    zi)
                                                    .floatValue();

    boolean lLightSheetSwitchedOn =
                                  mOpticalSwitchStateQueue.getQueuedBooleanValue(mOpticalSwitch.getSwitchVariable(pLightSheetIndex),
                                                                                 zi);

    LaserDeviceInterface lLaserDevice =
                                      mLightSheetMicroscope.getDevice(LaserDeviceInterface.class,
                                                                      0);

    float lLaserPower =
                      lLaserDevice.getCurrentPowerInMilliWattVariable()
                                  .get()
                                  .floatValue();

    float lEffectiveIlluminationIntensity = (lLightSheetSwitchedOn ? 1
                                                                   : 0)
                                            * lLightSheetPower
                                            * lLaserPower;

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Intensity,
                                                      pLightSheetIndex,
                                                      lEffectiveIlluminationIntensity);

  }

  private int getLast(ArrayList<Boolean> pKeepPlaneList)
  {
    int lSize = pKeepPlaneList.size();
    for (int i = lSize - 1; i >= 0; i--)
      if (pKeepPlaneList.get(i))
        return i;
    return 0;
  }

}
