package clearcontrol.microscope.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.ClearCLImage;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.ClearCLBackends;
import clearcl.enums.ImageChannelDataType;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
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
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.gui.LightSheetMicroscopeGUI;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.lightsheet.simulation.LightSheetMicroscopeSimulationDevice;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import clearcontrol.stack.sourcesink.sink.RawFileStackSink;
import simbryo.synthoscopy.microscope.aberration.Miscalibration;
import simbryo.synthoscopy.microscope.lightsheet.drosophila.LightSheetMicroscopeSimulatorDrosophila;
import simbryo.synthoscopy.microscope.parameters.PhantomParameter;
import simbryo.synthoscopy.microscope.parameters.UnitConversion;
import simbryo.textures.noise.UniformNoise;

/**
 * Simulated lightSheet microscope demo
 *
 * @author royer
 */
public class LightSheetMicroscopeDemo implements
                                      AsynchronousSchedulerServiceAccess
{

  /**
   * @throws Exception
   *           NA
   */
  @SuppressWarnings("unchecked")
  @Test
  public void demoSimulatedScope() throws Exception
  {
    boolean lDummySimulation = false;
    boolean lUniformFluorescence = false;

    boolean l2DDisplayFlag = true;
    boolean l3DDisplayFlag = true;

    int lMaxNumberOfStacks = 32;

    int lMaxCameraResolution = 1024;
    long lImageResolution = 1024;

    int lNumberOfLightSheets = 2;
    int lNumberOfDetectionArms = 2;

    float lDivisionTime = 11f;

    int lPhantomWidth = 256;
    int lPhantomHeight = lPhantomWidth;
    int lPhantomDepth = lPhantomWidth;

    ClearCLBackendInterface lBestBackend =
                                         ClearCLBackends.getBestBackend();

    ClearCL lClearCL = new ClearCL(lBestBackend);
    ClearCLDevice lSimulationGPUDevice =
                                       lClearCL.getDeviceByName("TITAN");
    ClearCLContext lContext = lSimulationGPUDevice.createContext();

    LightSheetMicroscopeSimulatorDrosophila lSimulator =
                                                       new LightSheetMicroscopeSimulatorDrosophila(lContext,
                                                                                                   lNumberOfDetectionArms,
                                                                                                   lNumberOfLightSheets,
                                                                                                   lMaxCameraResolution,
                                                                                                   lDivisionTime,
                                                                                                   lPhantomWidth,
                                                                                                   lPhantomHeight,
                                                                                                   lPhantomDepth);
    // lSimulator.openViewerForControls();
    lSimulator.setFreezedEmbryo(true);
    lSimulator.setNumberParameter(UnitConversion.Length, 0, 700f);

    lSimulator.addAbberation(new Miscalibration());
    // lSimulator.addAbberation(new SampleDrift());
    // lSimulator.addAbberation(new IlluminationMisalignment());
    // lSimulator.addAbberation(new DetectionMisalignment());

    /*scheduleAtFixedRate(() -> lSimulator.simulationSteps(1),
                        10,
                        TimeUnit.MILLISECONDS);/**/

    if (lUniformFluorescence)
    {
      long lEffPhantomWidth = lSimulator.getWidth();
      long lEffPhantomHeight = lSimulator.getHeight();
      long lEffPhantomDepth = lSimulator.getDepth();

      ClearCLImage lFluoPhantomImage =
                                     lContext.createSingleChannelImage(ImageChannelDataType.Float,
                                                                       lEffPhantomWidth,
                                                                       lEffPhantomHeight,
                                                                       lEffPhantomDepth);

      ClearCLImage lScatterPhantomImage =
                                        lContext.createSingleChannelImage(ImageChannelDataType.Float,
                                                                          lEffPhantomWidth / 2,
                                                                          lEffPhantomHeight / 2,
                                                                          lEffPhantomDepth / 2);

      UniformNoise lUniformNoise = new UniformNoise(3);
      lUniformNoise.setNormalizeTexture(false);
      lUniformNoise.setMin(0.25f);
      lUniformNoise.setMax(0.75f);
      lFluoPhantomImage.readFrom(lUniformNoise.generateTexture(lEffPhantomWidth,
                                                               lEffPhantomHeight,
                                                               lEffPhantomDepth),
                                 true);

      lUniformNoise.setMin(0.0001f);
      lUniformNoise.setMax(0.001f);
      lScatterPhantomImage.readFrom(lUniformNoise.generateTexture(lEffPhantomWidth
                                                                  / 2,
                                                                  lEffPhantomHeight
                                                                       / 2,
                                                                  lEffPhantomDepth
                                                                            / 2),
                                    true);

      lSimulator.setPhantomParameter(PhantomParameter.Fluorescence,
                                     lFluoPhantomImage);

      lSimulator.setPhantomParameter(PhantomParameter.Scattering,
                                     lScatterPhantomImage);
    }

    // lSimulator.openViewerForCameraImage(0);
    // lSimulator.openViewerForAllLightMaps();
    // lSimulator.openViewerForScatteringPhantom();

    LightSheetMicroscopeSimulationDevice lLightSheetMicroscopeSimulatorDevice =
                                                                              new LightSheetMicroscopeSimulationDevice(lSimulator);

    final LightSheetMicroscope lLightSheetMicroscope =
                                                     new LightSheetMicroscope("SimulatedMicroscopeDemo",
                                                                              lSimulationGPUDevice.createContext(),
                                                                              lMaxNumberOfStacks,
                                                                              1);

    // Setting up lasers:

    int[] lLaserWavelengths = new int[]
    { 488, 594 };
    ArrayList<LaserDeviceInterface> lLaserList = new ArrayList<>();
    for (int l = 0; l < lLaserWavelengths.length; l++)
    {
      LaserDeviceInterface lLaser = new LaserDeviceSimulator("Laser "
                                                             + lLaserWavelengths[l],
                                                             l,
                                                             lLaserWavelengths[l],
                                                             100);
      lLaserList.add(lLaser);
      lLightSheetMicroscope.addDevice(l, lLaser);
    }

    // Setting up Stage:

    StageDeviceSimulator lStageDeviceSimulator =
                                               new StageDeviceSimulator("Stage",
                                                                        StageType.XYZR,
                                                                        true);
    lStageDeviceSimulator.addXYZRDOFs();
    lStageDeviceSimulator.setSpeed(0.8);

    lLightSheetMicroscope.addDevice(0, lStageDeviceSimulator);

    // Setting up Filterwheel:

    int[] lFilterWheelPositions = new int[]
    { 0, 1, 2, 3 };
    FilterWheelDeviceInterface lFilterWheelDevice =
                                                  new FilterWheelDeviceSimulator("FilterWheel",
                                                                                 lFilterWheelPositions);
    lFilterWheelDevice.setPositionName(0, "405 filter");
    lFilterWheelDevice.setPositionName(1, "488 filter");
    lFilterWheelDevice.setPositionName(2, "561 filter");
    lFilterWheelDevice.setPositionName(3, "594 filter");
    lLightSheetMicroscope.getDeviceLists()
                         .addDevice(0, lFilterWheelDevice);

    ArrayList<StackCameraDeviceSimulator> lCameraList =
                                                      new ArrayList<>();

    // Setting up trigger:

    Variable<Boolean> lTrigger =
                               new Variable<Boolean>("CameraTrigger",
                                                     false);

    // Setting up cameras:
    for (int c = 0; c < lNumberOfDetectionArms; c++)
    {
      final StackCameraDeviceSimulator lCamera =
                                               new StackCameraDeviceSimulator("StackCamera"
                                                                              + c,
                                                                              lTrigger);

      lCamera.getStackWidthVariable().set(lImageResolution);
      lCamera.getStackHeightVariable().set(lImageResolution);
      lCamera.getExposureInSecondsVariable().set(0.010);

      // lCamera.getStackVariable().addSetListener((o,n)->
      // {System.out.println("camera output:"+n);} );

      lLightSheetMicroscope.addDevice(c, lCamera);

      lCameraList.add(lCamera);
    }

    // lLightSheetMicroscope.sendPipelineStacksToNull();

    // Scaling Amplifier:

    ScalingAmplifierDeviceInterface lScalingAmplifier1 =
                                                       new ScalingAmplifierSimulator("ScalingAmplifier1");
    lLightSheetMicroscope.addDevice(0, lScalingAmplifier1);

    ScalingAmplifierDeviceInterface lScalingAmplifier2 =
                                                       new ScalingAmplifierSimulator("ScalingAmplifier2");
    lLightSheetMicroscope.addDevice(1, lScalingAmplifier2);

    // Signal generator:

    SignalGeneratorSimulatorDevice lSignalGeneratorSimulatorDevice =
                                                                   new SignalGeneratorSimulatorDevice();

    /*lLightSheetMicroscope.addDevice(0,
                                    lSignalGeneratorSimulatorDevice);/**/
    lSignalGeneratorSimulatorDevice.getTriggerVariable()
                                   .sendUpdatesTo(lTrigger);

    final LightSheetSignalGeneratorDevice lLightSheetSignalGeneratorDevice =
                                                                           LightSheetSignalGeneratorDevice.wrap(lSignalGeneratorSimulatorDevice,
                                                                                                                false);

    lLightSheetMicroscope.addDevice(0,
                                    lLightSheetSignalGeneratorDevice);

    // setting up staging score visualization:

    /*final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
    																																					lStagingScore);/**/

    // Setting up detection path:

    for (int c = 0; c < lNumberOfDetectionArms; c++)
    {
      final DetectionArm lDetectionArm = new DetectionArm("D" + c);
      lDetectionArm.getPixelSizeInMicrometerVariable()
                   .set(lSimulator.getPixelWidth(c));

      lLightSheetMicroscope.addDevice(c, lDetectionArm);
    }

    // Setting up lightsheets:

    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      final LightSheet lLightSheet =
                                   new LightSheet("I" + l,
                                                  9.4,
                                                  lLightSheetMicroscope.getNumberOfLaserLines());
      lLightSheetMicroscope.addDevice(l, lLightSheet);

      lLightSheet.getHeightVariable().set(100.0);
      lLightSheet.getEffectiveExposureInSecondsVariable().set(0.010);

      lLightSheet.getImageHeightVariable().set(lImageResolution);
    }

    // Setting up lightsheets selector

    LightSheetOpticalSwitch lLightSheetOpticalSwitch =
                                                     new LightSheetOpticalSwitch("OpticalSwitch",
                                                                                 lNumberOfLightSheets);

    lLightSheetMicroscope.addDevice(0, lLightSheetOpticalSwitch);

    // Setting up acquisition state manager
    AcquisitionStateManager<InterpolatedAcquisitionState> lAcquisitionStateManager;
    lAcquisitionStateManager =
                             (AcquisitionStateManager<InterpolatedAcquisitionState>) lLightSheetMicroscope.addAcquisitionStateManager();
    InterpolatedAcquisitionState lAcquisitionState =
                                                   new InterpolatedAcquisitionState("default",
                                                                                    lLightSheetMicroscope);
    lAcquisitionState.setupControlPlanes(3, 30);
    lAcquisitionStateManager.setCurrentState(lAcquisitionState);
    lLightSheetMicroscope.addInteractiveAcquisition();

    // Adding calibrator:

    Calibrator lCalibrator = lLightSheetMicroscope.addCalibrator();
    lCalibrator.load();

    // Adding timelapse device:

    TimelapseInterface lTimelapse =
                                  lLightSheetMicroscope.addTimelapse();

    lTimelapse.addFileStackSinkType(RawFileStackSink.class);

    // Now that the microscope has been setup, we can connect the simulator to
    // it:

    // first, we connect the devices in the simulator so that parameter changes
    // are forwarded:
    lLightSheetMicroscopeSimulatorDevice.connectTo(lLightSheetMicroscope);

    // second, we make sure that the simulator is used as provider for the
    // simulated cameras:
    for (int c = 0; c < lNumberOfDetectionArms; c++)
    {
      StackCameraSimulationProvider lStackProvider;
      if (lDummySimulation)
        lStackProvider = new FractalStackProvider();
      else
        lStackProvider =
                       lLightSheetMicroscopeSimulatorDevice.getStackProvider(c);
      lCameraList.get(c)
                 .setStackCameraSimulationProvider(lStackProvider);
    }

    // setting up scope GUI:

    LightSheetMicroscopeGUI lMicroscopeGUI =
                                           new LightSheetMicroscopeGUI(lLightSheetMicroscope,
                                                                       l2DDisplayFlag,
                                                                       l3DDisplayFlag);
    // lMicroscopeGUI.addGroovyScripting("lsm");
    // lMicroscopeGUI.addJythonScripting("lsm");

    lMicroscopeGUI.setup();

    if (lMicroscopeGUI != null)
      assertTrue(lMicroscopeGUI.open());

    assertTrue(lLightSheetMicroscope.open());
    Thread.sleep(1000);
    lMicroscopeGUI.waitForVisible(true, 1L, TimeUnit.MINUTES);

    if (lMicroscopeGUI != null)
      lMicroscopeGUI.connectGUI();

    lMicroscopeGUI.waitForVisible(false, null, null);

    lMicroscopeGUI.disconnectGUI();

    /*
    if (false)
    {
    	System.out.println("Start building queue");
    
    	for (int i = 0; i < 128; i++)
    		lLightSheetMicroscope.addCurrentStateToQueue();
    	lLightSheetMicroscope.finalizeQueue();
    	System.out.println("finished building queue");
    
    	while (lVisualizer.isVisible())
    	{
    		System.out.println("playQueue!");
    		final FutureBooleanList lPlayQueue = lLightSheetMicroscope.playQueue();
    
    		System.out.print("waiting...");
    		final Boolean lBoolean = lPlayQueue.get();
    		System.out.print(" ...done!");
    		// System.out.println(lBoolean);
    		// Thread.sleep(4000);
    	}
    }
    else/**/

    assertTrue(lLightSheetMicroscope.close());
    if (lMicroscopeGUI != null)
      assertTrue(lMicroscopeGUI.close());

    lSimulator.close();

    lClearCL.close();

  }
}
