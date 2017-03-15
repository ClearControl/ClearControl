package clearcontrol.microscope.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.hardware.lasers.LaserDeviceInterface;
import clearcontrol.hardware.lasers.devices.sim.LaserDeviceSimulator;
import clearcontrol.hardware.optomech.filterwheels.FilterWheelDeviceInterface;
import clearcontrol.hardware.optomech.filterwheels.devices.sim.FilterWheelDeviceSimulator;
import clearcontrol.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import clearcontrol.hardware.optomech.opticalswitch.devices.sim.OpticalSwitchDeviceSimulator;
import clearcontrol.hardware.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.hardware.signalamp.devices.sim.ScalingAmplifierSimulator;
import clearcontrol.hardware.signalgen.SignalGeneratorInterface;
import clearcontrol.hardware.signalgen.devices.sim.SignalGeneratorSimulatorDevice;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.score.ScoreInterface;
import clearcontrol.hardware.stages.StageType;
import clearcontrol.hardware.stages.devices.sim.StageDeviceSimulator;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.gui.LightSheetMicroscopeGUI;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.processor.StackIdentityPipeline;
import clearcontrol.stack.sourcesink.RandomStackSource;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

import org.junit.Test;

public class LightSheetMicroscopeDemo
{

  private static final long cImageResolution = 2048;

  @Test
  public void demoSimulatedScope() throws InterruptedException,
                                   ExecutionException
  {

    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    final RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                    new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                    10);
    RandomStackSource lRandomStackSource =
                                         new RandomStackSource(100L,
                                                               101L,
                                                               103L,
                                                               lRecycler);

    Variable<Boolean> lTrigger =
                               new Variable<Boolean>("CameraTrigger",
                                                     false);

    final LightSheetMicroscope lLightSheetMicroscope =
                                                     new LightSheetMicroscope("demoscope");

    // Setting up lasers:

    int[] lLaserWavelengths = new int[]
    { 405, 488, 561, 594 };
    for (int l = 0; l < 3; l++)
    {
      LaserDeviceInterface lLaser =
                                  new LaserDeviceSimulator("Laser"
                                                           + l,
                                                           l,
                                                           lLaserWavelengths[l],
                                                           100 + 10
                                                                 * l);
      lLightSheetMicroscope.addDevice(l, lLaser);
    }

    // Setting up Stage:

    StageDeviceSimulator lStageDeviceSimulator =
                                               new StageDeviceSimulator("Stage",
                                                                        StageType.XYZR);
    lStageDeviceSimulator.addXYZRDOFs();

    lLightSheetMicroscope.addDevice(0, lStageDeviceSimulator);

    // Setting up optical switch:

    OpticalSwitchDeviceInterface lOpticalSwitchDeviceSimulator =
                                                               new OpticalSwitchDeviceSimulator("OpticalSwitch",
                                                                                                4);
    lLightSheetMicroscope.addDevice(0, lOpticalSwitchDeviceSimulator);

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

    ArrayList<StackCameraDeviceInterface> lCameraList =
                                                      new ArrayList<>();

    // Setting up cameras:
    for (int c = 0; c < 2; c++)
    {
      final StackCameraDeviceInterface lCamera =
                                               new StackCameraDeviceSimulator("StackCamera"
                                                                              + c,
                                                                              lRandomStackSource,
                                                                              lTrigger);

      final StackIdentityPipeline lStackIdentityPipeline =
                                                         new StackIdentityPipeline();

      lStackIdentityPipeline.getOutputVariable()
                            .addSetListener((pCurrentValue,
                                             pNewValue) -> {
                              System.out.println("StackIdentityPipeline"
                                                 + lCamera.getName()
                                                 + "->"
                                                 + pNewValue);

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

    final SignalGeneratorInterface lSignalGeneratorDevice =
                                                          new SignalGeneratorSimulatorDevice();

    lLightSheetMicroscope.addDevice(0, lSignalGeneratorDevice);

    // Setting up staging movements:

    final Movement lBeforeExposureMovement =
                                           new Movement("BeforeExposure");
    final Movement lExposureMovement = new Movement("Exposure");

    final ScoreInterface lStagingScore =
                                       lSignalGeneratorDevice.getStagingScore();

    lStagingScore.addMovement(lBeforeExposureMovement);
    lStagingScore.addMovement(lExposureMovement);

    // setting up staging score visualization:

    /*final ScoreVisualizerJFrame lVisualizer = ScoreVisualizerJFrame.visualize("LightSheetDemo",
    																																					lStagingScore);/**/

    // Setting up detection path:

    for (int c = 0; c < 2; c++)
    {
      final DetectionArm lDetectionArm =
                                       new DetectionArm("D" + c,
                                                        lCameraList.get(c));

      lLightSheetMicroscope.addDevice(c, lDetectionArm);

      lDetectionArm.addStavesToBeforeExposureMovement(lBeforeExposureMovement);
      lDetectionArm.addStavesToExposureMovement(lExposureMovement);
    }

    // Setting up lightsheets:

    for (int l = 0; l < 4; l++)
    {
      final LightSheet lLightSheet = new LightSheet("I" + l,
                                                    9.4,
                                                    512,
                                                    2);
      lLightSheetMicroscope.addDevice(l, lLightSheet);

      lBeforeExposureMovement.setDuration(lLightSheet.getBeforeExposureMovementDuration(TimeUnit.NANOSECONDS),
                                          TimeUnit.NANOSECONDS);
      lExposureMovement.setDuration(lLightSheet.getExposureMovementDuration(TimeUnit.NANOSECONDS),
                                    TimeUnit.NANOSECONDS);

      lLightSheet.setBeforeExposureMovement(lBeforeExposureMovement);
      lLightSheet.setExposureMovement(lExposureMovement);

      lLightSheet.getHeightVariable().set(100.0);
      lLightSheet.getEffectiveExposureInMicrosecondsVariable()
                 .set(5000.0);

      lLightSheet.getImageHeightVariable().set(cImageResolution);
    }

    AcquisitionStateManager lAddAcquisitionStateManager =
                                                        lLightSheetMicroscope.addAcquisitionStateManager();

    lLightSheetMicroscope.addInteractiveAcquisition(lAddAcquisitionStateManager);
    lLightSheetMicroscope.addCalibrator();

    // setting up scope GUI:

    LightSheetMicroscopeGUI lMicroscopeGUI =
                                           new LightSheetMicroscopeGUI(lLightSheetMicroscope,
                                                                       true,
                                                                       true);

    lMicroscopeGUI.addGroovyScripting("lsm");
    lMicroscopeGUI.addJythonScripting("lsm");

    lMicroscopeGUI.generate();

    if (lMicroscopeGUI != null)
      assertTrue(lMicroscopeGUI.open());
    else
      lLightSheetMicroscope.sendStacksToNull();

    assertTrue(lLightSheetMicroscope.open());
    Thread.sleep(1000);

    // if (lGUI != null)
    // lGUI.connectGUI();

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
    {
      while (lMicroscopeGUI.isVisible())
      {
        ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
      }
    }

    assertTrue(lLightSheetMicroscope.close());
    if (lMicroscopeGUI != null)
      assertTrue(lMicroscopeGUI.close());

  }
}
