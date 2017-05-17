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
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.ip.iqm.DCTS2D;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.metadata.MetaDataChannel;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Adaptation module responsible for adjusting the Alpha angle
 *
 * @author royer
 */
public class AdaptationA extends StandardAdaptationModule implements
                         AdaptationModuleInterface<InterpolatedAcquisitionState>
{

  private double mMaxDefocus;

  /**
   * Instantiates an Alpha adaptation module given a max defocus, number of
   * samples and probability threshold.
   * 
   * @param pMaxDefocus
   *          max defocus
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
  public AdaptationA(double pMaxDefocus,
                     int pNumberOfSamples,
                     double pProbabilityThreshold,
                     double pImageMetricThreshold,
                     double pExposureInSeconds,
                     double pLaserPower)
  {
    super("A",
          LightSheetDOF.IA,
          pNumberOfSamples,
          pProbabilityThreshold,
          pImageMetricThreshold,
          pExposureInSeconds,
          pLaserPower);
    mMaxDefocus = pMaxDefocus;
  }

  @Override
  public Future<?> atomicStep(int... pStepCoordinates)
  {
    int lControlPlaneIndex = pStepCoordinates[0];
    int lLightSheetIndex = pStepCoordinates[1];

    LightSheetMicroscope lMicroscope =
                                     (LightSheetMicroscope) getAdaptiveEngine().getMicroscope();
    LightSheetMicroscopeQueue lQueue = lMicroscope.requestQueue();

    InterpolatedAcquisitionState lStackAcquisition =
                                                   getAdaptiveEngine().getAcquisitionStateVariable()
                                                                      .get();

    final TDoubleArrayList lDZList = new TDoubleArrayList();

    lQueue.clearQueue();

    lStackAcquisition.applyStateAtControlPlane(lQueue,
                                               lControlPlaneIndex);

    double lCurrentDZ = lQueue.getDZ(0);// FIXME
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

    lQueue.setTransitionTime(0.5);
    lQueue.setFinalisationTime(0.001);

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

    double lMinZ = -mMaxDefocus;
    double lMaxZ = +mMaxDefocus;
    double lStepZ = (lMaxZ - lMinZ)
                    / (getNumberOfSamplesVariable().get() - 1);

    pQueue.setIY(pLightSheetIndex, pIY);
    pQueue.setIH(pLightSheetIndex, pCurrentH / 3);
    // pLSM.setIP(pLightSheetIndex, 1.0 / 3);

    pQueue.setDZ(pCurrentDZ + lMinZ);
    pQueue.setC(false);
    pQueue.setILO(false);
    pQueue.setI(pLightSheetIndex);
    pQueue.addCurrentStateToQueue();
    pQueue.addCurrentStateToQueue();

    pQueue.setC(true);
    for (double z = lMinZ; z <= lMaxZ; z += lStepZ)
    {
      pDZList.add(z);
      pQueue.setDZ(pCurrentDZ + z);

      pQueue.setILO(true);
      pQueue.setC(true);
      pQueue.setI(pLightSheetIndex);
      pQueue.addCurrentStateToQueue();
    }

    pQueue.setC(false);
    pQueue.setILO(false);
    pQueue.setDZ(pCurrentDZ);
    pQueue.setI(pLightSheetIndex);
    pQueue.addCurrentStateToQueue();

    pQueue.addMetaDataEntry(MetaDataChannel.Channel, "NoDisplay");

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

      final StackInterface lStackInterface =
                                           pMicroscope.getCameraStackVariable(0)// FIXME
                                                      .get();
      StackInterface lDuplicateStack = lStackInterface.duplicate();

      Runnable lRunnable = () -> {

        try
        {

          final double[] lMetricArray =
                                      computeMetricForAlpha(pControlPlaneIndex,
                                                            pLightSheetIndex,
                                                            0, // FIXME
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
                        getAdaptiveEngine().executeAsynchronously(lRunnable);

      if (!getAdaptiveEngine().getConcurrentExecutionVariable().get())
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

    String lChartName = String.format("LS=%d, D=%d CPI=%d",
                                      pLightSheetIndex,
                                      pDetectionArmIndex,
                                      pControlPlaneIndex);

    int lLength = lMetricArray.length / 2;

    getAdaptiveEngine().configureChart(getName(),
                                       lChartName,
                                       "delta z",
                                       "focus metric",
                                       ChartType.Line);

    for (int i = 0; i < lLength; i++)
    {
      /*System.out.format("%g\t%g \n",
                        lDOFValueList.get(i),
                        lMetricArray[i]);/**/

      getAdaptiveEngine().addPoint(getName(),
                                   lChartName,
                                   i == 0,
                                   lDOFValueList.get(i),
                                   lMetricArray[i]);

    }

    for (int i = lLength; i < 2 * lLength; i++)
    {
      /*System.out.format("%g\t%g \n",
                        lDOFValueList.get(i),
                        lMetricArray[i]);/**/

      getAdaptiveEngine().addPoint(getName(),
                                   lChartName,
                                   i == 0,
                                   lDOFValueList.get(i),
                                   lMetricArray[i]);

    }

    return lMetricArray;
  }

  @Override
  public void updateState(InterpolatedAcquisitionState pStateToUpdate)
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
