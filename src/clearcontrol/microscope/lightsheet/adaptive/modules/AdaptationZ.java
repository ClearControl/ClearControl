package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.concurrent.Future;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.metadata.MetaDataChannel;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the Z focus
 *
 * @author royer
 */
public class AdaptationZ extends StandardAdaptationModule implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private final Variable<Double> mDeltaZVariable =
                                                 new Variable<>("DeltaZ",
                                                                      1.0);

  /**
   * Instantiates a Z focus adaptation module given the delta Z parameter,
   * number of samples, probability threshold and image metric threshold
   * 
   * @param pNumberOfSamples
   *          number of samples
   * @param pDeltaZ
   *          delta z parameter
   * @param pProbabilityThreshold
   *          probability threshold
   * @param pImageMetricThreshold
   *          image metric threshold
   * @param pExposureInSeconds
   *          expsoure in seconds
   * @param pLaserPower
   *          laser power
   */
  public AdaptationZ(int pNumberOfSamples,
                     double pDeltaZ,
                     double pProbabilityThreshold,
                     double pImageMetricThreshold,
                     double pExposureInSeconds,
                     double pLaserPower)
  {
    super("Z",
          LightSheetDOF.IZ,
          pNumberOfSamples,
          pProbabilityThreshold,
          pImageMetricThreshold,
          pExposureInSeconds,
          pLaserPower);
    getDeltaZVariable().set(pDeltaZ);

  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    info("Atomic step...");

    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    double lDeltaZ = getDeltaZVariable().get();
    int lNumberOfSamples = getNumberOfSamplesVariable().get();
    int lHalfSamples = (lNumberOfSamples - 1) / 2;
    double lMinZ = -lDeltaZ * lHalfSamples;

    final TDoubleArrayList lDZList = new TDoubleArrayList();

    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptiveEngine().getAcquisitionStateVariable()
                                                                      .get();

    LightSheetMicroscopeQueue lQueue =
                                     (LightSheetMicroscopeQueue) getAdaptiveEngine().getMicroscope()
                                                                                    .requestQueue();

    lQueue.clearQueue();

    // here we set IZ:
    lAcquisitionState.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);
    double lCurrentDZ = lQueue.getDZ(0);

    lQueue.setI(lLightSheetIndex);
    lQueue.setExp(getExposureInSecondsVariable().get());
    lQueue.setIP(lLightSheetIndex, getLaserPowerVariable().get());
    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lCurrentDZ + lMinZ);
    lQueue.addCurrentStateToQueue();
    lQueue.addCurrentStateToQueue();

    lQueue.setILO(true);
    lQueue.setC(true);
    for (int i = 0; i < lNumberOfSamples; i++)
    {
      double z = lMinZ + lDeltaZ * i;
      lDZList.add(z);
      lQueue.setDZ(lCurrentDZ + z);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lCurrentDZ);
    lQueue.addCurrentStateToQueue();

    lQueue.setTransitionTime(0.5);
    lQueue.setFinalisationTime(0.001);

    lQueue.finalizeQueue();

    lQueue.addMetaDataEntry(MetaDataChannel.Channel, "NoDisplay");

    return findBestDOFValue(lControlPlaneIndex,
                            lLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lDZList);

    /**/

  }

  @Override
  public void updateState(InterpolatedAcquisitionState pStateToUpdate)
  {
    updateStateInternal(pStateToUpdate, true, true);
  }

  /**
   * Returns the variable holding the delta Z value
   * 
   * @return delta Z variable
   */
  public Variable<Double> getDeltaZVariable()
  {
    return mDeltaZVariable;
  }

}
