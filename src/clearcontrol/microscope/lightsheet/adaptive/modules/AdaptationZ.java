package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.concurrent.Future;

import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.metadata.MetaDataChannel;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the Z focus
 *
 * @author royer
 */
public class AdaptationZ extends
                         StandardAdaptationModule<InterpolatedAcquisitionState>
                         implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private final Variable<Double> mDeltaZVariable =
                                                 new Variable<Double>("DeltaZ",
                                                                      1.0);

  /**
   * Instantiates a Z focus adaptation module given the deltaz parameter, number
   * of samples and probability threshold
   * 
   * @param pDeltaZ
   *          delta z parameter
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public AdaptationZ(double pDeltaZ,
                     int pNumberOfSamples,
                     double pProbabilityThreshold)
  {
    super("Z", pNumberOfSamples, pProbabilityThreshold);
    getDeltaZVariable().set(pDeltaZ);

  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    info("Atomic step...");

    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    int lNumberOfDetectionArms =
                               getAdaptiveEngine().getMicroscope()
                                                  .getNumberOfDevices(DetectionArm.class);

    double lDeltaZ = getDeltaZVariable().get();

    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptiveEngine().getAcquisitionStateVariable()
                                                                      .get();

    int lNumberOfSamples = getNumberOfSamplesVariable().get();
    int lHalfSamples = (lNumberOfSamples - 1) / 2;
    double lMinZ = -lDeltaZ * lHalfSamples;
    double lMaxZ = lDeltaZ * lHalfSamples;

    final TDoubleArrayList lDZList = new TDoubleArrayList();

    LightSheetMicroscopeQueue lQueue =
                                     (LightSheetMicroscopeQueue) getAdaptiveEngine().getMicroscope()
                                                                                    .requestQueue();

    lQueue.clearQueue();

    // here we set IZ:
    lAcquisitionState.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);
    double lCurrentDZ = lQueue.getDZ(0);

    lQueue.setI(lLightSheetIndex);
    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lCurrentDZ + lMinZ);
    lQueue.addCurrentStateToQueue();
    lQueue.addCurrentStateToQueue();

    lQueue.setILO(true);
    lQueue.setC(true);
    for (double z = lMinZ; z <= lMaxZ; z += lDeltaZ)
    {
      lDZList.add(z);
      lQueue.setDZ(lCurrentDZ + z);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lCurrentDZ);
    lQueue.addCurrentStateToQueue();

    for (int d = 0; d < lNumberOfDetectionArms; d++)
      lQueue.setFlyBackDZ(d, lCurrentDZ);
    lQueue.setFinalisationTime(0.3);

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
  public void updateNewState(InterpolatedAcquisitionState pStateToUpdate)
  {
    info("Update new state...");

    int lNumberOfControlPlanes =
                               getAdaptiveEngine().getAcquisitionStateVariable()
                                                  .get()
                                                  .getNumberOfControlPlanes();
    int lNumberOfLightSheets =
                             getAdaptiveEngine().getAcquisitionStateVariable()
                                                .get()
                                                .getNumberOfLightSheets();

    int lNumberOfDetectionArms =
                               getAdaptiveEngine().getAcquisitionStateVariable()
                                                  .get()
                                                  .getNumberOfDetectionArms();

    for (int cpi = 0; cpi < lNumberOfControlPlanes; cpi++)
    {

      for (int l = 0; l < lNumberOfLightSheets; l++)
      {
        int lSelectedDetectionArm = 0;
        Result lResult = getResult(cpi, l, 0);
        for (int d = 1; d < lNumberOfDetectionArms; d++)
        {
          Result lOneResult = getResult(cpi, l, d);
          if (lOneResult.metricmax
              * lOneResult.probability > lResult.metricmax
                                         * lResult.probability)
          {
            lResult = lOneResult;
            lSelectedDetectionArm = d;
          }
        }

        if (lResult == null)
        {
          severe("Found null result for cpi=%d, l=%d \n", cpi, l);
          continue;
        }

        double lCorrection = -lResult.argmax;

        boolean lProbabilityInsufficient =
                                         lResult.probability < getProbabilityThresholdVariable().get();

        boolean lMetricMaxInsufficient =
                                       lResult.metricmax < getImageMetricThresholdVariable().get();

        if (lMetricMaxInsufficient)
        {
          warning("Metric maximum too low (%g < %g) for cpi=%d, l=%d using neighbooring values\n",
                  lResult.metricmax,
                  getImageMetricThresholdVariable().get(),
                  cpi,
                  l);
        }

        if (lProbabilityInsufficient)
        {
          warning("Probability too low (%g < %g) for cpi=%d, l=%d using neighbooring values\n",
                  lResult.probability,
                  getProbabilityThresholdVariable().get(),
                  cpi,
                  l);
        }

        boolean lMissingInfo = lMetricMaxInsufficient
                               || lProbabilityInsufficient;

        if (lMissingInfo)
        {
          lCorrection =
                      computeCorrectionBasedOnNeighbooringControlPlanes(pStateToUpdate,
                                                                        cpi,
                                                                        l);
        }

        info("Applying correction: %g \n", lCorrection);

        getAdaptiveEngine().notifyLabelGridListenerOfNewEntry(this,
                                                              getName(),
                                                              false,
                                                              "LS",
                                                              "CPI",
                                                              l,
                                                              cpi,
                                                              String.format("argmax=%g\nmetricmax=%g\nprob=%g\ncorr=%g\nmissing=%s\nselected=%d",
                                                                            lResult.argmax,
                                                                            lResult.metricmax,
                                                                            lResult.probability,
                                                                            lCorrection,
                                                                            lMissingInfo,
                                                                            lSelectedDetectionArm));

        pStateToUpdate.getInterpolationTables()
                      .add(LightSheetDOF.IZ, cpi, l, lCorrection);
      }
    }

  }

  protected double computeCorrectionBasedOnNeighbooringControlPlanes(InterpolatedAcquisitionState pStateToUpdate,
                                                                     int cpi,
                                                                     int l)
  {
    double lCorrection;
    if (cpi == 0)
    {
      double lValue = pStateToUpdate.getInterpolationTables()
                                    .get(LightSheetDOF.IZ, cpi, l);

      double lValueAfter = pStateToUpdate.getInterpolationTables()
                                         .get(LightSheetDOF.IZ,
                                              cpi + 1,
                                              l);

      lCorrection = lValueAfter - lValue;
    }
    else if (cpi == pStateToUpdate.getNumberOfControlPlanes() - 1)
    {
      double lValue = pStateToUpdate.getInterpolationTables()
                                    .get(LightSheetDOF.IZ, cpi, l);

      double lValueBefore = pStateToUpdate.getInterpolationTables()
                                          .get(LightSheetDOF.IZ,
                                               cpi - 1,
                                               l);

      lCorrection = lValueBefore - lValue;
    }
    else
    {
      double lValue = pStateToUpdate.getInterpolationTables()
                                    .get(LightSheetDOF.IZ, cpi, l);

      double lValueBefore = pStateToUpdate.getInterpolationTables()
                                          .get(LightSheetDOF.IZ,
                                               cpi - 1,
                                               l);

      double lValueAfter = pStateToUpdate.getInterpolationTables()
                                         .get(LightSheetDOF.IZ,
                                              cpi + 1,
                                              l);

      lCorrection = 0.5 * (lValueAfter + lValueBefore) - lValue;
    }
    return lCorrection;
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

  /*
  @Override
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {
    info("CORRECTIONS HAPPEN HERE...");
   int lBestDetectioArm =
                         getAdaptator().getCurrentAcquisitionStateVariable()
                                       .get()
                                       .getBestDetectionArm(pControlPlaneIndex);
  
    //double lCorrection = -pArgMaxList.get(lBestDetectioArm);
  
  
    COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
    getAdaptator().getNewAcquisitionState()
                  .addAtControlPlaneIZ(pControlPlaneIndex,
                                       pLightSheetIndex,
                                       lCorrection);
  }
  /**/

}
