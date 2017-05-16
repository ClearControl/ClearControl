package clearcontrol.microscope.lightsheet.calibrator.modules.impl;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationBase;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationModuleInterface;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.stack.OffHeapPlanarStack;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.stat.StatUtils;

/**
 * Lightsheet width calibration module
 *
 * @author royer
 */
public class CalibrationW extends CalibrationBase
                          implements CalibrationModuleInterface
{

  private HashMap<Integer, TDoubleArrayList> mIntensityLists;
  private TDoubleArrayList mWList = new TDoubleArrayList();

  /**
   * Instantiates a lightsheet width calibration module
   * 
   * @param pCalibrator
   *          parent calibrator
   */
  public CalibrationW(CalibrationEngine pCalibrator)
  {
    super(pCalibrator);

  }

  /**
   * Instantiates a lightsheet width calibration module
   * 
   * @param pDetectionArmIndex
   *          detection arm index
   * @param pNumberOfSamples
   *          number of samples
   * @return true for success
   */
  public boolean calibrate(int pDetectionArmIndex,
                           int pNumberOfSamples)
  {
    mIntensityLists.clear();
    int lNumberOfLightSheets = getNumberOfLightSheets();
    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      double[] lAverageIntensities = calibrate(l,
                                               pDetectionArmIndex,
                                               3);
      if (lAverageIntensities == null)
        return false;

      mIntensityLists.put(l,
                          new TDoubleArrayList(lAverageIntensities));

      if (ScriptingEngine.isCancelRequestedStatic())
        return false;
    }

    return true;
  }

  /**
   * Calibrates the lightsheet width for a given lightsheet, detection arm, and
   * number of samples.
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pDetectionArmIndex
   *          detection arm index
   * @param pNumberOfSamples
   *          number of samples
   * @return metric value per plane.
   */
  public double[] calibrate(int pLightSheetIndex,
                            int pDetectionArmIndex,
                            int pNumberOfSamples)
  {

    try
    {
      LightSheetInterface lLightSheetDevice =
                                            getLightSheetMicroscope().getDeviceLists()
                                                                     .getDevice(LightSheetInterface.class,
                                                                                pLightSheetIndex);

      BoundedVariable<Number> lWVariable =
                                         lLightSheetDevice.getWidthVariable();

      @SuppressWarnings("unused")
      UnivariateAffineFunction lWFunction =
                                          lLightSheetDevice.getWidthFunction()
                                                           .get();
      double lMinW = lWVariable.getMin().doubleValue();
      double lMaxW = lWVariable.getMax().doubleValue();
      double lStep = (lMaxW - lMinW) / pNumberOfSamples;

      // Building queue start:
      LightSheetMicroscopeQueue lQueue =
                                       getLightSheetMicroscope().requestQueue();
      lQueue.clearQueue();
      lQueue.zero();

      lQueue.setI(pLightSheetIndex);
      lQueue.setIX(pLightSheetIndex, 0);
      lQueue.setIY(pLightSheetIndex, 0);
      lQueue.setIZ(pLightSheetIndex, 0);
      lQueue.setIH(pLightSheetIndex, 0);

      lQueue.setDZ(pDetectionArmIndex, 0);
      lQueue.setC(pDetectionArmIndex, false);

      lQueue.setIZ(pLightSheetIndex, lMinW);
      lQueue.addCurrentStateToQueue();

      mWList.clear();
      for (double w = lMinW; w <= lMaxW; w += lStep)
      {
        mWList.add(w);
        lQueue.setIZ(pLightSheetIndex, w);

        lQueue.setC(pDetectionArmIndex, false);
        for (int i = 0; i < 10; i++)
          lQueue.addCurrentStateToQueue();

        lQueue.setC(pDetectionArmIndex, true);
        lQueue.addCurrentStateToQueue();
      }

      lQueue.addVoxelDimMetaData(getLightSheetMicroscope(), 10);

      lQueue.finalizeQueue();
      // Building queue end.

      getLightSheetMicroscope().useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      getLightSheetMicroscope().playQueueAndWaitForStacks(lQueue,
                                                                                          lQueue.getQueueLength(),
                                                                                          TimeUnit.SECONDS);

      if (!lPlayQueueAndWait)
        return null;

      final OffHeapPlanarStack lStack =
                                      (OffHeapPlanarStack) getLightSheetMicroscope().getCameraStackVariable(pDetectionArmIndex)
                                                                                    .get();

      long lWidth = lStack.getWidth();
      long lHeight = lStack.getHeight();

      System.out.format("Image: width=%d, height=%d \n",
                        lWidth,
                        lHeight);

      double[] lAverageIntensities =
                                   ImageAnalysisUtils.computeImageAverageIntensityPerPlane(lStack);

      System.out.format("Image: average intensities: \n");

      String lChartName = String.format("D=%d, I=%d",
                                        pDetectionArmIndex,
                                        pLightSheetIndex);

      getCalibrationEngine().configureChart(lChartName,
                                            "avg. intensity",
                                            "IW",
                                            "intensity",
                                            ChartType.Line);

      for (int i = 0; i < lAverageIntensities.length; i++)
      {
        System.out.println(lAverageIntensities[i]);
        getCalibrationEngine().addPoint(lChartName,
                                        "avg. intensity",
                                        i == 0,
                                        mWList.get(i),
                                        lAverageIntensities[i]);
      }

      return lAverageIntensities;
    }
    catch (InterruptedException | ExecutionException
        | TimeoutException e)
    {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Applies the lighsheet width calibration corrections
   * 
   * @return residual error
   */
  public double apply()
  {
    int lNumberOfLightSheets = getNumberOfLightSheets();

    double lError = 0;

    double lIntensityMin = Double.POSITIVE_INFINITY;
    double lIntensityMax = Double.NEGATIVE_INFINITY;

    TDoubleArrayList lSums = new TDoubleArrayList();
    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      TDoubleArrayList lIntensityList = mIntensityLists.get(l);
      lSums.add(lIntensityList.sum());

      lIntensityMin = min(lIntensityMin, lIntensityList.min());
      lIntensityMax = max(lIntensityMax, lIntensityList.max());
    }

    double lLargestSum = Double.NEGATIVE_INFINITY;
    int lIndexOfLargestSum = -1;
    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      double lSum = lSums.get(l);
      if (lSum > lLargestSum)
      {
        lIndexOfLargestSum = l;
        lLargestSum = lSum;
      }
    }

    TDoubleArrayList lReferenceIntensityList =
                                             mIntensityLists.get(lIndexOfLargestSum);

    TDoubleArrayList[] lOffsetsLists =
                                     new TDoubleArrayList[lNumberOfLightSheets];
    for (int l = 0; l < lNumberOfLightSheets; l++)
      lOffsetsLists[l] = new TDoubleArrayList();

    double lStep = (lIntensityMax - lIntensityMin)
                   / (lReferenceIntensityList.size());

    for (double i = lIntensityMin; i <= lIntensityMax; i += lStep)
    {
      for (int l = 0; l < lNumberOfLightSheets; l++)
      {

        int lReferenceIndex =
                            searchFirstAbove(lReferenceIntensityList,
                                             i);
        double lReferenceW = mWList.get(lReferenceIndex);

        int lOtherIndex =
                        searchFirstAbove(lReferenceIntensityList, i);

        double lOtherW = mWList.get(lOtherIndex);

        double lOffsets = lOtherW - lReferenceW;

        lOffsetsLists[l].add(lOffsets);

      }
    }

    TDoubleArrayList lMedianOffsets = new TDoubleArrayList();
    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      double lMedianOffset =
                           StatUtils.percentile(lOffsetsLists[l].toArray(),
                                                50);
      lMedianOffsets.add(lMedianOffset);
    }

    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      LightSheetInterface lLightSheetDevice =
                                            getLightSheetMicroscope().getDeviceLists()
                                                                     .getDevice(LightSheet.class,
                                                                                l);

      UnivariateAffineFunction lFunction =
                                         lLightSheetDevice.getWidthFunction()
                                                          .get();

      double lOffset = lMedianOffsets.get(l);

      System.out.format("Applying offset: %g to lightsheet %d \n",
                        lOffset,
                        l);

      lFunction.composeWith(UnivariateAffineFunction.axplusb(1,
                                                             lOffset));

      System.out.format("Width function for lightsheet %d is now: %s \n",
                        l,
                        lFunction);

      lError += abs(lOffset);
    }

    System.out.format("Error after applying width offset correction: %g \n",
                      lError);

    return lError;
  }

  private int searchFirstAbove(TDoubleArrayList pList, double pValue)
  {
    int lSize = pList.size();
    for (int i = 0; i < lSize; i++)
      if (pList.getQuick(i) >= pValue)
        return i;
    return lSize - 1;
  }

  /**
   * Resets calibration
   */
  @Override
  public void reset()
  {
    super.reset();
  }
}
