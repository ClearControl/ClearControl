package clearcontrol.microscope.lightsheet.simulation;

import java.util.ArrayList;

import clearcl.ClearCLContext;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProvider;
import clearcontrol.devices.cameras.devices.sim.providers.FractalStackProvider;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.devices.lasers.devices.sim.LaserDeviceSimulator;
import clearcontrol.devices.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.devices.optomech.filterwheels.devices.sim.FilterWheelDeviceSimulator;
import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.devices.signalamp.devices.sim.ScalingAmplifierSimulator;
import clearcontrol.devices.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import clearcontrol.devices.stages.StageType;
import clearcontrol.devices.stages.devices.sim.StageDeviceSimulator;
import clearcontrol.microscope.adaptive.AdaptiveEngine;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.adaptive.modules.AdaptationX;
import clearcontrol.microscope.lightsheet.adaptive.modules.AdaptationZ;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.state.ControlPlaneLayout;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import clearcontrol.stack.sourcesink.sink.RawFileStackSink;

/**
 * Simulated lightsheet microscope
 *
 * @author royer
 */
public class SimulatedLightSheetMicroscope extends
                                           LightSheetMicroscope
{

  /**
   * Instantiates a simulated lightsheet microscope
   * 
   * @param pDeviceName
   *          device name
   * @param pStackFusionContext
   *          ClearCL context for stack fusion
   * @param pMaxStackProcessingQueueLength
   *          max stack processing queue length
   * @param pThreadPoolSize
   *          thread pool size
   */
  public SimulatedLightSheetMicroscope(String pDeviceName,
                                       ClearCLContext pStackFusionContext,
                                       int pMaxStackProcessingQueueLength,
                                       int pThreadPoolSize)
  {
    super(pDeviceName,
          pStackFusionContext,
          pMaxStackProcessingQueueLength,
          pThreadPoolSize);

  }

  /**
   * Assembles the microscope
   * 
   * @param pDummySimulation
   *          true-> uses a dummy simulation instead of the embryo
   * @param pXYZRStage
   *          XYZR Stage
   * @param pSharedLightSheetControl
   *          true -> shared lightsheet control
   * @param pSimulatorDevice
   *          simulator device
   */
  public void addSimulatedDevices(boolean pDummySimulation,
                                  boolean pXYZRStage,
                                  boolean pSharedLightSheetControl,
                                  LightSheetMicroscopeSimulationDevice pSimulatorDevice)
  {

    int lNumberOfDetectionArms =
                               pSimulatorDevice.getSimulator()
                                               .getNumberOfDetectionArms();
    int lNumberOfLightSheets =
                             pSimulatorDevice.getSimulator()
                                             .getNumberOfLightSheets();

    // Setting up lasers:
    {
      int[] lLaserWavelengths = new int[]
      { 488, 594 };
      ArrayList<LaserDeviceInterface> lLaserList = new ArrayList<>();
      for (int l = 0; l < lLaserWavelengths.length; l++)
      {
        LaserDeviceInterface lLaser =
                                    new LaserDeviceSimulator("Laser "
                                                             + lLaserWavelengths[l],
                                                             l,
                                                             lLaserWavelengths[l],
                                                             100);
        lLaserList.add(lLaser);
        addDevice(l, lLaser);
      }
    }

    // Setting up Stage:
    if (pXYZRStage)
    {
      StageDeviceSimulator lStageDeviceSimulator =
                                                 new StageDeviceSimulator("Stage",
                                                                          StageType.XYZR,
                                                                          true);
      lStageDeviceSimulator.addXYZRDOFs();
      lStageDeviceSimulator.setSpeed(0.8);

      addDevice(0, lStageDeviceSimulator);
    }

    // Setting up Filterwheel:
    {
      int[] lFilterWheelPositions = new int[]
      { 0, 1, 2, 3 };
      FilterWheelDeviceInterface lFilterWheelDevice =
                                                    new FilterWheelDeviceSimulator("FilterWheel",
                                                                                   lFilterWheelPositions);
      lFilterWheelDevice.setPositionName(0, "405 filter");
      lFilterWheelDevice.setPositionName(1, "488 filter");
      lFilterWheelDevice.setPositionName(2, "561 filter");
      lFilterWheelDevice.setPositionName(3, "594 filter");
      getDeviceLists().addDevice(0, lFilterWheelDevice);
    }

    // Setting up trigger:

    Variable<Boolean> lTrigger =
                               new Variable<Boolean>("CameraTrigger",
                                                     false);

    ArrayList<StackCameraDeviceSimulator> lCameraList =
                                                      new ArrayList<>();

    // Setting up cameras:
    {

      for (int c = 0; c < lNumberOfDetectionArms; c++)
      {
        final StackCameraDeviceSimulator lCamera =
                                                 new StackCameraDeviceSimulator("StackCamera"
                                                                                + c,
                                                                                lTrigger);

        long lMaxWidth = pSimulatorDevice.getSimulator()
                                         .getCameraRenderer(c)
                                         .getMaxWidth();

        long lMaxHeight = pSimulatorDevice.getSimulator()
                                          .getCameraRenderer(c)
                                          .getMaxHeight();

        lCamera.getMaxWidthVariable().set(lMaxWidth);
        lCamera.getMaxHeightVariable().set(lMaxHeight);
        lCamera.getStackWidthVariable().set(lMaxWidth / 2);
        lCamera.getStackHeightVariable().set(lMaxHeight);
        lCamera.getExposureInSecondsVariable().set(0.010);

        // lCamera.getStackVariable().addSetListener((o,n)->
        // {System.out.println("camera output:"+n);} );

        addDevice(c, lCamera);

        lCameraList.add(lCamera);
      }
    }

    // Scaling Amplifier:
    {
      ScalingAmplifierDeviceInterface lScalingAmplifier1 =
                                                         new ScalingAmplifierSimulator("ScalingAmplifier1");
      addDevice(0, lScalingAmplifier1);

      ScalingAmplifierDeviceInterface lScalingAmplifier2 =
                                                         new ScalingAmplifierSimulator("ScalingAmplifier2");
      addDevice(1, lScalingAmplifier2);
    }

    // Signal generator:

    {
      SignalGeneratorSimulatorDevice lSignalGeneratorSimulatorDevice =
                                                                     new SignalGeneratorSimulatorDevice();

      // addDevice(0, lSignalGeneratorSimulatorDevice);
      lSignalGeneratorSimulatorDevice.getTriggerVariable()
                                     .sendUpdatesTo(lTrigger);/**/

      final LightSheetSignalGeneratorDevice lLightSheetSignalGeneratorDevice =
                                                                             LightSheetSignalGeneratorDevice.wrap(lSignalGeneratorSimulatorDevice,
                                                                                                                  pSharedLightSheetControl);

      addDevice(0, lLightSheetSignalGeneratorDevice);
    }

    // setting up staging score visualization:

    /*final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
                                                                              lStagingScore);/**/

    // Setting up detection arms:

    {
      for (int c = 0; c < lNumberOfDetectionArms; c++)
      {
        final DetectionArm lDetectionArm = new DetectionArm("D" + c);
        lDetectionArm.getPixelSizeInMicrometerVariable()
                     .set(pSimulatorDevice.getSimulator()
                                          .getPixelWidth(c));

        addDevice(c, lDetectionArm);
      }
    }

    // Setting up lightsheets:
    {
      for (int l = 0; l < lNumberOfLightSheets; l++)
      {
        final LightSheet lLightSheet =
                                     new LightSheet("I" + l,
                                                    9.4,
                                                    getNumberOfLaserLines());
        addDevice(l, lLightSheet);

      }
    }

    // Setting up lightsheets selector
    {
      LightSheetOpticalSwitch lLightSheetOpticalSwitch =
                                                       new LightSheetOpticalSwitch("OpticalSwitch",
                                                                                   lNumberOfLightSheets);

      addDevice(0, lLightSheetOpticalSwitch);
    }

    // Setting up simulator:
    {
      // Now that the microscope has been setup, we can connect the simulator to
      // it:

      // first, we connect the devices in the simulator so that parameter
      // changes
      // are forwarded:
      pSimulatorDevice.connectTo(this);

      // second, we make sure that the simulator is used as provider for the
      // simulated cameras:
      for (int c = 0; c < lNumberOfDetectionArms; c++)
      {
        StackCameraSimulationProvider lStackProvider;
        if (pDummySimulation)
          lStackProvider = new FractalStackProvider();
        else
          lStackProvider = pSimulatorDevice.getStackProvider(c);
        lCameraList.get(c)
                   .setStackCameraSimulationProvider(lStackProvider);
      }
    }

  }

  /**
   * Adds standard devices such as the acquisition state manager, calibrator and
   * Timelapse
   */
  @SuppressWarnings("unchecked")
  public void addStandardDevices()
  {

    // Adding calibrator:
    {
      CalibrationEngine lCalibrator = addCalibrator();
      lCalibrator.load();
    }

    // Setting up acquisition state manager:
    {
      AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>> lAcquisitionStateManager;
      lAcquisitionStateManager =
                               (AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>>) addAcquisitionStateManager();
      InterpolatedAcquisitionState lAcquisitionState =
                                                     new InterpolatedAcquisitionState("default",
                                                                                      this);
      lAcquisitionState.setupControlPlanes(7,
                                           ControlPlaneLayout.Circular);
      lAcquisitionState.copyCurrentMicroscopeSettings();
      lAcquisitionStateManager.setCurrentState(lAcquisitionState);
      addInteractiveAcquisition();

      // Adding adaptive engine device:
      {
        AdaptiveEngine<InterpolatedAcquisitionState> lAdaptiveEngine =
                                                                     addAdaptiveEngine(lAcquisitionState);
        lAdaptiveEngine.add(new AdaptationZ(7,
                                            1.66,
                                            0.95,
                                            2e-5,
                                            0.010,
                                            0.5));
        lAdaptiveEngine.add(new AdaptationX(11,
                                            50,
                                            200,
                                            0.95,
                                            2e-5,
                                            0.010,
                                            0.5));
      }

    }

    // Adding timelapse device:
    {
      TimelapseInterface lTimelapse = addTimelapse();

      lTimelapse.addFileStackSinkType(RawFileStackSink.class);
    }

  }

}
