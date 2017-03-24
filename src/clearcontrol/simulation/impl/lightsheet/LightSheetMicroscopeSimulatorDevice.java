package clearcontrol.simulation.impl.lightsheet;

import java.util.ArrayList;

import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.simulation.SampleSimulationDeviceBase;
import clearcontrol.simulation.SampleSimulatorDeviceInterface;
import simbryo.synthoscopy.microscope.lightsheet.LightSheetMicroscopeSimulator;
import simbryo.synthoscopy.microscope.parameters.IlluminationParameter;
import simbryo.synthoscopy.microscope.parameters.StageParameter;

/**
 * Lightsheet microscope simulator device
 *
 * @author royer
 */
public class LightSheetMicroscopeSimulatorDevice extends
                                                 SampleSimulationDeviceBase
                                                 implements
                                                 SampleSimulatorDeviceInterface
{

  private LightSheetMicroscopeSimulator mLightSheetMicroscopeSimulator;

  private ArrayList<LightSheetSimulationStackProvider> mLightSheetSimulationStackProviderList =
                                                                                              new ArrayList<>();

  /**
   * Instanciates a light sheet microscope simulator device
   * 
   * @param pLightSheetMicroscopeSimulator
   *          light sheet microscope simulator (from Simbryo project)
   * 
   */
  public LightSheetMicroscopeSimulatorDevice(LightSheetMicroscopeSimulator pLightSheetMicroscopeSimulator)
  {
    super();
    mLightSheetMicroscopeSimulator = pLightSheetMicroscopeSimulator;

  }

  private void setParameterForAllLightSheets(IlluminationParameter pParameter,
                                             Number pValue)
  {
    int lNumberOfLightSheets =
                             mLightSheetMicroscopeSimulator.getNumberOfLightSheets();

    for (int l = 0; l < lNumberOfLightSheets; l++)
      mLightSheetMicroscopeSimulator.setNumberParameter(pParameter,
                                                        l,
                                                        pValue);
  }

  @Override
  public LightSheetSimulationStackProvider getStackProvider(int pIndex)
  {
    return mLightSheetSimulationStackProviderList.get(pIndex);
  }

  @Override
  public void connectTo(MicroscopeInterface pMicroscope)
  {
    if (pMicroscope instanceof LightSheetMicroscopeInterface)
    {
      LightSheetMicroscopeInterface lLightSheetMicroscope =
                                                          (LightSheetMicroscopeInterface) pMicroscope;

      StageDeviceInterface lMainXYZRStage =
                                          lLightSheetMicroscope.getMainXYZRStage();

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
                                     n) -> mLightSheetMicroscopeSimulator.setNumberParameter(StageParameter.StageRZ,
                                                                                             0,
                                                                                             n));

      int lNumberOfCameras =
                           mLightSheetMicroscopeSimulator.getNumberOfDetectionPaths();
      for (int i = 0; i < lNumberOfCameras; i++)
      {
        LightSheetSimulationStackProvider lLightSheetSimulationStackProvider =
                                                                             new LightSheetSimulationStackProvider(lLightSheetMicroscope,
                                                                                                                   mLightSheetMicroscopeSimulator,
                                                                                                                   i);

        mLightSheetSimulationStackProviderList.add(lLightSheetSimulationStackProvider);
      }
    }
    else
      throw new IllegalArgumentException("Must be a lightsheet microsocpe");
  }

}
