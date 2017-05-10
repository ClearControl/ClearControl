package clearcontrol.microscope.lightsheet.simulation;

import java.util.ArrayList;

import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.simulation.SampleSimulationDeviceBase;
import clearcontrol.simulation.SampleSimulationDeviceInterface;
import simbryo.synthoscopy.microscope.lightsheet.LightSheetMicroscopeSimulator;
import simbryo.synthoscopy.microscope.parameters.StageParameter;

/**
 * Lightsheet microscope simulator device
 *
 * @author royer
 */
public class LightSheetMicroscopeSimulationDevice extends
                                                  SampleSimulationDeviceBase<LightSheetMicroscopeQueue>
                                                  implements
                                                  SampleSimulationDeviceInterface<LightSheetMicroscopeQueue>
{

  private LightSheetMicroscopeSimulator mLightSheetMicroscopeSimulator;

  private ArrayList<LightSheetSimulationStackProvider> mLightSheetSimulationStackProviderList =
                                                                                              new ArrayList<>();

  /**
   * Instantiates a light sheet microscope simulator device
   * 
   * @param pLightSheetMicroscopeSimulator
   *          light sheet microscope simulator (from Simbryo project)
   * 
   */
  public LightSheetMicroscopeSimulationDevice(LightSheetMicroscopeSimulator pLightSheetMicroscopeSimulator)
  {
    super();
    mLightSheetMicroscopeSimulator = pLightSheetMicroscopeSimulator;
  }

  /**
   * Returns the underlying simulator.
   * 
   * @return simulator
   */
  public LightSheetMicroscopeSimulator getSimulator()
  {
    return mLightSheetMicroscopeSimulator;
  }

  @Override
  public LightSheetSimulationStackProvider getStackProvider(int pIndex)
  {
    return mLightSheetSimulationStackProviderList.get(pIndex);
  }

  @Override
  public void connectTo(MicroscopeInterface<LightSheetMicroscopeQueue> pMicroscope)
  {
    if (!(pMicroscope instanceof LightSheetMicroscopeInterface))
      throw new IllegalArgumentException("Must be a lightsheet microscope");

    LightSheetMicroscopeInterface lLightSheetMicroscope =
                                                        (LightSheetMicroscopeInterface) pMicroscope;

    StageDeviceInterface lMainXYZRStage =
                                        lLightSheetMicroscope.getMainStage();

    if (lMainXYZRStage != null)
    {

      lMainXYZRStage.getCurrentPositionVariable(0)
                    .addSetListener((o,
                                     n) -> mLightSheetMicroscopeSimulator.setNumberParameter(StageParameter.StageX,
                                                                                             0,
                                                                                             n));

      lMainXYZRStage.getCurrentPositionVariable(1)
                    .addSetListener((o,
                                     n) -> mLightSheetMicroscopeSimulator.setNumberParameter(StageParameter.StageY,
                                                                                             0,
                                                                                             n));

      lMainXYZRStage.getCurrentPositionVariable(2)
                    .addSetListener((o,
                                     n) -> mLightSheetMicroscopeSimulator.setNumberParameter(StageParameter.StageZ,
                                                                                             0,
                                                                                             n));

      lMainXYZRStage.getCurrentPositionVariable(3)
                    .addSetListener((o,
                                     n) -> mLightSheetMicroscopeSimulator.setNumberParameter(StageParameter.StageRY,
                                                                                             0,
                                                                                             n));
    }

    int lNumberOfCameras =
                         mLightSheetMicroscopeSimulator.getNumberOfDetectionArms();
    for (int i = 0; i < lNumberOfCameras; i++)
    {
      LightSheetSimulationStackProvider lLightSheetSimulationStackProvider =
                                                                           new LightSheetSimulationStackProvider(lLightSheetMicroscope,
                                                                                                                 mLightSheetMicroscopeSimulator,
                                                                                                                 i);

      mLightSheetSimulationStackProviderList.add(lLightSheetSimulationStackProvider);
    }

  }

}
