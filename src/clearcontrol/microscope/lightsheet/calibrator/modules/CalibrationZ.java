package clearcontrol.microscope.lightsheet.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.Fitting1D;
import clearcontrol.core.math.argmax.methods.ModeArgMaxFinder;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.math.regression.linear.TheilSenEstimator;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.collections4.map.MultiKeyMap;

public class CalibrationZ
{

  private final Calibrator mCalibrator;
  private final LightSheetMicroscope mLightSheetMicroscope;
  private ArgMaxFinder1DInterface mArgMaxFinder;
  private MultiPlot mMultiPlotZFocusCurves, mMultiPlotZModels;
  private MultiKeyMap<Integer, UnivariateAffineFunction> mModels;
  private int mNumberOfDetectionArmDevices;
  private int mIteration;
  private int mNumberOfLightSheetDevices;

  @SuppressWarnings("unchecked")
  public CalibrationZ(Calibrator pCalibrator)
  {
    super();
    mCalibrator = pCalibrator;
    mLightSheetMicroscope = pCalibrator.getLightSheetMicroscope();

    mMultiPlotZFocusCurves =
                           MultiPlot.getMultiPlot(this.getClass()
                                                      .getSimpleName()
                                                  + " calibration: focus curves");
    mMultiPlotZFocusCurves.setVisible(false);

    mMultiPlotZModels =
                      MultiPlot.getMultiPlot(this.getClass()
                                                 .getSimpleName()
                                             + " calibration: models");
    mMultiPlotZModels.setVisible(false);

    mNumberOfDetectionArmDevices =
                                 mLightSheetMicroscope.getDeviceLists()
                                                      .getNumberOfDevices(DetectionArmInterface.class);

    mNumberOfLightSheetDevices =
                               mLightSheetMicroscope.getDeviceLists()
                                                    .getNumberOfDevices(LightSheetInterface.class);

    mModels = new MultiKeyMap<>();
  }

  public boolean calibrate(int pLightSheetIndex,
                           int pNumberOfDSamples,
                           int pNumberOfISamples)
  {
    mArgMaxFinder = new ModeArgMaxFinder();

    mMultiPlotZFocusCurves.clear();
    mMultiPlotZFocusCurves.setVisible(true);

    mIteration++;
    if (pLightSheetIndex == 0)
      mMultiPlotZModels.clear();
    if (!mMultiPlotZModels.isVisible())
      mMultiPlotZModels.setVisible(true);

    final TheilSenEstimator[] lTheilSenEstimators =
                                                  new TheilSenEstimator[mNumberOfDetectionArmDevices];
    final PlotTab[] lPlots =
                           new PlotTab[mNumberOfDetectionArmDevices];
    for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
    {
      lTheilSenEstimators[i] = new TheilSenEstimator();

      lPlots[i] =
                mMultiPlotZModels.getPlot(String.format("iter=%d, D=%d, I=%d",
                                                        mIteration,
                                                        i,
                                                        pLightSheetIndex));

      lPlots[i].setScatterPlot("D" + i);
      lPlots[i].setLinePlot("fit D" + i);
    }

    LightSheetInterface lLightSheetDevice =
                                          mLightSheetMicroscope.getDeviceLists()
                                                               .getDevice(LightSheetInterface.class,
                                                                          pLightSheetIndex);

    BoundedVariable<Number> lZVariable =
                                       lLightSheetDevice.getZVariable();
    double lMinIZ = lZVariable.getMin().doubleValue();
    double lMaxIZ = lZVariable.getMax().doubleValue();

    double lStepIZ = (lMaxIZ - lMinIZ) / (pNumberOfISamples - 1);

    for (double iz = lMinIZ; iz <= lMaxIZ; iz += lStepIZ)
    {

      final double lPerturbedIZ = iz + 0.1 * lStepIZ
                                       * (2 * Math.random() - 1);

      final double[] dz = focusZ(pLightSheetIndex,
                                 pNumberOfDSamples,
                                 iz);

      if (dz == null)
        return false;

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
        if (!Double.isNaN(dz[d]))
        {
          lTheilSenEstimators[d].enter(dz[d], lPerturbedIZ);
          lPlots[d].addPoint("D" + d, dz[d], lPerturbedIZ);
          lPlots[d].ensureUpToDate();
        }

      if (ScriptingEngine.isCancelRequestedStatic()
          || !mCalibrator.isRunning())
        return false;
    }

    for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
    {
      final UnivariateAffineFunction lModel =
                                            lTheilSenEstimators[d].getModel();

      System.out.println("lModel=" + lModel);

      mModels.put(pLightSheetIndex,
                  d,
                  lTheilSenEstimators[d].getModel());

      BoundedVariable<Number> lDetectionFocusZVariable =
                                                       mLightSheetMicroscope.getDeviceLists()
                                                                            .getDevice(DetectionArmInterface.class,
                                                                                       d)
                                                                            .getZVariable();

      double lMinDZ = lDetectionFocusZVariable.getMin().doubleValue();
      double lMaxDZ = lDetectionFocusZVariable.getMax().doubleValue();
      double lStepDZ = (lMaxDZ - lMinDZ) / 1000;

      for (double z = lMinDZ; z <= lMaxDZ; z += lStepDZ)
      {
        lPlots[d].addPoint("fit D" + d,
                           z,
                           mModels.get(pLightSheetIndex, d).value(z));
      }

      lPlots[d].ensureUpToDate();
    }

    return true;
  }

