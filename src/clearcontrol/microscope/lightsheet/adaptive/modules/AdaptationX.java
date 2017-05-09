package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.concurrent.Future;

import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the X lightsheeet positions
 *
 * @author royer
 */
public class AdaptationX extends
                         StandardAdaptationModule<InterpolatedAcquisitionState>
                         implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  /**
   * Instanciates a X focus adaptation module given the number of samples and
   * probability threshold
   * 
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public AdaptationX(int pNumberOfSamples,
                     double pProbabilityThreshold)
  {
    super("X", pNumberOfSamples, pProbabilityThreshold);
  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    int pControlPlaneIndex = pStepCoordinates[0];
    int pLightSheetIndex = pStepCoordinates[1];

    LightSheetMicroscope lLightsheetMicroscope =
                                               (LightSheetMicroscope) getAdaptator().getMicroscope();

    LightSheetMicroscopeQueue lQueue =
                                     lLightsheetMicroscope.requestQueue();
    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptator().getCurrentAcquisitionStateVariable()
                                                                 .get();

    LightSheetInterface lLightSheetDevice =
                                          lLightsheetMicroscope.getDeviceLists()
                                                               .getDevice(LightSheetInterface.class,
                                                                          pLightSheetIndex);

    double lMinX = lLightSheetDevice.getXVariable()
                                    .getMin()
                                    .doubleValue();
    double lMaxX = lLightSheetDevice.getXVariable()
                                    .getMax()
                                    .doubleValue();

    int lNumberOfSamples = getNumberOfSamples();
    double lStepX = (lMaxX - lMinX) / (lNumberOfSamples - 1);

    double lCurrentX = lQueue.getIX(pLightSheetIndex);

    lQueue.clearQueue();

    lAcquisitionState.applyStateAtControlPlane(lQueue,
                                               pControlPlaneIndex);

    final TDoubleArrayList lIXList = new TDoubleArrayList();

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setIX(pLightSheetIndex, lMinX);
    lQueue.setI(pLightSheetIndex);
    lQueue.addCurrentStateToQueue();
    lQueue.addCurrentStateToQueue();

    lQueue.setILO(true);
    lQueue.setC(true);
    for (double x = lMinX; x <= lMaxX; x += lStepX)
    {
      lIXList.add(x);
      lQueue.setIX(pLightSheetIndex, x);
      lQueue.setI(pLightSheetIndex);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setIX(pLightSheetIndex, lCurrentX);
    lQueue.setI(pLightSheetIndex);
    lQueue.addCurrentStateToQueue();

    lQueue.finalizeQueue();

    return findBestDOFValue(pControlPlaneIndex,
                            pLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lIXList);

  }

  @Override
  public void updateNewState()
  {
    // TODO Auto-generated method stub

  }

  /*
  @Override
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {
    info("CORRECTIONS HAPPEN HERE");
    int lBestDetectioArm =
                         getAdaptator().getCurrentAcquisitionStateVariable()
                                       .get()
                                       .getBestDetectionArm(pControlPlaneIndex);/**/

  /*
   * 
   *    COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
  getAdaptator().getNewAcquisitionState()
                .setAtControlPlaneIX(pControlPlaneIndex,
                                     pLightSheetIndex,
                                     pArgMaxList.get(lBestDetectioArm));
  
  }  /**/

}
