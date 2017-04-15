package clearcontrol.microscope.lightsheet.autopilot.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import gnu.trove.list.array.TDoubleArrayList;

public class AdaptationW extends NDIteratorAdaptationModule
                         implements AdaptationModuleInterface
{

  private static final int cRepeats = 2;

  public AdaptationW(int pNumberOfSamples,
                     double pProbabilityThreshold)
  {
    super(pNumberOfSamples, pProbabilityThreshold);
  }

  @Override
  public Future<?> atomicStep(int pControlPlaneIndex,
                              int pLightSheetIndex,
                              int pNumberOfSamples)
  {
    LightSheetMicroscope lLSM =
                              getAdaptator().getLightSheetMicroscope();
    LightSheetAcquisitionStateInterface lStackAcquisition =
                                                          getAdaptator().getCurrentAcquisitionStateVariable()
                                                                        .get();

    LightSheetInterface lLightSheetDevice =
                                          lLSM.getDeviceLists()
                                              .getDevice(LightSheetInterface.class,
                                                         pLightSheetIndex);

    double lMinW = lLightSheetDevice.getWidthVariable()
                                    .getMin()
                                    .doubleValue();
    double lMaxW = lLightSheetDevice.getWidthVariable()
                                    .getMax()
                                    .doubleValue();
    double lStepW = (lMaxW - lMinW) / (pNumberOfSamples - 1);

    double lCurrentW = lLSM.getIW(pLightSheetIndex);

    lLSM.clearQueue();

    lStackAcquisition.applyStateAtControlPlane(pControlPlaneIndex);

    final TDoubleArrayList lIWList = new TDoubleArrayList();

    lLSM.setC(false);
    lLSM.setILO(false);
    lLSM.setIW(pLightSheetIndex, lMinW);
    lLSM.setI(pLightSheetIndex);
    for (int r = 0; r < cRepeats; r++)
      lLSM.addCurrentStateToQueue();

    for (double w = lMinW; w <= lMaxW; w += lStepW)
    {
      lIWList.add(w);
      lLSM.setIW(pLightSheetIndex, w);

      lLSM.setILO(false);
      lLSM.setC(false);
      lLSM.setI(pLightSheetIndex);
      for (int r = 0; r < cRepeats; r++)
        lLSM.addCurrentStateToQueue();

      lLSM.setILO(true);
      lLSM.setC(true);
      lLSM.setI(pLightSheetIndex);
      lLSM.addCurrentStateToQueue();
    }

    lLSM.setC(false);
    lLSM.setILO(false);
    lLSM.setIW(pLightSheetIndex, lCurrentW);
    lLSM.setI(pLightSheetIndex);
    for (int r = 0; r < cRepeats; r++)
      lLSM.addCurrentStateToQueue();

    lLSM.finalizeQueue();

    return findBestDOFValue(pControlPlaneIndex,
                            pLightSheetIndex,
                            lLSM,
                            lStackAcquisition,
                            lIWList);

  }

  @Override
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {

    int lBestDetectioArm =
                         getAdaptator().getCurrentAcquisitionStateVariable()
                                       .get()
                                       .getBestDetectionArm(pControlPlaneIndex);

    /*
     *     COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
     
    getAdaptator().getNewAcquisitionState()
                  .setAtControlPlaneIW(pControlPlaneIndex,
                                       pLightSheetIndex,
                                       pArgMaxList.get(lBestDetectioArm));/**/
  }

}