  private double[] focusZ(int pLightSheetIndex,
                          int pNumberOfDSamples,
                          double pIZ)
  {

    try
    {

      double lMinDZ = Double.NEGATIVE_INFINITY;
      double lMaxDZ = Double.POSITIVE_INFINITY;

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
      {
        BoundedVariable<Number> lDetectionFocusZVariable =
                                                         mLightSheetMicroscope.getDeviceLists()
                                                                              .getDevice(DetectionArmInterface.class,
                                                                                         d)
                                                                              .getZVariable();

        lMinDZ = max(lMinDZ,
                     lDetectionFocusZVariable.getMin().doubleValue());
        lMaxDZ = min(lMaxDZ,
                     lDetectionFocusZVariable.getMax().doubleValue());
      }

      System.out.format("Focus: LightSheet=%d, Iz=%g, minDZ=%g, maxDZ=%g \n",
                        pLightSheetIndex,
                        pIZ,
                        lMinDZ,
                        lMaxDZ);

      double lStep = (lMaxDZ - lMinDZ) / (pNumberOfDSamples - 1);

      LightSheetMicroscopeQueue lQueue = mLightSheetMicroscope.requestQueue();
      lQueue.clearQueue();
      lQueue.zero();

      lQueue.setI(pLightSheetIndex);
      lQueue.setIX(pLightSheetIndex, 0);
      lQueue.setIY(pLightSheetIndex, 0);
      lQueue.setIZ(pLightSheetIndex, 0);

      final double[] dz = new double[mNumberOfDetectionArmDevices];

      final TDoubleArrayList lDZList = new TDoubleArrayList();

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
      {
        lQueue.setIZ(pLightSheetIndex, lMinDZ);
        lQueue.setDZ(d, lMinDZ);
        lQueue.setC(d, false);
      }
      lQueue.addCurrentStateToQueue();

      for (double z = lMinDZ; z <= lMaxDZ; z += lStep)
      {
        lDZList.add(z);

        for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
        {
          lQueue.setDZ(d, z);
          lQueue.setC(d, true);
        }

        lQueue.setIH(pLightSheetIndex, 0);
        lQueue.setIZ(pLightSheetIndex, pIZ);

        lQueue.addCurrentStateToQueue();
      }

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
      {
        lQueue.setDZ(d, lMinDZ);
        lQueue.setC(d, false);
      }
      lQueue.addCurrentStateToQueue();

      lQueue.finalizeQueue();

      /*ScoreVisualizerJFrame.visualize("queuedscore",
      																mLightSheetMicroscope.getDeviceLists()
      																											.getSignalGeneratorDevice(0)
      																											.getQueuedScore());/**/

      mLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      mLightSheetMicroscope.playQueueAndWaitForStacks(lQueue,
                                                                                      lQueue.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (lPlayQueueAndWait)
        for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
        {
          final StackInterface lStackInterface =
                                               mLightSheetMicroscope.getCameraStackVariable(i)
                                                                    .get();

          OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage =
                                                                         (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

          if (lImage == null)
            continue;

          // final double[] lDCTSArray =
          // mDCTS2D.computeImageQualityMetric(lImage);
          final double[] lMetricArray =
                                      ImageAnalysisUtils.computeAveragePowerIntensityPerPlane(lImage);

          PlotTab lPlot =
                        mMultiPlotZFocusCurves.getPlot(String.format("D=%d, I=%d, Iz=%g",
                                                                     i,
                                                                     pLightSheetIndex,
                                                                     pIZ));
          lPlot.setScatterPlot("samples");

          // System.out.format("metric array: \n");
          for (int j = 0; j < lMetricArray.length; j++)
          {
            lPlot.addPoint("samples",
                           lDZList.get(j),
                           lMetricArray[j]);
            /*System.out.format(	"%d,%d\t%g\t%g\n",
            					i,
            					j,
            					lDZList.get(j),
            					lMetricArray[j]);/**/
          }
          lPlot.ensureUpToDate();

          final Double lArgMax =
                               mArgMaxFinder.argmax(lDZList.toArray(),
                                                    lMetricArray);

          if (lArgMax != null)
          {
            TDoubleArrayList lDCTSList =
                                       new TDoubleArrayList(lMetricArray);

            double lAmplitudeRatio =
                                   (lDCTSList.max() - lDCTSList.min())
                                     / lDCTSList.max();

            System.out.format("argmax=%s amplratio=%s \n",
                              lArgMax.toString(),
                              lAmplitudeRatio);

            lPlot.setScatterPlot("argmax");
            lPlot.addPoint("argmax", lArgMax, 0);

            if (lAmplitudeRatio > 0.1 && lArgMax > lDZList.get(0))
              dz[i] = lArgMax;
            else
              dz[i] = Double.NaN;

            if (mArgMaxFinder instanceof Fitting1D)
            {
              Fitting1D lFitting1D = (Fitting1D) mArgMaxFinder;

              double[] lFit =
                            lFitting1D.fit(lDZList.toArray(),
                                           new double[lDZList.size()]);

              for (int j = 0; j < lDZList.size(); j++)
              {
                lPlot.setScatterPlot("fit");
                lPlot.addPoint("fit", lDZList.get(j), lFit[j]);
              }
            }

            /*Double lLastFitProbability = mArgMaxFinder.getLastFitProbability();
            
            System.out.format("argmax=%s fitprob=%s \n",
            									lArgMax.toString(),
            									lLastFitProbability);
            
            lPlot.setScatterPlot("argmax");
            lPlot.addPoint("argmax", lArgMax, 0);
            
            if (lLastFitProbability != null && lLastFitProbability > 0.9)
            	dz[i] = lArgMax;
            else
            	dz[i] = Double.NaN;
            	/**/

          }
          else
          {
            dz[i] = Double.NaN;
            System.out.println("Argmax is NULL!");
          }
        }

      return dz;

    }
    catch (final InterruptedException e)
    {
      e.printStackTrace();
    }
    catch (final ExecutionException e)
    {
      e.printStackTrace();
    }
    catch (final TimeoutException e)
    {
      e.printStackTrace();
    }

    return null;

  }

  @SuppressWarnings("unchecked")
  public double apply(int pLightSheetIndex, boolean pAdjustDetectionZ)
  {

    if (mNumberOfDetectionArmDevices == 2)
    {
      System.out.println("LightSheet index: " + pLightSheetIndex);

      final double b0 =
                      mModels.get(pLightSheetIndex, 0).getConstant();
      final double a0 = mModels.get(pLightSheetIndex, 0).getSlope();

      final double b1 =
                      mModels.get(pLightSheetIndex, 1).getConstant();
      final double a1 = mModels.get(pLightSheetIndex, 1).getSlope();

      final LightSheetInterface lLightSheetDevice =
                                                  mLightSheetMicroscope.getDeviceLists()
                                                                       .getDevice(LightSheetInterface.class,
                                                                                  pLightSheetIndex);

      double lSlope = 0.5 * (a0 + a1);
      double lOffset = 0.5 * (b0 + b1);

      System.out.println("before: getZFunction()="
                         + lLightSheetDevice.getZFunction());

      if (abs(lSlope) > 0.00001)
      {
        lLightSheetDevice.getZFunction()
                         .get()
                         .composeWith(new UnivariateAffineFunction(lSlope,
                                                                   lOffset));
        lLightSheetDevice.getZFunction().setCurrent();
      }
      else
        System.out.println("slope too low: " + abs(lSlope));
      System.out.println("after: getZFunction()="
                         + lLightSheetDevice.getZFunction());

      System.out.println("before: getYFunction()="
                         + lLightSheetDevice.getYFunction());
      lLightSheetDevice.getYFunction()
                       .set(UnivariateAffineFunction.axplusb(lLightSheetDevice.getZFunction()
                                                                              .get()
                                                                              .getSlope(),
                                                             0));

      BoundedVariable<Number> lZVariable =
                                         lLightSheetDevice.getZVariable();
      BoundedVariable<Number> lYVariable =
                                         lLightSheetDevice.getYVariable();

      lYVariable.setMinMax(lZVariable.getMin().doubleValue(),
                           lZVariable.getMax().doubleValue());

      System.out.println("after: getYFunction()="
                         + lLightSheetDevice.getYFunction());

      if (pAdjustDetectionZ)
      {

        double lDZIntercept0 = -b0 / a0;
        double lDZIntercept1 = -b1 / a1;

        System.out.println("lDZIntercept0=" + lDZIntercept0);
        System.out.println("lDZIntercept1=" + lDZIntercept1);

        double lDesiredIntercept = 0.5
                                   * (lDZIntercept0 + lDZIntercept1);

        System.out.println("lDesiredIntercept=" + lDesiredIntercept);

        double lInterceptCorrection0 = -(lDesiredIntercept
                                         - lDZIntercept0);
        double lInterceptCorrection1 = -(lDesiredIntercept
                                         - lDZIntercept1);

        System.out.println("lInterceptCorrection0="
                           + lInterceptCorrection0);
        System.out.println("lInterceptCorrection1="
                           + lInterceptCorrection1);

        final DetectionArmInterface lDetectionArmDevice0 =
                                                         mLightSheetMicroscope.getDeviceLists()
                                                                              .getDevice(DetectionArmInterface.class,
                                                                                         0);
        final DetectionArmInterface lDetectionArmDevice1 =
                                                         mLightSheetMicroscope.getDeviceLists()
                                                                              .getDevice(DetectionArmInterface.class,
                                                                                         1);

        System.out.println("Before: lDetectionArmDevice0.getDetectionFocusZFunction()="
                           + lDetectionArmDevice0.getZFunction());
        System.out.println("Before: lDetectionArmDevice1.getDetectionFocusZFunction()="
                           + lDetectionArmDevice1.getZFunction());

        UnivariateAffineFunction lFunction0 =
                                            lDetectionArmDevice0.getZFunction()
                                                                .get();
        UnivariateAffineFunction lFunction1 =
                                            lDetectionArmDevice1.getZFunction()
                                                                .get();

        lFunction0.composeWith(UnivariateAffineFunction.axplusb(1,
                                                                lInterceptCorrection0));
        lFunction1.composeWith(UnivariateAffineFunction.axplusb(1,
                                                                lInterceptCorrection1));

        lDetectionArmDevice0.getZFunction().setCurrent();
        lDetectionArmDevice1.getZFunction().setCurrent();

        System.out.println("After: lDetectionArmDevice0.getDetectionFocusZFunction()="
                           + lDetectionArmDevice0.getZFunction());
        System.out.println("After: lDetectionArmDevice1.getDetectionFocusZFunction()="
                           + lDetectionArmDevice1.getZFunction());
      }

      double lError = (abs(1 - lSlope) + abs(lOffset));

      System.out.println("lError=" + lError);

      return lError;
    }
    return Double.NaN;

  }

  public void reset()
  {
    mMultiPlotZFocusCurves.clear();

    mIteration = 0;
    mMultiPlotZModels.clear();

  }

}
