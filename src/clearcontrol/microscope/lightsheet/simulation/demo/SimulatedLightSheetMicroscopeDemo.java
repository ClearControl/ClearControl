package clearcontrol.microscope.lightsheet.simulation.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.ClearCLImage;
import clearcl.backend.ClearCLBackends;
import clearcl.enums.ImageChannelDataType;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.microscope.lightsheet.gui.LightSheetMicroscopeGUI;
import clearcontrol.microscope.lightsheet.simulation.LightSheetMicroscopeSimulationDevice;
import clearcontrol.microscope.lightsheet.simulation.SimulatedLightSheetMicroscope;
import simbryo.synthoscopy.microscope.lightsheet.drosophila.LightSheetMicroscopeSimulatorDrosophila;
import simbryo.synthoscopy.microscope.parameters.PhantomParameter;
import simbryo.synthoscopy.microscope.parameters.UnitConversion;
import simbryo.textures.noise.UniformNoise;

/**
 * Simulated lightsheet microscope demo
 *
 * @author royer
 */
public class SimulatedLightSheetMicroscopeDemo
{

  /**
   * Demo
   * 
   * @throws Exception
   *           NA
   */
  @Test
  public void demo() throws Exception
  {
    boolean lDummySimulation = false;
    boolean lUniformFluorescence = false;

    boolean l2DDisplayFlag = true;
    boolean l3DDisplayFlag = true;

    int lMaxNumberOfStacks = 32;

    int lMaxCameraResolution = 1024;

    int lNumberOfLightSheets = 4;
    int lNumberOfDetectionArms = 2;

    float lDivisionTime = 11f;

    int lPhantomWidth = 256;
    int lPhantomHeight = lPhantomWidth;
    int lPhantomDepth = lPhantomWidth;

    ClearCL lClearCL = new ClearCL(ClearCLBackends.getBestBackend());

    for (ClearCLDevice lClearCLDevice : lClearCL.getAllDevices())
      System.out.println(lClearCLDevice.getName());

    ClearCLContext lSimulationContext =
                                      getClearCLDeviceByName(lClearCL,
                                                             "NVIDIA");

    ClearCLContext lMicroscopeContext =
                                      getClearCLDeviceByName(lClearCL,
                                                             "NVIDIA");

    LightSheetMicroscopeSimulationDevice lSimulatorDevice =
                                                          getSimulatorDevice(lSimulationContext,
                                                                             lNumberOfDetectionArms,
                                                                             lNumberOfLightSheets,
                                                                             lMaxCameraResolution,
                                                                             lDivisionTime,
                                                                             lPhantomWidth,
                                                                             lPhantomHeight,
                                                                             lPhantomDepth,
                                                                             lUniformFluorescence);

    SimulatedLightSheetMicroscope lMicroscope =
                                              new SimulatedLightSheetMicroscope("SimulatedLightSheetMicroscope",
                                                                                lMicroscopeContext,
                                                                                lMaxNumberOfStacks,
                                                                                1);
    lMicroscope.addSimulatedDevices(lDummySimulation,
                                    lSimulatorDevice);

    lMicroscope.addStandardDevices();

    if (lMicroscope.open())
      if (lMicroscope.start())
      {

        LightSheetMicroscopeGUI lMicroscopeGUI =
                                               new LightSheetMicroscopeGUI(lMicroscope,
                                                                           l2DDisplayFlag,
                                                                           l3DDisplayFlag);

        lMicroscopeGUI.setup();

        assertTrue(lMicroscopeGUI.open());

        ThreadUtils.sleep(1000, TimeUnit.MILLISECONDS);
        lMicroscopeGUI.waitForVisible(true, 1L, TimeUnit.MINUTES);

        lMicroscopeGUI.connectGUI();

        lMicroscopeGUI.waitForVisible(false, null, null);

        lMicroscopeGUI.disconnectGUI();
        lMicroscopeGUI.close();

        lMicroscope.stop();
        lMicroscope.close();
      }

    lSimulatorDevice.getSimulator().close();

    lClearCL.close();

  }

  protected ClearCLContext getClearCLDeviceByName(ClearCL pClearCL,
                                                  String lDeviceName)
  {
    ClearCLDevice lSimulationGPUDevice =
                                       pClearCL.getFastestGPUDeviceForImages(); // (lDeviceName);
    ClearCLContext lSimulationContext =
                                      lSimulationGPUDevice.createContext();
    return lSimulationContext;
  }

  /**
   * Returns a simulator device
   * 
   * @param pSimulationContext
   * @param pNumberOfDetectionArms
   * @param pNumberOfLightSheets
   * @param pMaxCameraResolution
   * @param pDivisionTime
   * @param pPhantomWidth
   * @param pPhantomHeight
   * @param pPhantomDepth
   * @param pUniformFluorescence
   * @return
   */
  @SuppressWarnings("javadoc")
  public LightSheetMicroscopeSimulationDevice getSimulatorDevice(ClearCLContext pSimulationContext,
                                                                 int pNumberOfDetectionArms,
                                                                 int pNumberOfLightSheets,
                                                                 int pMaxCameraResolution,
                                                                 float pDivisionTime,
                                                                 int pPhantomWidth,
                                                                 int pPhantomHeight,
                                                                 int pPhantomDepth,
                                                                 boolean pUniformFluorescence)
  {

    LightSheetMicroscopeSimulatorDrosophila lSimulator =
                                                       new LightSheetMicroscopeSimulatorDrosophila(pSimulationContext,
                                                                                                   pNumberOfDetectionArms,
                                                                                                   pNumberOfLightSheets,
                                                                                                   pMaxCameraResolution,
                                                                                                   pDivisionTime,
                                                                                                   pPhantomWidth,
                                                                                                   pPhantomHeight,
                                                                                                   pPhantomDepth);
    // lSimulator.openViewerForControls();
    lSimulator.setFreezedEmbryo(true);
    lSimulator.setNumberParameter(UnitConversion.Length, 0, 700f);

    // lSimulator.addAbberation(new SampleDrift());
    // lSimulator.addAbberation(new IlluminationMisalignment());
    // lSimulator.addAbberation(new DetectionMisalignment());

    /*scheduleAtFixedRate(() -> lSimulator.simulationSteps(1),
    10,
    TimeUnit.MILLISECONDS);/**/

    if (pUniformFluorescence)
    {
      long lEffPhantomWidth = lSimulator.getWidth();
      long lEffPhantomHeight = lSimulator.getHeight();
      long lEffPhantomDepth = lSimulator.getDepth();

      ClearCLImage lFluoPhantomImage =
                                     pSimulationContext.createSingleChannelImage(ImageChannelDataType.Float,
                                                                                 lEffPhantomWidth,
                                                                                 lEffPhantomHeight,
                                                                                 lEffPhantomDepth);

      ClearCLImage lScatterPhantomImage =
                                        pSimulationContext.createSingleChannelImage(ImageChannelDataType.Float,
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

    return lLightSheetMicroscopeSimulatorDevice;
  }

}
