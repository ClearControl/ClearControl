package clearcontrol.microscope.lightsheet.calibrator.modules.impl;

import static java.lang.Math.abs;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
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
 * Width-Power lightsheet calibration
 *
 * @author royer
 */
public class CalibrationWP extends CalibrationBase
                           implements CalibrationModuleInterface
{
  private MultiPlot mMultiPlotAdjustPCurves, mMultiPlotHPPCurves;
  private MultiKeyMap<Integer, PolynomialFunction> mWPFunctions;

  /**
   * Instantiates a Width-Power lightsheet calibration module
   * 
   * @param pCalibrator
   *          parent calibrator
   */
  public CalibrationWP(Calibrator pCalibrator)
  {
    super(pCalibrator);
    mMultiPlotAdjustPCurves =
                            MultiPlot.getMultiPlot(this.getClass()
                                                       .getSimpleName()
                                                   + " calibration: adjust power curves");
    mMultiPlotAdjustPCurves.setVisible(false);

    mWPFunctions = new MultiKeyMap<>();
  }

  /**
   * Calibrates the lightsheet width-power relationship for a given lightsheet,
   * detection arm, number of width and power samples.
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pDetectionArmIndex
   *          detection arm index
   * @param pNumberOfSamplesW
   *          number of W samples
   * @param pNumberOfSamplesP
   *          number of P samples
   */
  public void calibrate(int pLightSheetIndex,
                        int pDetectionArmIndex,
                        int pNumberOfSamplesW,
                        int pNumberOfSamplesP)
  {

    mMultiPlotAdjustPCurves.clear();
    if (!mMultiPlotAdjustPCurves.isVisible())
      mMultiPlotAdjustPCurves.setVisible(true);

    LightSheetInterface lLightSheet =
                                    mLightSheetMicroscope.getDeviceLists()
                                                         .getDevice(LightSheetInterface.class,
                                                                    pLightSheetIndex);

    BoundedVariable<Number> lWidthVariable =
                                           lLightSheet.getWidthVariable();
    BoundedVariable<Number> lPowerVariable =
                                           lLightSheet.getWidthVariable();

    double lMinP = lPowerVariable.getMin().doubleValue();
    double lMaxP = lPowerVariable.getMax().doubleValue();
    double lReferencePower = (lMaxP - lMinP) / 2;

    double lMinW = lWidthVariable.getMin().doubleValue();
    double lMaxW = lWidthVariable.getMax().doubleValue();
    double lStepW = (lMaxW - lMinW) / pNumberOfSamplesW;
    double lReferenceW = (lMaxW - lMinW) / 2;

    final double lReferenceIntensity = adjustP(pLightSheetIndex,
                                               pDetectionArmIndex,
                                               lReferencePower,
                                               lReferencePower,
                                               pNumberOfSamplesP,
                                               lReferenceW,
                                               0,
                                               true);

    final WeightedObservedPoints lObservations =
                                               new WeightedObservedPoints();
    TDoubleArrayList lWList = new TDoubleArrayList();
    TDoubleArrayList lPRList = new TDoubleArrayList();

    for (double w = lMinW; w <= lMaxW; w += lStepW)
    {
      final double lPower =
                          adjustP(pLightSheetIndex,
                                  pDetectionArmIndex,
                                  lMinP,
                                  lMaxP,
                                  10,
                                  w,
                                  lReferenceIntensity,
                                  false);

      double lPowerRatio = lPower / lReferencePower;

      lWList.add(w);
      lPRList.add(lPowerRatio);
      lObservations.add(w, lPowerRatio);
    }

    final PolynomialCurveFitter lPolynomialCurveFitter =
                                                       PolynomialCurveFitter.create(3);

    final double[] lCoeficients =
                                lPolynomialCurveFitter.fit(lObservations.toList());

    PolynomialFunction lPowerRatioFunction =
                                           new PolynomialFunction(lCoeficients);

    mWPFunctions.put(pLightSheetIndex,
                     pDetectionArmIndex,
                     lPowerRatioFunction);

    PlotTab lPlot =
                  mMultiPlotHPPCurves.getPlot(String.format(" D=%d, I=%d, W=%g",
                                                            pDetectionArmIndex,
                                                            pLightSheetIndex));

    lPlot.setScatterPlot("samples");
    lPlot.setScatterPlot("fit");
    List<WeightedObservedPoint> lObservationsList =
                                                  lObservations.toList();

    for (int j = 0; j < lWList.size(); j++)
    {
      lPlot.addPoint("samples", lWList.get(j), lPRList.get(j));
      lPlot.addPoint("fit",
                     lWList.get(j),
                     lObservationsList.get(j).getY());

    }
    lPlot.ensureUpToDate();

  }

  private Double adjustP(int pLightSheetIndex,
                         int pDetectionArmIndex,
                         double pMinP,
                         double pMaxP,
                         int pNumberOfSamples,
                         double pW,
                         double pTargetIntensity,
                         boolean pReturnIntensity)
  {
    try
    {
      int lNumberOfDetectionArms = getNumberOfLightSheets();

      LightSheetMicroscopeQueue lQueue =
                                       mLightSheetMicroscope.requestQueue();
      lQueue.clearQueue();
      lQueue.zero();

      lQueue.setI(pLightSheetIndex);
      lQueue.setIZ(pLightSheetIndex, pW);

      final TDoubleArrayList lPList = new TDoubleArrayList();

      lQueue.setIP(pLightSheetIndex, pMinP);

      for (int i = 0; i < lNumberOfDetectionArms; i++)
      {
        lQueue.setDZ(i, 0);
        lQueue.setC(i, false);
      }
      lQueue.addCurrentStateToQueue();

      for (int i = 0; i < lNumberOfDetectionArms; i++)
        lQueue.setC(i, true);

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
      for (int i = 0; i < lNumberOfDetectionArms; i++)
      {
        lQueue.setDZ(i, 0);
        lQueue.setC(i, false);
      }
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

        final double[] lAvgIntensityArray =
                                          ImageAnalysisUtils.computeAverageSquareVariationPerPlane(lStack);

        smooth(lAvgIntensityArray, 1);

        PlotTab lPlot =
                      mMultiPlotAdjustPCurves.getPlot(String.format("Mode=%s, D=%d, I=%d, W=%g",
                                                                    pReturnIntensity ? "ret_int"
                                                                                     : "ret_pow",
                                                                    pDetectionArmIndex,
                                                                    pLightSheetIndex,
                                                                    pW));
        lPlot.setScatterPlot("samples");

        // System.out.format("metric array: \n");
        for (int j = 0; j < lAvgIntensityArray.length; j++)
        {
          lPlot.addPoint("samples",
                         lPList.get(j),
                         lAvgIntensityArray[j]);
          /*System.out.format("%d,%d\t%g\t%g\n",
          									i,
          									j,
          									lAList.get(j),
          									lMetricArray[j]);/**/
        }
        lPlot.ensureUpToDate();

        if (pReturnIntensity)
        {
          double lAvgIntensity =
                               StatUtils.percentile(lAvgIntensityArray,
                                                    50);
          return lAvgIntensity;
        }
        else
        {
          int lIndex = find(lAvgIntensityArray, pTargetIntensity);

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
   * Applies the correction for a given lightsheet and detection arm.
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
                                              mWPFunctions.get(pLightSheetIndex,
                                                               pDetectionArmIndex);

    Variable<PolynomialFunction> lFunctionVariable =
                                                   lLightSheetDevice.getWidthPowerFunction();

    System.out.format("Current WidthPower function: %s \n",
                      lFunctionVariable.get());

    lFunctionVariable.set(lNewWidthPowerFunction);

    System.out.format("New WidthPower function: %s \n",
                      lFunctionVariable.get());

    double lError = 0;

    return lError;
  }

  /**
   * Resets this calibration
   */
  public void reset()
  {
    mMultiPlotAdjustPCurves.clear();
    mMultiPlotAdjustPCurves.setVisible(false);
    mWPFunctions.clear();
  }

}
