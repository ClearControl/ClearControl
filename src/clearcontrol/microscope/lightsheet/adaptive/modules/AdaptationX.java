package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.concurrent.Future;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.metadata.MetaDataChannel;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the X lightsheeet positions
 *
 * @author royer
 */
public class AdaptationX extends StandardAdaptationModule implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private final Variable<Double> mDeltaXVariable =
                                                 new Variable<Double>("DeltaX",
                                                                      20.0);

  /**
   * Instantiates a X focus adaptation module given the number of samples,
   * probability threshold, and image metric threshold
   * 
   * @param pNumberOfSamples
   *          number of samples
   * @param pDeltaX
   *          delta X
   * @param pProbabilityThreshold
   *          probability threshold
   * @param pImageMetricThreshold
   *          image metric threshold
   * @param pExposureInSeconds
   *          exposure in seconds
   * @param pLaserPower
   *          laser power
   */
  public AdaptationX(int pNumberOfSamples,
                     double pDeltaX,
                     double pProbabilityThreshold,
                     double pImageMetricThreshold,
                     double pExposureInSeconds,
                     double pLaserPower)
  {
    super("X",
          LightSheetDOF.IX,
          pNumberOfSamples,
          pProbabilityThreshold,
          pImageMetricThreshold,
          pExposureInSeconds,
          pLaserPower);

    getDeltaXVariable().set(pDeltaX);
  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    LightSheetMicroscope lLightsheetMicroscope =
                                               (LightSheetMicroscope) getAdaptiveEngine().getMicroscope();

    double lDeltaX = getDeltaXVariable().get();
    int lNumberOfSamples = getNumberOfSamplesVariable().get();
    int lHalfSamples = (lNumberOfSamples - 1) / 2;
    double lMinX = -lDeltaX * lHalfSamples;
    double lMaxX = lDeltaX * lHalfSamples;

    LightSheetMicroscopeQueue lQueue =
                                     lLightsheetMicroscope.requestQueue();
    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptiveEngine().getAcquisitionStateVariable()
                                                                      .get();

    lQueue.clearQueue();

    lAcquisitionState.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);

    double lCurrentDX = lQueue.getIX(lLightSheetIndex);

    final TDoubleArrayList lIXList = new TDoubleArrayList();

    lQueue.setI(lLightSheetIndex);
    lQueue.setExp(getExposureInSecondsVariable().get());
    lQueue.setIP(lLightSheetIndex, getLaserPowerVariable().get());
    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setIX(lLightSheetIndex, lMinX);
    lQueue.addCurrentStateToQueue();
    lQueue.addCurrentStateToQueue();

    lQueue.setILO(true);
    lQueue.setC(true);
    for (double x = lMinX; x <= lMaxX; x += lDeltaX)
    {
      lIXList.add(x);
      lQueue.setIX(lLightSheetIndex, lCurrentDX + x);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setIX(lLightSheetIndex, lCurrentDX);
    lQueue.addCurrentStateToQueue();

    lQueue.setDefaultFlyBackDZ();
    lQueue.setFinalisationTime(0.3);

    lQueue.finalizeQueue();

    lQueue.addMetaDataEntry(MetaDataChannel.Channel, "NoDisplay");

    return findBestDOFValue(lControlPlaneIndex,
                            lLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lIXList);

  }

  @Override
  public void updateState(InterpolatedAcquisitionState pStateToUpdate)
  {
    updateStateInternal(pStateToUpdate, true, false);
  }

  /**
   * Returns the variable holding the delta X value
   * 
   * @return delta Z variable
   */
  public Variable<Double> getDeltaXVariable()
  {
    return mDeltaXVariable;
  }

}
