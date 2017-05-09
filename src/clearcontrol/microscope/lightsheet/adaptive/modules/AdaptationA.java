package clearcontrol.microscope.lightsheet.adaptive.modules;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.methods.ModeArgMaxFinder;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.ip.iqm.DCTS2D;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the Alpha angle
 *
 * @author royer
 */
public class AdaptationA extends
                         StandardAdaptationModule<InterpolatedAcquisitionState>
                         implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private double mMaxDefocus;

  /**
   * Instanciates an Alpha adaptation module given a max defocus, number of
   * samples and probability threshold.
   * 
   * @param pMaxDefocus
   *          max defocus
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public AdaptationA(double pMaxDefocus,
                     int pNumberOfSamples,
                     double pProbabilityThreshold)
  {
    super("A", pNumberOfSamples, pProbabilityThreshold);
    mMaxDefocus = pMaxDefocus;
  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    LightSheetMicroscope lMicroscope = (LightSheetMicroscope) getAdaptator().getMicroscope();
    LightSheetMicroscopeQueue lQueue = lMicroscope.requestQueue();

    InterpolatedAcquisitionState lStackAcquisition =
                                                   getAdaptator().getCurrentAcquisitionStateVariable()
                                                                 .get();

    int lBestDetectionArm =
                          getAdaptator().getCurrentAcquisitionStateVariable()
                                        .get()
                                        .getBestDetectionArm(lControlPlaneIndex);

    final TDoubleArrayList lDZList = new TDoubleArrayList();

    lQueue.clearQueue();

    lStackAcquisition.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);

    double lCurrentDZ = lQueue.getDZ(lBestDetectionArm);
    double lCurrentH = lQueue.getIH(lLightSheetIndex);
    double lIY = 0.6 * lCurrentH;

    addOneSeqToQueue(lControlPlaneIndex,
                     lLightSheetIndex,
                     lQueue,
                     lDZList,
                     lCurrentDZ,
                     lCurrentH,
                     -lIY);

    addOneSeqToQueue(lControlPlaneIndex,
                     lLightSheetIndex,
                     lQueue,
                     lDZList,
                     lCurrentDZ,
                     lCurrentH,
                     lIY);

    lQueue.finalizeQueue();

    return findBestAlphaValue(lControlPlaneIndex,
                              lLightSheetIndex,
                              lMicroscope,
                              lQueue,
                              lStackAcquisition,
                              lIY,
                              lDZList);

  }

  private void addOneSeqToQueue(int pControlPlaneIndex,
                                int pLightSheetIndex,
                                LightSheetMicroscopeQueue pQueue,
                                final TDoubleArrayList pDZList,
                                double pCurrentDZ,
                                double pCurrentH,
                                double pIY)
  {
    int lBestDetectionArm =
                          getAdaptator().getCurrentAcquisitionStateVariable()
                                        .get()
                                        .getBestDetectionArm(pControlPlaneIndex);

    double lMinZ = -mMaxDefocus;
    double lMaxZ = +mMaxDefocus;
    double lStepZ = (lMaxZ - lMinZ) / (getNumberOfSamples() - 1);

    pQueue.setIY(pLightSheetIndex, pIY);
    pQueue.setIH(pLightSheetIndex, pCurrentH / 3);
    // pLSM.setIP(pLightSheetIndex, 1.0 / 3);

    pQueue.setDZ(lBestDetectionArm, pCurrentDZ + lMinZ);
    pQueue.setC(false);
    pQueue.setILO(false);
    pQueue.setI(pLightSheetIndex);
    pQueue.addCurrentStateToQueue();
    pQueue.addCurrentStateToQueue();

    pQueue.setC(true);
    for (double z = lMinZ; z <= lMaxZ; z += lStepZ)
    {
      pDZList.add(z);
      pQueue.setDZ(lBestDetectionArm, pCurrentDZ + z);

      pQueue.setILO(true);
      pQueue.setC(true);
      pQueue.setI(pLightSheetIndex);
      pQueue.addCurrentStateToQueue();
    }

    pQueue.setC(false);
    pQueue.setILO(false);
    pQueue.setDZ(lBestDetectionArm, pCurrentDZ);
    pQueue.setI(pLightSheetIndex);
    pQueue.addCurrentStateToQueue();
  }

  protected Future<?> findBestAlphaValue(int pControlPlaneIndex,
                                         int pLightSheetIndex,
                                         LightSheetMicroscope pMicroscope,
                                         LightSheetMicroscopeQueue pQueue,
                                         InterpolatedAcquisitionState lStackAcquisition,
                                         double pIY,
                                         final TDoubleArrayList lDOFValueList)
  {

    try
    {
      pMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      pMicroscope.playQueueAndWaitForStacks(pQueue,
                                                                            10 + pQueue.getQueueLength(),
                                                                            TimeUnit.SECONDS);

      if (!lPlayQueueAndWait)
        return null;

      final int lBestDetectioArm =
                                 getAdaptator().getCurrentAcquisitionStateVariable()
                                               .get()
                                               .getBestDetectionArm(pControlPlaneIndex);

      final StackInterface lStackInterface =
                                           pMicroscope.getCameraStackVariable(lBestDetectioArm)
                                                      .get();
      StackInterface lDuplicateStack = lStackInterface.duplicate();

      Runnable lRunnable = () -> {

        try
        {

          final double[] lMetricArray =
                                      computeMetricForAlpha(pControlPlaneIndex,
                                                            pLightSheetIndex,
                                                            lBestDetectioArm,
                                                            lDOFValueList,
                                                            lDuplicateStack);

          lDuplicateStack.free();

          int lLength = lMetricArray.length / 2;

          double[] lAngleAlphaArray =
                                    Arrays.copyOfRange(lDOFValueList.toArray(),
                                                       0,
                                                       lLength - 1);

          double[] lArrayN = Arrays.copyOfRange(lMetricArray,
                                                0,
                                                lLength - 1);
          double[] lArrayP = Arrays.copyOfRange(lMetricArray,
                                                lLength,
                                                2 * lLength - 1);

          smooth(lArrayN, 6);
          smooth(lArrayP, 6);

          ArgMaxFinder1DInterface lSmartArgMaxFinder =
                                                     new ModeArgMaxFinder();

          Double lArgmaxN =
                          lSmartArgMaxFinder.argmax(lAngleAlphaArray,
                                                    lArrayN);

          Double lArgmaxP =
                          lSmartArgMaxFinder.argmax(lAngleAlphaArray,
                                                    lArrayP);

          System.out.println("lArgmaxN = " + lArgmaxN);
          System.out.println("lArgmaxP = " + lArgmaxP);

          if (lArgmaxN != null && lArgmaxP != null
              && !Double.isNaN(lArgmaxN)
              && !Double.isNaN(lArgmaxP))
          {
            double lObservedAngleInRadians =
                                           atan((lArgmaxP - lArgmaxN)
                                                / (2 * pIY));
            double lObservedAngleInDegrees =
                                           toDegrees(lObservedAngleInRadians);

            System.out.println("lArgmaxP - lArgmaxN="
                               + (lArgmaxP - lArgmaxN));
            System.out.println("2 * pIY * mMicronsPerPixel="
                               + 2 * pIY);
            System.out.println("lObservedAngleInDegrees="
                               + lObservedAngleInDegrees);

            // TODO: put back eventually:
            /*updateNewState(pControlPlaneIndex,
                           pLightSheetIndex,
                           lObservedAngleInDegrees);/**/
          }
        }
        catch (Throwable e)
        {
          e.printStackTrace();
        }
      };

      Future<?> lFuture =
                        getAdaptator().executeAsynchronously(lRunnable);

      if (!getAdaptator().getConcurrentExecutionVariable().get())
      {
        try
        {
          lFuture.get();
        }
        catch (Throwable e)
        {
          e.printStackTrace();
        }
      }

      return lFuture;
    }
    catch (InterruptedException | ExecutionException
        | TimeoutException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  protected double[] computeMetricForAlpha(int pControlPlaneIndex,
                                           int pLightSheetIndex,
                                           int pDetectionArmIndex,
                                           final TDoubleArrayList lDOFValueList,
                                           StackInterface lDuplicatedStack)
  {
    DCTS2D lDCTS2D = new DCTS2D();

    System.out.format("computing DCTS on %s ...\n", lDuplicatedStack);
    final double[] lMetricArray =
                                lDCTS2D.computeImageQualityMetric((OffHeapPlanarStack) lDuplicatedStack);
    lDuplicatedStack.free();

    PlotTab lPlot =
                  mMultiPlotZFocusCurves.getPlot(String.format("LS=%d, D=%d CPI=%d",
                                                               pLightSheetIndex,
                                                               pDetectionArmIndex,
                                                               pControlPlaneIndex));
    lPlot.clearPoints();
    lPlot.setScatterPlot("samples");

    int lLength = lMetricArray.length / 2;

    for (int i = 0; i < lLength; i++)
    {
      System.out.format("%g\t%g \n",
                        lDOFValueList.get(i),
                        lMetricArray[i]);
      lPlot.addPoint("samples N",
                     lDOFValueList.get(i),
                     lMetricArray[i]);
    }

    for (int i = lLength; i < 2 * lLength; i++)
    {
      System.out.format("%g\t%g \n",
                        lDOFValueList.get(i),
                        lMetricArray[i]);
      lPlot.addPoint("samples P",
                     lDOFValueList.get(i),
                     lMetricArray[i]);
    }

    lPlot.ensureUpToDate();
    return lMetricArray;
  }

  @Override
  public void updateNewState()
  {
    // TODO Auto-generated method stub

  }

  /*
  public void updateNewState(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             ArrayList<Double> pArgMaxList)
  {
  
    info("CORRECTIONS HAPPEN HERE");
    // double lCorrection = -pObservedAngle;
         COMMENTED SO IT COMPILES PUT IT BACK EVENTUALLY!
    getAdaptator().getNewAcquisitionState()
                  .addAtControlPlaneIA(pControlPlaneIndex,
                                       pLightSheetIndex,
                                       lCorrection); 
  }/**/

  private void smooth(double[] pMetricArray, int pIterations)
  {

    for (int j = 0; j < pIterations; j++)
    {
      for (int i = 1; i < pMetricArray.length - 1; i++)
      {
        pMetricArray[i] = (pMetricArray[i - 1] + pMetricArray[i]
                           + pMetricArray[i + 1])
                          / 3;
      }

      for (int i = pMetricArray.length - 2; i >= 1; i--)
      {
        pMetricArray[i] = (pMetricArray[i - 1] + pMetricArray[i]
                           + pMetricArray[i + 1])
                          / 3;
      }
    }

  }

}
