package clearcontrol.microscope.lightsheet.autopilot.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.FitProbabilityInterface;
import clearcontrol.core.math.argmax.methods.ModeArgMaxFinder;
import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.ip.iqm.DCTS2D;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.autopilot.utils.NDIterator;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.stack.EmptyStack;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * ND iterator adaptation module
 *
 * @author royer
 */
public abstract class NDIteratorAdaptationModule extends
                                                 AdaptationModuleBase
{

  private int mNumberOfSamples;
  private double mProbabilityThreshold;
  private NDIterator mNDIterator;
  protected MultiPlot mMultiPlotZFocusCurves;

  /**
   * Instanciates a ND iterator adaptation module
   * 
   * @param pNumberOfSamples
   *          numbe rof samples
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public NDIteratorAdaptationModule(int pNumberOfSamples,
                                    double pProbabilityThreshold)
  {
    super();
    setNumberOfSamples(pNumberOfSamples);
    setProbabilityThreshold(pProbabilityThreshold);

    mMultiPlotZFocusCurves =
                           MultiPlot.getMultiPlot(this.getClass()
                                                      .getSimpleName()
                                                  + " calibration: focus curves");
    mMultiPlotZFocusCurves.setVisible(true);

  }

  /**
   * Returns ND iterator
   * 
   * @return ND iterator
   */
  public NDIterator getNDIterator()
  {
    return mNDIterator;
  }

  /**
   * Sets the ND iterator
   * 
   * @param pNDIterator
   *          ND iterator
   */
  public void setNDIterator(NDIterator pNDIterator)
  {
    mNDIterator = pNDIterator;
  }

  @Override
  public int getNumberOfSteps()
  {
    return mNDIterator.getNumberOfIterations();
  };

  /**
   * Returns the probability threshold
   * 
   * @return probability threshold
   */
  public double getProbabilityThreshold()
  {
    return mProbabilityThreshold;
  }

  /**
   * Sets the probability threshold
   * 
   * @param pProbabilityThreshold
   *          probability threshold
   */
  public void setProbabilityThreshold(double pProbabilityThreshold)
  {
    mProbabilityThreshold = pProbabilityThreshold;
  }

  /**
   * Returns the number of samples
   * 
   * @return number of samples
   */
  public int getNumberOfSamples()
  {
    return mNumberOfSamples;
  }

  /**
   * Sets the number of samples
   * 
   * @param pNumberOfSamples
   *          number of samples
   */
  public void setNumberOfSamples(int pNumberOfSamples)
  {
    mNumberOfSamples = pNumberOfSamples;
  }

  @Override
  public void reset()
  {
    super.reset();

    LightSheetMicroscope lLightSheetMicroscope =
                                               getAdaptator().getLightSheetMicroscope();
    LightSheetAcquisitionStateInterface lAcquisitionState =
                                                          getAdaptator().getCurrentAcquisitionStateVariable()
                                                                        .get();

    int lNumberOfControlPlanes =
                               lAcquisitionState.getInterpolationTables()
                                                .getNumberOfControlPlanes();

    int lNumberOfLighSheets =
                            lLightSheetMicroscope.getDeviceLists()
                                                 .getNumberOfDevices(LightSheetInterface.class);

    setNDIterator(new NDIterator(lNumberOfControlPlanes,
                                 lNumberOfLighSheets));

  }

  @Override
  public Boolean apply(Void pVoid)
  {
    System.out.format("NDIteratorAdaptationModule step \n");

    boolean lHasNext = getNDIterator().hasNext();

    System.out.format("lHasNext: %s \n", lHasNext);

    if (lHasNext)
    {
      int[] lNext = getNDIterator().next();

      System.out.format("lNext: %s \n", Arrays.toString(lNext));

      int lControlPlaneIndex = lNext[0];
      int lLightSheetIndex = lNext[1];

      System.out.format("controlplane: %d, lighsheetindex: %d \n",
                        lControlPlaneIndex,
                        lLightSheetIndex);

      Future<?> lFuture = atomicStep(lControlPlaneIndex,
                                     lLightSheetIndex,
                                     getNumberOfSamples());

      mListOfFuturTasks.add(lFuture);

    }

    return getNDIterator().hasNext();
  }

  /**
   * Performs an atomic step
   * 
   * @param pControlPlaneIndex
   *          control plane index
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pNumberOfSamples
   *          number of samples
   * @return future
   */
  public abstract Future<?> atomicStep(int pControlPlaneIndex,
                                       int pLightSheetIndex,
                                       int pNumberOfSamples);

  protected Future<?> findBestDOFValue(int pControlPlaneIndex,
                                       int pLightSheetIndex,
                                       LightSheetMicroscopeQueue pQueue,
                                       LightSheetAcquisitionStateInterface lStackAcquisition,
                                       final TDoubleArrayList lDOFValueList)
  {

    try
    {
      LightSheetMicroscope lLightSheetMicroscope =
                                                 getAdaptator().getLightSheetMicroscope();

      lLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      lLightSheetMicroscope.playQueueAndWaitForStacks(pQueue,
                                                                                      10 + pQueue.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (!lPlayQueueAndWait)
        return null;

      final int lNumberOfDetectionArmDevices =
                                             lLightSheetMicroscope.getDeviceLists()
                                                                  .getNumberOfDevices(DetectionArmInterface.class);

      ArrayList<StackInterface> lStacks = new ArrayList<>();
      for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
        if (isRelevantDetectionArm(pControlPlaneIndex, d))
        {
          final StackInterface lStackInterface =
                                               lLightSheetMicroscope.getCameraStackVariable(d)
                                                                    .get();
          lStacks.add(lStackInterface.duplicate());

        }
        else
          lStacks.add(new EmptyStack());

      Runnable lRunnable = () -> {

        try
        {
          ArgMaxFinder1DInterface lSmartArgMaxFinder =
                                                     new ModeArgMaxFinder();

          ArrayList<Double> lArgMaxList = new ArrayList<Double>();

          for (int d = 0; d < lNumberOfDetectionArmDevices; d++)

          {
            if (!isRelevantDetectionArm(pControlPlaneIndex, d))
            {
              lArgMaxList.add(Double.NaN);
              continue;
            }

            final double[] lMetricArray =
                                        computeMetric(pControlPlaneIndex,
                                                      pLightSheetIndex,
                                                      d,
                                                      lDOFValueList,
                                                      lStacks.get(d));

            Double lArgmax =
                           lSmartArgMaxFinder.argmax(lDOFValueList.toArray(),
                                                     lMetricArray);

            System.out.println("lArgmax = " + lArgmax);

            if (lArgmax != null && !Double.isNaN(lArgmax))
            {
              if (lSmartArgMaxFinder instanceof FitProbabilityInterface)
              {
                double lFitProbability =
                                       ((FitProbabilityInterface) lSmartArgMaxFinder).getLastFitProbability();

                if (lFitProbability > getProbabilityThreshold())
                  lArgMaxList.add(lArgmax);
                else
                  lArgMaxList.add(Double.NaN);
              }
              else
              {
                lArgMaxList.add(lArgmax);
              }

            }
            else
              lArgMaxList.add(Double.NaN);

          }

          System.out.println("lArgMaxList=" + lArgMaxList.toString());

          for (StackInterface lStack : lStacks)
            lStack.free();

          updateNewState(pControlPlaneIndex,
                         pLightSheetIndex,
                         lArgMaxList);
        }
        catch (Throwable e)
        {
          e.printStackTrace();
        }

      };

      Future<?> lFuture =
                        getAdaptator().executeAsynchronously(lRunnable);

      // FORCE SYNC:
      if (!getAdaptator().isConcurrentExecution())
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

  /**
   * Returns true if the given detection arm index is relevant at this control
   * plane index
   * 
   * @param pControlPlaneIndex
   *          control plane idnex
   * @param pDetectionArmIndex
   *          detection arm index
   * @return true if relevant
   */
  public boolean isRelevantDetectionArm(int pControlPlaneIndex,
                                        int pDetectionArmIndex)
  {
    int lBestDetectionArm =
                          getAdaptator().getCurrentAcquisitionStateVariable()
                                        .get()
                                        .getBestDetectionArm(pControlPlaneIndex);
    return (lBestDetectionArm == pDetectionArmIndex);
  };

  protected double[] computeMetric(int pControlPlaneIndex,
                                   int pLightSheetIndex,
                                   int pDetectionArmIndex,
                                   final TDoubleArrayList lDOFValueList,
                                   StackInterface lDuplicatedStack)
  {

    DCTS2D lDCTS2D = new DCTS2D();

    @SuppressWarnings("unchecked")
    OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage =
                                                                   (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lDuplicatedStack.getImage();

    if (lDuplicatedStack.isFree() || lImage.isFree())
    {
      System.err.println("Image freed!!");
      return null;
    }

    System.out.format("computing DCTS on %s ...\n", lImage);
    final double[] lMetricArray =
                                lDCTS2D.computeImageQualityMetric(lImage);
    lDuplicatedStack.free();

    if (isRelevantDetectionArm(pControlPlaneIndex,
                               pDetectionArmIndex))
    {
      PlotTab lPlot =
                    mMultiPlotZFocusCurves.getPlot(String.format("LS=%d, D=%d CPI=%d",
                                                                 pLightSheetIndex,
                                                                 pDetectionArmIndex,
                                                                 pControlPlaneIndex));
      lPlot.clearPoints();
      lPlot.setScatterPlot("samples");

      for (int i = 0; i < lMetricArray.length; i++)
      {
        System.out.format("%g\t%g \n",
                          lDOFValueList.get(i),
                          lMetricArray[i]);
        lPlot.addPoint("samples",
                       lDOFValueList.get(i),
                       lMetricArray[i]);
      }
      lPlot.ensureUpToDate();
    }

    return lMetricArray;
  }

  /**
   * Updates new state for a given control plane index, lightsheet index and
   * given the argmax list
   * 
   * @param pControlPlaneIndex
   *          control plane index
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pArgMaxList
   *          argmax list
   */
  public abstract void updateNewState(int pControlPlaneIndex,
                                      int pLightSheetIndex,
                                      ArrayList<Double> pArgMaxList);

  @Override
  public boolean isReady()
  {
    return !getNDIterator().hasNext() && super.isReady();
  }

}
