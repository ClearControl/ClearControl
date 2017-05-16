package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.microscope.adaptive.modules.AdaptationModuleBase;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.metadata.MetaDataChannel;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the lightsheet width.
 *
 * @author royer
 */
public class AdaptationP extends
                         AdaptationModuleBase<InterpolatedAcquisitionState>
                         implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private double mTargetLaserPower;

  /**
   * Instanciates a laser power adaptation module given the target laser power.
   * 
   * @param pTargetLaserPower
   *          laser power
   */
  public AdaptationP(double pTargetLaserPower)
  {
    super("W");
    mTargetLaserPower = pTargetLaserPower;
  }

  @Override
  public int getNumberOfSteps()
  {
    return 1;
  }

  @Override
  public Boolean apply(Void pVoid)
  {
    LightSheetMicroscope lLightsheetMicroscope =
                                               (LightSheetMicroscope) getAdaptiveEngine().getMicroscope();

    LightSheetMicroscopeQueue lQueue =
                                     lLightsheetMicroscope.requestQueue();
    InterpolatedAcquisitionState lStackAcquisition =
                                                   getAdaptiveEngine().getAcquisitionStateVariable()
                                                                      .get();

    int lNumberOfControlPlanes =
                               getAdaptiveEngine().getAcquisitionStateVariable()
                                                  .get()
                                                  .getNumberOfControlPlanes();

    int lNumberOfLightSheets =
                             lLightsheetMicroscope.getDeviceLists()
                                                  .getNumberOfDevices(LightSheetInterface.class);

    int lNumberOfDetectionArmDevices =
                                     lLightsheetMicroscope.getDeviceLists()
                                                          .getNumberOfDevices(DetectionArmInterface.class);

    lQueue.clearQueue();

    for (int l = 0; l < lNumberOfLightSheets; l++)
    {

      lStackAcquisition.applyStateAtControlPlane(lQueue, 0);
      lQueue.setILO(false);
      lQueue.setC(false);
      lQueue.setI(false);
      lQueue.setI(l);
      lQueue.addCurrentStateToQueue();

      for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
      {
        lStackAcquisition.applyStateAtControlPlane(lQueue, czi);
        lQueue.setILO(false);
        lQueue.setC(false);
        lQueue.setI(l);
        lQueue.addCurrentStateToQueue();

        // lLSM.setIP(l, mTargetLaserPower);
        lQueue.setILO(true);
        lQueue.setC(true);
        lQueue.setI(l);
        lQueue.addCurrentStateToQueue();
      }
    }
    lQueue.finalizeQueue();

    lQueue.addMetaDataEntry(MetaDataChannel.Channel, "NoDisplay");

    try
    {
      lLightsheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      lLightsheetMicroscope.playQueueAndWaitForStacks(lQueue,
                                                                                      10 + lQueue.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (!lPlayQueueAndWait)
        return null;

      ArrayList<double[]> lAvgIntensities = new ArrayList<>();
      for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
      {
        final OffHeapPlanarStack lStack =
                                        (OffHeapPlanarStack) lLightsheetMicroscope.getCameraStackVariable(d)
                                                                                  .get();
        double[] lImageSumIntensity =
                                    ImageAnalysisUtils.computeImageAverageIntensityPerPlane(lStack);
        lAvgIntensities.add(lImageSumIntensity);
      }

      TDoubleArrayList lImageIntensityPerLightSheetList =
                                                        new TDoubleArrayList();

      int i = 0;
      for (int l = 0; l < lNumberOfLightSheets; l++)
      {
        double lImageIntensityPerLightSheet = 0;
        for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
        {

          for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
          {
            double[] lAvgIntensityArray = lAvgIntensities.get(d);

            double lImageSumIntensity = lAvgIntensityArray[i++];

            System.out.format("Average Image Intensity [%d,%d,%d]= %g \n",
                              czi,
                              l,
                              d,
                              lImageSumIntensity);

            lImageIntensityPerLightSheet += lImageSumIntensity;
          }

        }

        lImageIntensityPerLightSheet /= lNumberOfControlPlanes;

        System.out.format("lAverageIntensityPerLightSheet[%d]= %g \n",
                          l,
                          lImageIntensityPerLightSheet);

        lImageIntensityPerLightSheetList.add(lImageIntensityPerLightSheet);

      }

      System.out.println("lAverageIntensityPerLightSheetList="
                         + lImageIntensityPerLightSheetList);

      double lAverageIntensity =
                               lImageIntensityPerLightSheetList.sum()
                                 / lImageIntensityPerLightSheetList.size();

      System.out.format("lAverageIntensity= %g \n",
                        lAverageIntensity);

      TDoubleArrayList lPowerPerLightSheet = new TDoubleArrayList();

      for (int l = 0; l < lNumberOfLightSheets; l++)
      {

        double lOldPower =
                         0; /*     COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY! getAdaptator().getNewAcquisitionState()
                                       .getAtControlPlaneIP(0, l);/**/

        double lRatio = (lAverageIntensity
                         / lImageIntensityPerLightSheetList.get(l));

        double lNewPower = lOldPower * lRatio;

        System.out.format("lOldPower[%d]= %g \n", l, lOldPower);
        System.out.format("lRatio[%d]= %g \n", l, lRatio);
        System.out.format("lNewPower[%d]= %g \n", l, lNewPower);

        lPowerPerLightSheet.add(lNewPower);
      }

      double lMaxUnNormalizedPower = lPowerPerLightSheet.max();

      System.out.format("lMaxUnNormalizedPowero= %g \n",
                        lMaxUnNormalizedPower);

      for (int l = 0; l < lNumberOfLightSheets; l++)
      {
        double lRenormalizedPower = (mTargetLaserPower
                                     / lMaxUnNormalizedPower)
                                    * lPowerPerLightSheet.get(l);

        System.out.format("Max Damped Ratio[%d]= %g \n",
                          l,
                          lRenormalizedPower);

        lPowerPerLightSheet.set(l, lRenormalizedPower);
      }

      for (int l = 0; l < lNumberOfLightSheets; l++)
        for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
        {
          System.out.format("Setting new power for lightsheet %d and control mplane %d:  %g \n",
                            l,
                            czi,
                            lPowerPerLightSheet.get(l));

          /*     COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
          getAdaptator().getNewAcquisitionState()
                        .setAtControlPlaneIP(czi,
                                             l,
                                             lPowerPerLightSheet.get(l));/**/
        }

    }
    catch (InterruptedException | ExecutionException
        | TimeoutException e)
    {
      e.printStackTrace();
    }

    return false;
  }

  @Override
  public boolean areAllTasksCompleted()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean areAllStepsCompleted()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int getRemainingNumberOfSteps()
  {
    // TODO: change this
    return 1;
  }

  @Override
  public void updateState(InterpolatedAcquisitionState pStateToUpdate)
  {
    // TODO Auto-generated method stub

  }

}
