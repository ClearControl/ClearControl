package clearcontrol.microscope.lightsheet.calibrator.modules.impl;

import static java.lang.Math.abs;
import static java.lang.Math.log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationBase;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationModuleInterface;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.stack.OffHeapPlanarStack;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Lightsheets power calibration module
 *
 * @author royer
 */
public class CalibrationP extends CalibrationBase
                          implements CalibrationModuleInterface
{

  private TDoubleArrayList mRatioList;

  /**
   * Instantiates a lightsheets power calibration module
   * 
   * @param pCalibrator
   *          parent calibrator
   */
  public CalibrationP(Calibrator pCalibrator)
  {
    super(pCalibrator);
  }

  /**
   * Calibrates the lightsheets power
   * 
   * @return true for success
   */
  public boolean calibrate()
  {
    int lNumberOfLightSheets = getNumberOfLightSheets();

    TDoubleArrayList lAverageIntensityList = new TDoubleArrayList();
    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      Double lValue = calibrate(l, 0, 6);
      if (lValue == null)
        return false;
      lAverageIntensityList.add(lValue);

      if (ScriptingEngine.isCancelRequestedStatic())
        return false;
    }

    System.out.format("Average image intensity list: %s \n",
                      lAverageIntensityList);

    double lWeakestLightSheetIntensity = lAverageIntensityList.min();

    System.out.format("Weakest lightsheet intensity: %g \n",
                      lWeakestLightSheetIntensity);

    mRatioList = new TDoubleArrayList();
    for (int l = 0; l < lNumberOfLightSheets; l++)
      mRatioList.add(lWeakestLightSheetIntensity
                     / lAverageIntensityList.get(l));

    System.out.format("Intensity ratios list: %s \n", mRatioList);

    return true;
  }

  /**
   * Calibrates the power of a given lightsheet usinga given detection arm
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pDetectionArmIndex
   *          detection arm
   * @param pNumberOfSamples
   *          number of samples
   * @return average intensity
   */
  public Double calibrate(int pLightSheetIndex,
                          int pDetectionArmIndex,
                          int pNumberOfSamples)
  {
    try
    {
      int lNumberOfDetectionArms = getNumberOfDetectionArms();

      LightSheetMicroscopeQueue lQueue =
                                       mLightSheetMicroscope.requestQueue();
      lQueue.setI(pLightSheetIndex);
      ThreadUtils.sleep(200, TimeUnit.MILLISECONDS);

      // Building queue start:
      lQueue.clearQueue();
      lQueue.zero();

      lQueue.setIX(pLightSheetIndex, 0);
      lQueue.setIY(pLightSheetIndex, 0);
      lQueue.setIZ(pLightSheetIndex, 0);
      lQueue.setIH(pLightSheetIndex, 0);
      lQueue.setIZ(pLightSheetIndex, 0);
      lQueue.setIP(pLightSheetIndex, 1);

      for (int i = 0; i < lNumberOfDetectionArms; i++)
        lQueue.setDZ(i, 0);

      for (int i = 1; i <= pNumberOfSamples; i++)
      {
        for (int d = 0; d < lNumberOfDetectionArms; d++)
          lQueue.setC(d, i == pNumberOfSamples);
        lQueue.addCurrentStateToQueue();
      }

      lQueue.addVoxelDimMetaData(mLightSheetMicroscope, 10);

      lQueue.finalizeQueue();
      // Building queue end.

      mLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      mLightSheetMicroscope.playQueueAndWaitForStacks(lQueue,
                                                                                      lQueue.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (!lPlayQueueAndWait)
        return null;

      final OffHeapPlanarStack lStack =
                                      (OffHeapPlanarStack) mLightSheetMicroscope.getCameraStackVariable(pDetectionArmIndex)
                                                                                .get();

      long lWidth = lStack.getWidth();
      long lHeight = lStack.getHeight();

      System.out.format("Image: width=%d, height=%d \n",
                        lWidth,
                        lHeight);

      double lAverageIntensity =
                               ImageAnalysisUtils.computeImageAverageIntensityPerPlane(lStack)[0];

      System.out.format("Image: average intensity: %s \n",
                        lAverageIntensity);

      return lAverageIntensity;
    }
    catch (InterruptedException | ExecutionException
        | TimeoutException e)
    {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Applies correction to the lighsheets power settings
   * 
   * @return residual error
   */
  public double apply()
  {
    int lNumberOfLightSheets = getNumberOfLightSheets();

    double lError = 0;

    for (int l = 0; l < lNumberOfLightSheets; l++)
    {
      System.out.format("Light sheet index: %d \n", l);

      LightSheetInterface lLightSheetDevice =
                                            mLightSheetMicroscope.getDeviceLists()
                                                                 .getDevice(LightSheetInterface.class,
                                                                            l);

      Variable<UnivariateAffineFunction> lPowerFunctionVariable =
                                                                lLightSheetDevice.getPowerFunction();

      double lPowerRatio = mRatioList.get(l);

      if (lPowerRatio == 0)
        continue;

      System.out.format("Applying power ratio correction: %g to lightsheet %d \n",
                        lPowerRatio,
                        l);

      lPowerFunctionVariable.get()
                            .composeWith(UnivariateAffineFunction.axplusb(lPowerRatio,
                                                                          0));
      lPowerFunctionVariable.setCurrent();

      System.out.format("Power function for lightsheet %d is now: %s \n",
                        l,
                        lPowerFunctionVariable.get());

      lError += abs(log(lPowerRatio));
    }

    System.out.format("Error after applying power ratio correction: %g \n",
                      lError);

    return lError;
  }

  /**
   * Resets calibration
   */
  @Override
  public void reset()
  {

  }
}
