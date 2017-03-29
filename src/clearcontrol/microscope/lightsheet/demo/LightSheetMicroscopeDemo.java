package clearcontrol.microscope.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.ClearCLBackends;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
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
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.gui.LightSheetMicroscopeGUI;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.simulation.impl.lightsheet.LightSheetMicroscopeSimulationDevice;
import clearcontrol.stack.processor.StackIdentityPipeline;

import org.junit.Test;

import simbryo.synthoscopy.microscope.lightsheet.drosophila.LightSheetMicroscopeSimulatorDrosophila;
import simbryo.synthoscopy.microscope.parameters.UnitConversion;

/**
 * Simulated lightSheet microscope demo
 *
 * @author royer
 */
public class LightSheetMicroscopeDemo
{

  private static final long cImageResolution = 1024;

  /**
   * @throws Exception
   *           NA
   */
  @Test
  public void demoSimulatedScope() throws Exception
  {

    int lNumberOfLightSheet = 1;
    int lNumberOfDetectionArms = 1;

    // Setting up sample simulator:
    /*StackCameraSimulationProvider lStackCameraSimulationProvider =
                                                                 new FractalStackProvider();/**/

    int lPhantomWidth = 320;
    int lPhantomHeight = lPhantomWidth;
    int lPhantomDepth = lPhantomWidth;

    ClearCLBackendInterface lBestBackend =
                                         ClearCLBackends.getBestBackend();

    ClearCL lClearCL = new ClearCL(lBestBackend);
    ClearCLDevice lFastestGPUDevice = lClearCL.getDeviceByName("HD");
    ClearCLContext lContext = lFastestGPUDevice.createContext();
    LightSheetMicroscopeSimulatorDrosophila lSimulator =
                                                       new LightSheetMicroscopeSimulatorDrosophila(lContext,
                                                                                                   1,
                                                                                                   1,
                                                                                                   1024,
                                                                                                   11f,
                                                                                                   lPhantomWidth,
                                                                                                   lPhantomHeight,
                                                                                                   lPhantomDepth);
    lSimulator.setNumberParameter(UnitConversion.Length, 0, 700f);
    lSimulator.openViewerForCameraImage(0);

    LightSheetMicroscopeSimulationDevice lLightSheetMicroscopeSimulatorDevice =
                                                                              new LightSheetMicroscopeSimulationDevice(lSimulator);

    final LightSheetMicroscope lLightSheetMicroscope =
                                                     new LightSheetMicroscope("demoscope");

    // Setting up lasers:

    int[] lLaserWavelengths = new int[]
    { 405, 488, 561, 594 };
    ArrayList<LaserDeviceInterface> lLaserList = new ArrayList<>();
    for (int l = 0; l < 3; l++)
    {
      LaserDeviceInterface lLaser =
                                  new LaserDeviceSimulator("Laser"
                                                           + l,
                                                           l,
                                                           lLaserWavelengths[l],
                                                           100 + 10
                                                                 * l);
      lLaserList.add(lLaser);
      lLightSheetMicroscope.addDevice(l, lLaser);
    }

    // Setting up Stage:

    StageDeviceSimulator lStageDeviceSimulator =
                                               new StageDeviceSimulator("Stage",
                                                                        StageType.XYZR);
    lStageDeviceSimulator.addXYZRDOFs();

    lLightSheetMicroscope.addDevice(0, lStageDeviceSimulator);
    lLightSheetMicroscope.setMainXYZRStage(lStageDeviceSimulator);

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

      final StackIdentityPipeline lStackIdentityPipeline =
                                                         new StackIdentityPipeline();

      lStackIdentityPipeline.getOutputVariable()
                            .addSetListener((pCurrentValue,
                                             pNewValue) -> {
                              /*System.out.println("StackIdentityPipeline"
                                                 + lCamera.getName()
                                                 + "->"
                                                 + pNewValue);/**/

                            });

      lCamera.getStackWidthVariable().set(cImageResolution);
      lCamera.getStackHeightVariable().set(cImageResolution);
      lCamera.getExposureInMicrosecondsVariable().set(5000.0);

      lLightSheetMicroscope.addDevice(c, lCamera);

      lLightSheetMicroscope.setStackProcessingPipeline(c,
                                                       lStackIdentityPipeline);
      lCameraList.add(lCamera);
    }

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

    lLightSheetMicroscope.addDevice(0,
                                    lSignalGeneratorSimulatorDevice);
    lSignalGeneratorSimulatorDevice.getTriggerVariable()
                                   .sendUpdatesTo(lTrigger);/**/

    final LightSheetSignalGeneratorDevice lLightSheetSignalGeneratorDevice =
                                                                           LightSheetSignalGeneratorDevice.wrap(lSignalGeneratorSimulatorDevice);

    lLightSheetMicroscope.addDevice(0,
                                    lLightSheetSignalGeneratorDevice);

    // setting up staging score visualization:

    /*final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
    																																					lStagingScore);/**/

    // Setting up detection path:

    for (int c = 0; c < lNumberOfDetectionArms; c++)
    {
      final DetectionArm lDetectionArm = new DetectionArm("D" + c);

      lLightSheetMicroscope.addDevice(c, lDetectionArm);
    }

    // Setting up lightsheets:

    for (int l = 0; l < lNumberOfLightSheet; l++)
    {
      final LightSheet lLightSheet = new LightSheet("I" + l,
                                                    9.4,
                                                    512,
                                                    2);
      lLightSheetMicroscope.addDevice(l, lLightSheet);

      lLightSheet.getHeightVariable().set(100.0);
      lLightSheet.getEffectiveExposureInMicrosecondsVariable()
                 .set(5000.0);

      lLightSheet.getImageHeightVariable().set(cImageResolution);
    }

    // Setting up lightsheets selector

    LightSheetOpticalSwitch lLightSheetOpticalSwitch =
                                                     new LightSheetOpticalSwitch("OpticalSwitch",
                                                                                 lNumberOfLightSheet);

    lLightSheetMicroscope.addDevice(0, lLightSheetOpticalSwitch);

    AcquisitionStateManager lAddAcquisitionStateManager =
                                                        lLightSheetMicroscope.addAcquisitionStateManager();

    lLightSheetMicroscope.addInteractiveAcquisition(lAddAcquisitionStateManager);
    lLightSheetMicroscope.addCalibrator();

    // Now that the microscope has been setup, we can connect the simulator to
    // it:

    // first, we connect the devices in the simulator so that parameter changes
    // are forwarded:
    lLightSheetMicroscopeSimulatorDevice.connectTo(lLightSheetMicroscope);

    // second, we make sure that the simulator is used as provider for the
    // simulated cameras:
    for (int c = 0; c < lNumberOfDetectionArms; c++)
      lCameraList.get(c)
                 .setStackCameraSimulationProvider(lLightSheetMicroscopeSimulatorDevice.getStackProvider(c));

    // setting up scope GUI:

    LightSheetMicroscopeGUI lMicroscopeGUI =
                                           new LightSheetMicroscopeGUI(lLightSheetMicroscope,
                                                                       true,
                                                                       true);
    // lMicroscopeGUI.addGroovyScripting("lsm");
    // lMicroscopeGUI.addJythonScripting("lsm");

    lMicroscopeGUI.generate();

    if (lMicroscopeGUI != null)
      assertTrue(lMicroscopeGUI.open());
    else
      lLightSheetMicroscope.sendStacksToNull();

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
