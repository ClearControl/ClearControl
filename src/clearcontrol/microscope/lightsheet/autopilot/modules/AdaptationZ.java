package clearcontrol.microscope.lightsheet.autopilot.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import gnu.trove.list.array.TDoubleArrayList;

public class AdaptationZ extends NDIteratorAdaptationModule
                         implements AdaptationModuleInterface
{

  private double mDeltaZ;

  public AdaptationZ(double pDeltaZ,
                     int pNumberOfSamples,
                     double pProbabilityThreshold)
  {
    super(pNumberOfSamples, pProbabilityThreshold);
    mDeltaZ = pDeltaZ;
  }

  @Override
  public Future<?> atomicStep(int pControlPlaneIndex,
                              int pLightSheetIndex,
                              int pNumberOfSamples)
  {
    LightSheetMicroscopeQueue lQueue =
                                     getAdaptator().getLightSheetMicroscope()
                                                   .requestQueue();
    InterpolatedAcquisitionState lAcquisitionState =
                                                   getAdaptator().getCurrentAcquisitionStateVariable()
                                                                 .get();
    double lCurrentIZ = lQueue.getIZ(pLightSheetIndex);
    int lBestDetectionArm =
                          getAdaptator().getCurrentAcquisitionStateVariable()
                                        .get()
                                        .getBestDetectionArm(pControlPlaneIndex);

    int lHalfSamples = (pNumberOfSamples - 1) / 2;
    double lMinZ = -mDeltaZ * lHalfSamples;
    double lMaxZ = mDeltaZ * lHalfSamples;

    final TDoubleArrayList lDZList = new TDoubleArrayList();

    lQueue.clearQueue();

    lAcquisitionState.applyStateAtControlPlane(lQueue,pControlPlaneIndex);
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
      lQueue.setI(pLightSheetIndex);
      lQueue.addCurrentStateToQueue();
    }

    lQueue.setILO(false);
    lQueue.setC(false);
    lQueue.setDZ(lBestDetectionArm, lCurrentDZ);
    lQueue.addCurrentStateToQueue();

    lQueue.finalizeQueue();

    return findBestDOFValue(pControlPlaneIndex,
                            pLightSheetIndex,
                            lQueue,
                            lAcquisitionState,
                            lDZList);

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

    double lCorrection = -pArgMaxList.get(lBestDetectioArm);

    /*
     * 
    COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
    getAdaptator().getNewAcquisitionState()
                  .addAtControlPlaneIZ(pControlPlaneIndex,
                                       pLightSheetIndex,
                                       lCorrection);/**/
  }

}
