package clearcontrol.microscope.lightsheet.calibrator.modules.impl;

import static java.lang.Math.abs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.StatUtils;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationBase;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationModuleInterface;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.stack.OffHeapPlanarStack;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Calibrates the lighthseet power versus its height
 *
 * @author royer
 */
public class CalibrationHP extends CalibrationBase
                           implements CalibrationModuleInterface
{

  private MultiPlot mMultiPlotAdjustPCurves, mMultiPlotHPPCurves;
  private MultiKeyMap<Integer, PolynomialFunction> mHPFunctions;

  /**
   * Instantiates a height-power calibration module
   * 
   * @param pCalibrator
   *          parent calibrator
   */
  public CalibrationHP(Calibrator pCalibrator)
  {
    super(pCalibrator);

    mMultiPlotAdjustPCurves =
                            MultiPlot.getMultiPlot(this.getClass()
                                                       .getSimpleName()
                                                   + " calibration: adjust power curves");
    mMultiPlotAdjustPCurves.setVisible(false);

    mMultiPlotHPPCurves =
                        MultiPlot.getMultiPlot(this.getClass()
                                                   .getSimpleName()
                                               + " calibration: power versus height curves");
    mMultiPlotHPPCurves.setVisible(false);

    mHPFunctions = new MultiKeyMap<>();
  }

  /**
   * Calibrates a given lightsheet's height-power relationship usinga giben
   * detection arm, number of H samples, and number of P samples.
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pDetectionArmIndex
   *          detection arm
   * @param pNumberOfSamplesH
   *          number of H samples
   * @param pNumberOfSamplesP
   *          number of P samples
   */
  public void calibrate(int pLightSheetIndex,
                        int pDetectionArmIndex,
                        int pNumberOfSamplesH,
                        int pNumberOfSamplesP)
  {

    mMultiPlotAdjustPCurves.clear();
    if (!mMultiPlotAdjustPCurves.isVisible())
      mMultiPlotAdjustPCurves.setVisible(true);

    mMultiPlotHPPCurves.clear();
    if (!mMultiPlotHPPCurves.isVisible())
      mMultiPlotHPPCurves.setVisible(true);

    LightSheetInterface lLightSheet =
                                    mLightSheetMicroscope.getDeviceLists()
                                                         .getDevice(LightSheetInterface.class,
                                                                    pLightSheetIndex);

    lLightSheet.getAdaptPowerToWidthHeightVariable().set(false);

    BoundedVariable<Number> lWidthVariable =
                                           lLightSheet.getWidthVariable();
    BoundedVariable<Number> lPowerVariable =
                                           lLightSheet.getPowerVariable();

    double lMinP = lPowerVariable.getMin().doubleValue();
    double lMaxP = lPowerVariable.getMax().doubleValue();
    double lReferencePower = lMaxP;

    double lMinH = lWidthVariable.getMin().doubleValue();
    double lMaxH = lWidthVariable.getMax().doubleValue();
    double lStepH = (lMaxH - lMinH) / pNumberOfSamplesH;
    double lReferenceH = lMaxH;

    final double lReferenceIntensity = adjustP(pLightSheetIndex,
                                               pDetectionArmIndex,
                                               lReferencePower,
                                               lReferencePower,
                                               pNumberOfSamplesP,
                                               lReferenceH,
                                               0,
                                               true);

    final WeightedObservedPoints lObservations =
                                               new WeightedObservedPoints();
    TDoubleArrayList lHList = new TDoubleArrayList();
    TDoubleArrayList lPRList = new TDoubleArrayList();

    for (double h = lMinH; h <= lMaxH; h += lStepH)
    {
      final double lPower = adjustP(pLightSheetIndex,
                                    pDetectionArmIndex,
                                    lMinP,
                                    lMaxP,
                                    pNumberOfSamplesP,
                                    h,
                                    lReferenceIntensity,
                                    false);

      double lPowerRatio = lPower / lReferencePower;

      lHList.add(h);
      lPRList.add(lPowerRatio);
      lObservations.add(h, lPowerRatio);
    }

    final PolynomialCurveFitter lPolynomialCurveFitter =
                                                       PolynomialCurveFitter.create(2);
    final double[] lCoeficients =
                                lPolynomialCurveFitter.fit(lObservations.toList());
    PolynomialFunction lPowerRatioFunction =
                                           new PolynomialFunction(lCoeficients);

    mHPFunctions.put(pLightSheetIndex,
                     pDetectionArmIndex,
                     lPowerRatioFunction);

    PlotTab lPlot =
                  mMultiPlotHPPCurves.getPlot(String.format(" D=%d, I=%d",
                                                            pDetectionArmIndex,
                                                            pLightSheetIndex));

    lPlot.setScatterPlot("samples");
    lPlot.setScatterPlot("fit");

    for (int j = 0; j < lHList.size(); j++)
    {
      lPlot.addPoint("samples", lHList.get(j), lPRList.get(j));
      lPlot.addPoint("fit", lHList.get(j), lPRList.get(j));

    }
    lPlot.ensureUpToDate();

  }

  private Double adjustP(int pLightSheetIndex,
                         int pDetectionArmIndex,
                         double pMinP,
                         double pMaxP,
                         int pNumberOfSamples,
                         double pH,
                         double pTargetIntensity,
                         boolean pReturnIntensity)
  {
    try
    {

      LightSheetMicroscopeQueue lQueue =
                                       mLightSheetMicroscope.requestQueue();
      lQueue.clearQueue();
      lQueue.zero();

      lQueue.setI(pLightSheetIndex);
      lQueue.setIH(pLightSheetIndex, pH);

      final TDoubleArrayList lPList = new TDoubleArrayList();

      lQueue.setIP(pLightSheetIndex, pMinP);
      lQueue.setC(false);
      lQueue.addCurrentStateToQueue();

      lQueue.setC(true);

      double lStep = (pMaxP - pMinP) / pNumberOfSamples;

      for (double p =
                    pMinP, i =
                             0; p <= pMaxP
                                && i < pNumberOfSamples; p +=
                                                           lStep, i++)
      {
        lPList.add(p);
        lQueue.setIP(pLightSheetIndex, p);
        lQueue.addCurrentStateToQueue();
      }

      lQueue.setIP(pLightSheetIndex, pMinP);
      lQueue.setC(false);
      lQueue.addCurrentStateToQueue();

      lQueue.addVoxelDimMetaData(mLightSheetMicroscope, 10);

      lQueue.finalizeQueue();

      mLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      mLightSheetMicroscope.playQueueAndWaitForStacks(lQueue,
                                                                                      lQueue.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (lPlayQueueAndWait)
      {
        final OffHeapPlanarStack lStack =
                                        (OffHeapPlanarStack) mLightSheetMicroscope.getCameraStackVariable(pDetectionArmIndex)
                                                                                  .get();
        // final double[] lDCTSArray =
        // mDCTS2D.computeImageQualityMetric(lImage);
        final double[] lRobustmaxIntensityArray =
                                                ImageAnalysisUtils.computePercentileIntensityPerPlane(lStack,
                                                                                                      99);

        smooth(lRobustmaxIntensityArray, 1);

        PlotTab lPlot =
                      mMultiPlotAdjustPCurves.getPlot(String.format("Mode=%s, D=%d, I=%d, H=%g",
                                                                    pReturnIntensity ? "ret_int"
                                                                                     : "ret_pow",
                                                                    pDetectionArmIndex,
                                                                    pLightSheetIndex,
                                                                    pH));
        lPlot.setScatterPlot("samples");

        // System.out.format("metric array: \n");
        for (int j = 0; j < lRobustmaxIntensityArray.length; j++)
        {
          lPlot.addPoint("samples",
                         lPList.get(j),
                         lRobustmaxIntensityArray[j]);
          System.out.format("%g\t%g\n",
                            lPList.get(j),
                            lRobustmaxIntensityArray[j]);/**/
        }
        lPlot.ensureUpToDate();

        if (pReturnIntensity)
        {
          double lAvgIntensity =
                               StatUtils.percentile(lRobustmaxIntensityArray,
                                                    50);
          return lAvgIntensity;
        }
        else
        {
          int lIndex =
                     find(lRobustmaxIntensityArray, pTargetIntensity);

          double lPower = lPList.get(lIndex);

          return lPower;
        }

      }

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

  private int find(double[] pArray, double pValueToFind)
  {
    int lIndex = -1;
    double lMinDistance = Double.POSITIVE_INFINITY;
    for (int i = 0; i < pArray.length; i++)
    {
      double lValue = pArray[i];
      double lDistance = abs(lValue - pValueToFind);
      if (lDistance < lMinDistance)
      {
        lMinDistance = lDistance;
        lIndex = i;
      }
    }

    return lIndex;
  }

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

  /**
   * Applies the height-power calibration correction for a given lightsheet and
   * detection arm.
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pDetectionArmIndex
   *          detection arm
   * @return residual error
   */
  public double apply(int pLightSheetIndex, int pDetectionArmIndex)
  {
    System.out.println("LightSheet index: " + pLightSheetIndex);

    LightSheetInterface lLightSheetDevice =
                                          mLightSheetMicroscope.getDeviceLists()
                                                               .getDevice(LightSheetInterface.class,
                                                                          pLightSheetIndex);

    PolynomialFunction lNewWidthPowerFunction =
                                              mHPFunctions.get(pLightSheetIndex,
                                                               pDetectionArmIndex);

    Variable<PolynomialFunction> lCurrentHeightFunctionVariable =
                                                                lLightSheetDevice.getHeightPowerFunction();

    System.out.format("Current HeightPower function: %s \n",
                      lCurrentHeightFunctionVariable.get());

    lCurrentHeightFunctionVariable.set(lNewWidthPowerFunction);

    System.out.format("New HeightPower function: %s \n",
                      lCurrentHeightFunctionVariable.get());

    double lError = 0;

    return lError;
  }

  /**
   * Resets the height-power calibration
   */
  @Override
  public void reset()
  {
    mMultiPlotAdjustPCurves.clear();
    mMultiPlotAdjustPCurves.setVisible(false);
    mMultiPlotHPPCurves.clear();
    mMultiPlotHPPCurves.setVisible(false);

    mHPFunctions.clear();
  }

}
