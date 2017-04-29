package clearcontrol.microscope.lightsheet.adaptor.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the lightsheet width.
 *
 * @author royer
 */
public class AdaptationW extends
                         StandardAdaptationModule<InterpolatedAcquisitionState>
                         implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private static final int cRepeats = 2;

  /**
   * Instanciates a W adaptation module given the deltaz parameter, number of
   * samples and probability threshold
   * 
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public AdaptationW(int pNumberOfSamples,
                     double pProbabilityThreshold)
  {
    super("W", pNumberOfSamples, pProbabilityThreshold);
  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    LightSheetMicroscopeQueue lQueue =
                                     getAdaptator().getLightSheetMicroscope()
                                                   .requestQueue();
    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptator().getCurrentAcquisitionStateVariable()
                                                                 .get();

    LightSheetInterface lLightSheetDevice =
                                          getAdaptator().getLightSheetMicroscope()
                                                        .getDeviceLists()
                                                        .getDevice(LightSheetInterface.class,
                                                                   lLightSheetIndex);

    double lMinW = lLightSheetDevice.getWidthVariable()
                                    .getMin()
                                    .doubleValue();
    double lMaxW = lLightSheetDevice.getWidthVariable()
                                    .getMax()
                                    .doubleValue();

    int lNumberOfSamples = getNumberOfSamples();
    double lStepW = (lMaxW - lMinW) / (lNumberOfSamples - 1);

    double lCurrentW = lQueue.getIW(lLightSheetIndex);

    lQueue.clearQueue();

    lAcquisitionState.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);

    final TDoubleArrayList lIWList = new TDoubleArrayList();

    lQueue.setC(false);
    lQueue.setILO(false);
    lQueue.setIW(lLightSheetIndex, lMinW);
    lQueue.setI(lLightSheetIndex);
    for (int r = 0; r < cRepeats; r++)
      lQueue.addCurrentStateToQueue();

    for (double w = lMinW; w <= lMaxW; w += lStepW)
    {
      lIWList.add(w);
      lQueue.setIW(lLightSheetIndex, w);

      lQueue.setILO(false);
      lQueue.setC(false);
      lQueue.setI(lLightSheetIndex);
      for (int r = 0; r < cRepeats; r++)
        lQueue.addCurrentStateToQueue();

      lQueue.setILO(true);
      lQueue.setC(true);
      lQueue.setI(lLightSheetIndex);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setC(false);
    lQueue.setILO(false);
    lQueue.setIW(lLightSheetIndex, lCurrentW);
    lQueue.setI(lLightSheetIndex);
    for (int r = 0; r < cRepeats; r++)
      lQueue.addCurrentStateToQueue();

    lQueue.finalizeQueue();

    return findBestDOFValue(lControlPlaneIndex,
                            lLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lIWList);

  }

  @Override
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {

    info("CORRECTION HAPPENS HERE");
    /*
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
