package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.concurrent.Future;

import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.metadata.MetaDataChannel;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the lightsheet width.
 *
 * @author royer
 */
public class AdaptationW extends StandardAdaptationModule implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private static final int cRepeats = 2;

  /**
   * Instantiates a W adaptation module given the number of samples, probability
   * threshold, and image metric threshold
   * 
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   * @param pImageMetricThreshold
   *          image metric threshold
   * @param pExposureInSeconds
   *          exposure in seconds
   * @param pLaserPower
   *          laser power
   */
  public AdaptationW(int pNumberOfSamples,
                     double pProbabilityThreshold,
                     double pImageMetricThreshold,
                     double pExposureInSeconds,
                     double pLaserPower)
  {
    super("W",
          LightSheetDOF.IW,
          pNumberOfSamples,
          pProbabilityThreshold,
          pImageMetricThreshold,
          pExposureInSeconds,
          pLaserPower);
  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    LightSheetMicroscope lLightsheetMicroscope =
                                               (LightSheetMicroscope) getAdaptiveEngine().getMicroscope();

    LightSheetMicroscopeQueue lQueue =
                                     lLightsheetMicroscope.requestQueue();
    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptiveEngine().getAcquisitionStateVariable()
                                                                      .get();

    LightSheetInterface lLightSheetDevice =
                                          getAdaptiveEngine().getMicroscope()
                                                             .getDeviceLists()
                                                             .getDevice(LightSheetInterface.class,
                                                                        lLightSheetIndex);

    double lMinW = lLightSheetDevice.getWidthVariable()
                                    .getMin()
                                    .doubleValue();
    double lMaxW = lLightSheetDevice.getWidthVariable()
                                    .getMax()
                                    .doubleValue();

    int lNumberOfSamples = getNumberOfSamplesVariable().get();
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

    lQueue.addMetaDataEntry(MetaDataChannel.Channel, "NoDisplay");

    return findBestDOFValue(lControlPlaneIndex,
                            lLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lIWList);

  }

  /*
  @Override
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {
  
    info("CORRECTION HAPPENS HERE");
    
    int lBestDetectioArm =
                         getAdaptator().getCurrentAcquisitionStateVariable()
                                       .get()
                                       .getBestDetectionArm(pControlPlaneIndex);
    
    
    /*
     *     COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
     
    getAdaptator().getNewAcquisitionState()
                  .setAtControlPlaneIW(pControlPlaneIndex,
                                       pLightSheetIndex,
                                       pArgMaxList.get(lBestDetectioArm));
  }/**/

  @Override
  public void updateState(InterpolatedAcquisitionState pStateToUpdate)
  {
    // TODO Auto-generated method stub

  }

}
