package clearcontrol.microscope.lightsheet.adaptor.modules;

import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
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

  private double mDeltaZ;

  /**
   * Instanciates a Z focus adaptation module given the deltaz parameter, number
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
    mDeltaZ = pDeltaZ;
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

    int lBestDetectionArm =
                          getAdaptator().getCurrentAcquisitionStateVariable()
                                        .get()
                                        .getBestDetectionArm(lControlPlaneIndex);

    int lNumberOfSamples = getNumberOfSamples();
    int lHalfSamples = (lNumberOfSamples - 1) / 2;
    double lMinZ = -mDeltaZ * lHalfSamples;
    double lMaxZ = mDeltaZ * lHalfSamples;

    final TDoubleArrayList lDZList = new TDoubleArrayList();

    lQueue.clearQueue();

    lAcquisitionState.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);
    double lCurrentDZ = lQueue.getDZ(lBestDetectionArm);

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lBestDetectionArm, lCurrentDZ + lMinZ);
    lQueue.addCurrentStateToQueue();
    lQueue.addCurrentStateToQueue();

    lQueue.setILO(true);
    lQueue.setC(true);
    for (double z = lMinZ; z <= lMaxZ; z += mDeltaZ)
    {
      lDZList.add(z);
      lQueue.setDZ(lBestDetectionArm, lCurrentDZ + z);
      lQueue.setI(lLightSheetIndex);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lBestDetectionArm, lCurrentDZ);
    lQueue.addCurrentStateToQueue();

    lQueue.finalizeQueue();

    return findBestDOFValue(lControlPlaneIndex,
                            lLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lDZList);

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
