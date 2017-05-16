package clearcontrol.microscope.lightsheet.simulation;

import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.enums.ImageChannelDataType;
import simbryo.synthoscopy.microscope.aberration.IlluminationMisalignment;
import simbryo.synthoscopy.microscope.lightsheet.drosophila.LightSheetMicroscopeSimulatorDrosophila;
import simbryo.synthoscopy.microscope.parameters.PhantomParameter;
import simbryo.synthoscopy.microscope.parameters.UnitConversion;
import simbryo.textures.noise.UniformNoise;

/**
 * Simulation utils
 *
 * @author royer
 */
public class SimulationUtils
{
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
  public static LightSheetMicroscopeSimulationDevice getSimulatorDevice(ClearCLContext pSimulationContext,
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

    // lSimulator.addAbberation(new Miscalibration());
    // lSimulator.addAbberation(new SampleDrift());
    lSimulator.addAbberation(IlluminationMisalignment.buildXYZ(0,
                                                               0,
                                                               0));
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
