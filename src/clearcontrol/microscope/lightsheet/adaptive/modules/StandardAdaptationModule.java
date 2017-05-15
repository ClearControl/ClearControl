package clearcontrol.microscope.lightsheet.adaptive.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Triple;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.math.argmax.SmartArgMaxFinder;
import clearcontrol.core.variable.Variable;
import clearcontrol.ip.iqm.DCTS2D;
import clearcontrol.microscope.adaptive.modules.NDIteratorAdaptationModule;
import clearcontrol.microscope.adaptive.utils.NDIterator;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;
import clearcontrol.stack.EmptyStack;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * ND iterator adaptation module
 *
 * @author royer
 * @param <S>
 *          state type
 */
public abstract class StandardAdaptationModule<S extends LightSheetAcquisitionStateInterface<S>>
                                              extends
                                              NDIteratorAdaptationModule<S>
                                              implements
                                              AsynchronousExecutorServiceAccess

{

  private final Variable<Integer> mNumberOfSamplesVariable =
                                                           new Variable<Integer>("NumberOfSamples",
                                                                                 17);
  private final Variable<Double> mProbabilityThresholdVariable =
                                                               new Variable<Double>("ProbabilityThreshold",
                                                                                    0.95);

  private final Variable<Double> mImageMetricThresholdVariable =
                                                               new Variable<Double>("MetricThreshold",
                                                                                    0.1e-4);

  private HashMap<Triple<Integer, Integer, Integer>, Result> mResultsMap =
                                                                         new HashMap<>();

  /**
   * Result
   *
   * @author royer
   */
  public static class Result
  {

    @SuppressWarnings("javadoc")
    public double argmax, metricmax, probability;

    @SuppressWarnings("javadoc")
    public static Result of(double pArgMax,
                            double pMetricMax,
                            double pProbability)
    {
      Result lResult = new Result();
      lResult.argmax = pArgMax;
      lResult.metricmax = pMetricMax;
      lResult.probability = pProbability;
      return lResult;
    }

    @SuppressWarnings("javadoc")
    public static Result none()
    {
      return of(Double.NaN, 0.0, 0.0);
    }

  }

  /**
   * Instanciates a ND iterator adaptation module
   * 
   * @param pModuleName
   *          module name
   * @param pNumberOfSamples
   *          number of samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public StandardAdaptationModule(String pModuleName,
                                  int pNumberOfSamples,
                                  double pProbabilityThreshold)
  {
    super(pModuleName);
    getNumberOfSamplesVariable().set(pNumberOfSamples);
    getProbabilityThresholdVariable().set(pProbabilityThreshold);
  }

  @Override
  public void reset()
  {
    super.reset();

    LightSheetMicroscope lLightsheetMicroscope =
                                               (LightSheetMicroscope) getAdaptiveEngine().getMicroscope();

    LightSheetAcquisitionStateInterface<S> lAcquisitionState =
                                                             getAdaptiveEngine().getAcquisitionStateVariable()
                                                                                .get();

    if (lAcquisitionState == null)
    {
      severe("There is no current acquisition state defined!");
      return;
    }

    int lNumberOfControlPlanes =
                               lAcquisitionState.getInterpolationTables()
                                                .getNumberOfControlPlanes();

    int lNumberOfLighSheets =
                            lLightsheetMicroscope.getDeviceLists()
                                                 .getNumberOfDevices(LightSheetInterface.class);

    setNDIterator(new NDIterator(lNumberOfControlPlanes,
                                 lNumberOfLighSheets));

  }

  protected Future<?> findBestDOFValue(int pControlPlaneIndex,
                                       int pLightSheetIndex,
                                       LightSheetMicroscopeQueue pQueue,
                                       S lStackAcquisition,
                                       final TDoubleArrayList lDOFValueList)
  {

    try
    {
      LightSheetMicroscope lLightsheetMicroscope =
                                                 (LightSheetMicroscope) getAdaptiveEngine().getMicroscope();

      lLightsheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      lLightsheetMicroscope.playQueueAndWaitForStacks(pQueue,
                                                                                      10 + pQueue.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (!lPlayQueueAndWait)
        return null;

      final int lNumberOfDetectionArmDevices =
                                             lLightsheetMicroscope.getDeviceLists()
                                                                  .getNumberOfDevices(DetectionArmInterface.class);

      ArrayList<StackInterface> lStacks = new ArrayList<>();
      for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
      {
        final StackInterface lStackInterface =
                                             lLightsheetMicroscope.getCameraStackVariable(d)
                                                                  .get();
        lStacks.add(lStackInterface.duplicate());

      }

      Runnable lRunnable = () -> {

        try
        {
          SmartArgMaxFinder lSmartArgMaxFinder =
                                               new SmartArgMaxFinder();

          String lInfoString = "";

          for (int pDetectionArmIndex =
                                      0; pDetectionArmIndex < lNumberOfDetectionArmDevices; pDetectionArmIndex++)

          {

            final double[] lMetricArray =
                                        computeMetric(pControlPlaneIndex,
                                                      pLightSheetIndex,
                                                      pDetectionArmIndex,
                                                      lDOFValueList,
                                                      lStacks.get(pDetectionArmIndex));

            if (lMetricArray == null)
              continue;

            Double lArgmax =
                           lSmartArgMaxFinder.argmax(lDOFValueList.toArray(),
                                                     lMetricArray);

            Double lFitProbability =
                                   lSmartArgMaxFinder.getLastFitProbability();

            if (lArgmax == null || lFitProbability == null)
            {
              lArgmax = 0d;
              lFitProbability = 0d;
            }

            double lMetricMax = Arrays.stream(lMetricArray)
                                      .max()
                                      .getAsDouble();

            info("argmax = %s, metric=%s, probability = %s ",
                 lArgmax,
                 lMetricMax,
                 lFitProbability);

            setResult(pControlPlaneIndex,
                      pLightSheetIndex,
                      pDetectionArmIndex,
                      Result.of(lArgmax,
                                lMetricMax,
                                lFitProbability));

            lInfoString +=
                        String.format("argmax=%g\nmetricmax=%g\nprob=%g\n",
                                      lArgmax,
                                      lMetricMax,
                                      lFitProbability);
          }

          getAdaptiveEngine().notifyLabelGridListenerOfNewEntry(this,
                                                                getName(),
                                                                false,
                                                                "LS",
                                                                "CPI",
                                                                pLightSheetIndex,
                                                                pControlPlaneIndex,
                                                                lInfoString);

          for (StackInterface lStack : lStacks)
            lStack.free();

        }
        catch (Throwable e)
        {
          e.printStackTrace();
        }

      };

      Future<?> lFuture = executeAsynchronously(lRunnable);

      // FORCE SYNC:
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

  protected void setResult(int pControlPlaneIndex,
                           int pLightSheetIndex,
                           int pDetectionArmIndex,
                           Result pResult)
  {
    mResultsMap.put(Triple.of(pControlPlaneIndex,
                              pLightSheetIndex,
                              pDetectionArmIndex),
                    pResult);
  }

  protected Result getResult(int pControlPlaneIndex,
                             int pLightSheetIndex,
                             int d)
  {
    return mResultsMap.get(Triple.of(pControlPlaneIndex,
                                     pLightSheetIndex,
                                     d));
  }

  protected double[] computeMetric(int pControlPlaneIndex,
                                   int pLightSheetIndex,
                                   int pDetectionArmIndex,
                                   final TDoubleArrayList lDOFValueList,
                                   StackInterface lDuplicatedStack)
  {

    if (lDuplicatedStack instanceof EmptyStack)
      return null;

    DCTS2D lDCTS2D = new DCTS2D();

    // System.out.format("computing DCTS on %s ...\n", lDuplicatedStack);
    final double[] lMetricArray =
                                lDCTS2D.computeImageQualityMetric((OffHeapPlanarStack) lDuplicatedStack);
    lDuplicatedStack.free();

    String lChartName = String.format("CPI=%d|LS=%d|D=%d",
                                      pControlPlaneIndex,
                                      pLightSheetIndex,
                                      pDetectionArmIndex);

    for (int i = 0; i < lMetricArray.length; i++)
    {
      /*System.out.format("%g\t%g \n",
                        lDOFValueList.get(i),
                        lMetricArray[i]);/**/

      getAdaptiveEngine().notifyChartListenersOfNewPoint(this,
                                                         lChartName,
                                                         i == 0,
                                                         "ΔZ",
                                                         "focus metric",
                                                         lDOFValueList.get(i),
                                                         lMetricArray[i]);

    }

    return lMetricArray;
  }

  @Override
  public boolean isReady()
  {
    return getNDIterator() != null && !getNDIterator().hasNext()
           && super.isReady();
  }

  /**
   * Returns the variable holding the number of samples
   * 
   * @return number of samples variable
   */
  public Variable<Integer> getNumberOfSamplesVariable()
  {
    return mNumberOfSamplesVariable;
  }

  /**
   * Returns the variable holding the probability threshold
   * 
   * @return probability threshold variable
   */
  public Variable<Double> getProbabilityThresholdVariable()
  {
    return mProbabilityThresholdVariable;
  }

  /**
   * Returns the variable holding the image metric threshold
   * 
   * @return image metric threshold variable
   */
  public Variable<Double> getImageMetricThresholdVariable()
  {
    return mImageMetricThresholdVariable;
  }

}
