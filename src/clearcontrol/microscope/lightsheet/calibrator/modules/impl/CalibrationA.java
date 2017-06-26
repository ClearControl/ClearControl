package clearcontrol.microscope.lightsheet.calibrator.modules.impl;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.SmartArgMaxFinder;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationBase;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationModuleInterface;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.stack.OffHeapPlanarStack;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Lightsheet A angle calibration module
 *
 * @author royer
 */
public class CalibrationA extends CalibrationBase
                          implements CalibrationModuleInterface
{

  private ArgMaxFinder1DInterface mArgMaxFinder;
  private HashMap<Integer, UnivariateAffineFunction> mModels;

  /**
   * Lightsheet Alpha angle calibration module
   * 
   * @param pCalibrator
   *          parent calibrator
   */
  public CalibrationA(CalibrationEngine pCalibrator)
  {
    super(pCalibrator);
    mModels = new HashMap<>();
  }

  /**
   * Calibrates the Alpha angle for a given lightsheet, number of angles and
   * number of repeats.
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pNumberOfAngles
   *          numbe rof angles
   * @param pNumberOfRepeats
   *          number of repeats.
   */
  public void calibrate(int pLightSheetIndex,
                        int pNumberOfAngles,
                        int pNumberOfRepeats)
  {
    int lNumberOfDetectionArmDevices = getNumberOfDetectionArms();

    mArgMaxFinder = new SmartArgMaxFinder();

    LightSheetInterface lLightSheet =
                                    getLightSheetMicroscope().getDeviceLists()
                                                             .getDevice(LightSheetInterface.class,
                                                                        pLightSheetIndex);

    System.out.println("Current Alpha function: "
                       + lLightSheet.getAlphaFunction());

    double lMinA = -7;
    double lMaxA = 7;

    double lMinIY = lLightSheet.getYVariable().getMin().doubleValue();
    double lMaxIY = lLightSheet.getYVariable().getMax().doubleValue();

    double lMinZ = lLightSheet.getZVariable().getMin().doubleValue();
    double lMaxZ = lLightSheet.getZVariable().getMax().doubleValue();

    double[] angles = new double[lNumberOfDetectionArmDevices];
    int lCount = 0;

    double y = 0.5 * min(abs(lMinIY), abs(lMaxIY));
    double z = 0.5 * (lMaxZ + lMinZ);

    for (int r = 0; r < pNumberOfRepeats; r++)
    {
      System.out.format("Searching for optimal alpha angles for lighsheet at y=+/-%g \n",
                        y);

      final double[] anglesM = focusA(pLightSheetIndex,
                                      lMinA,
                                      lMaxA,
                                      (lMaxA - lMinA)
                                             / (pNumberOfAngles - 1),
                                      -y,
                                      z);

      final double[] anglesP = focusA(pLightSheetIndex,
                                      lMinA,
                                      lMaxA,
                                      (lMaxA - lMinA)
                                             / (pNumberOfAngles - 1),
                                      +y,
                                      z);

      System.out.format("Optimal alpha angles for lighsheet at y=%g: %s \n",
                        -y,
                        Arrays.toString(anglesM));
      System.out.format("Optimal alpha angles for lighsheet at y=%g: %s \n",
                        +y,
                        Arrays.toString(anglesP));

      boolean lValid = true;

      for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
        lValid &=
               !Double.isNaN(anglesM[i]) && !Double.isNaN(anglesM[i]);

      if (lValid)
      {
        System.out.format("Angle values are valid, we proceed... \n");
        for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
        {
          angles[i] += 0.5 * (anglesM[i] + anglesP[i]);
        }

        lCount++;
      }
      else
        System.out.format("Angle are not valid, we continue with next set of y values... \n");
    }

    if (lCount == 0)
      return;

    for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
      angles[i] = angles[i] / lCount;

    System.out.format("Averaged alpha angles: %s \n",
                      Arrays.toString(angles));

    double angle = 0;
    for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
      angle += angles[i];
    angle /= lNumberOfDetectionArmDevices;

    System.out.format("Average alpha angle for all detection arms (assumes that the cameras are well aligned): %s \n",
                      angle);

    UnivariateAffineFunction lUnivariateAffineFunction =
                                                       new UnivariateAffineFunction(1,
                                                                                    angle);
    mModels.put(pLightSheetIndex, lUnivariateAffineFunction);

    System.out.format("Corresponding model: %s \n",
                      lUnivariateAffineFunction);

  }

  private double[] focusA(int pLightSheetIndex,
                          double pMinA,
                          double pMaxA,
                          double pStep,
                          double pY,
                          double pZ)
  {
    try
    {
      int lNumberOfDetectionArmDevices = getNumberOfDetectionArms();

      LightSheetMicroscopeQueue lQueue =
                                       getLightSheetMicroscope().requestQueue();

      final TDoubleArrayList lAList = new TDoubleArrayList();
      double[] angles = new double[lNumberOfDetectionArmDevices];

      lQueue.clearQueue();
      // lQueue.zero();

      lQueue.setFullROI();
      lQueue.setExp(0.25);

      lQueue.setI(pLightSheetIndex);
      lQueue.setIX(pLightSheetIndex, 0);
      lQueue.setIY(pLightSheetIndex, pY);
      lQueue.setIZ(pLightSheetIndex, pZ);
      lQueue.setIH(pLightSheetIndex, 0);
      lQueue.setIA(pLightSheetIndex, pMinA);

      for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
      {
        lQueue.setDZ(i, pZ);
        lQueue.setC(i, false);
      }
      lQueue.addCurrentStateToQueue();

      for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
        lQueue.setC(i, true);

      for (double a = pMinA; a <= pMaxA; a += pStep)
      {
        lAList.add(a);
        lQueue.setIA(pLightSheetIndex, a);
        lQueue.addCurrentStateToQueue();
      }

      lQueue.setIA(pLightSheetIndex, pMinA);
      for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
      {
        lQueue.setC(i, false);
      }
      lQueue.addCurrentStateToQueue();

      lQueue.addVoxelDimMetaData(getLightSheetMicroscope(), 10);

      lQueue.finalizeQueue();

      getLightSheetMicroscope().useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      getLightSheetMicroscope().playQueueAndWaitForStacks(lQueue,
                                                                                          lQueue.getQueueLength(),
                                                                                          TimeUnit.SECONDS);

      if (lPlayQueueAndWait)
        for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
        {
          final OffHeapPlanarStack lStack =
                                          (OffHeapPlanarStack) getLightSheetMicroscope().getCameraStackVariable(i)
                                                                                        .get();

          final double[] lAvgIntensityArray =
                                            ImageAnalysisUtils.computeAverageSquareVariationPerPlane(lStack);

          smooth(lAvgIntensityArray, 10);

          String lChartName = String.format("D=%d, I=%d, IY=%g",
                                            i,
                                            pLightSheetIndex,
                                            pY);

          getCalibrationEngine().configureChart(lChartName,
                                                "samples",
                                                "DZ",
                                                "IZ",
                                                ChartType.Line);

          for (int j = 0; j < lAvgIntensityArray.length; j++)
          {
            getCalibrationEngine().addPoint(lChartName,
                                            "samples",
                                            j == 0,
                                            lAList.get(j),
                                            lAvgIntensityArray[j]);

          }

          final Double lArgMax =
                               mArgMaxFinder.argmax(lAList.toArray(),
                                                    lAvgIntensityArray);

          if (lArgMax != null)
          {
            TDoubleArrayList lAvgIntensityList =
                                               new TDoubleArrayList(lAvgIntensityArray);

            double lAmplitudeRatio = (lAvgIntensityList.max()
                                      - lAvgIntensityList.min())
                                     / lAvgIntensityList.max();

            System.out.format("argmax=%s amplratio=%s \n",
                              lArgMax.toString(),
                              lAmplitudeRatio);

            // lPlot.setScatterPlot("argmax");
            // lPlot.addPoint("argmax", lArgMax, 0);

            if (lAmplitudeRatio > 0.1 && lArgMax > lAList.get(0))
              angles[i] = lArgMax;
            else
              angles[i] = Double.NaN;

            /* if (mArgMaxFinder instanceof Fitting1D)
            {
              Fitting1D lFitting1D = (Fitting1D) mArgMaxFinder;
            
              double[] lFit =
                            lFitting1D.fit(lAList.toArray(),
                                           new double[lAList.size()]);
            
              for (int j = 0; j < lAList.size(); j++)
              {
                //lPlot.setScatterPlot("fit");
                //lPlot.addPoint("fit", lAList.get(j), lFit[j]);
              }
            }/**/

          }
          else
          {
            angles[i] = Double.NaN;
            System.out.println("Argmax is NULL!");
          }
        }

      return angles;

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
   * Applies the Alpha angle calibration correction to a given lightsheet
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @return residual error
   */
  public double apply(int pLightSheetIndex)
  {
    System.out.println("LightSheet index: " + pLightSheetIndex);

    LightSheetInterface lLightSheetDevice =
                                          getLightSheetMicroscope().getDeviceLists()
                                                                   .getDevice(LightSheetInterface.class,
                                                                              pLightSheetIndex);

    UnivariateAffineFunction lUnivariateAffineFunction =
                                                       mModels.get(pLightSheetIndex);

    if (lUnivariateAffineFunction == null)
    {
      System.out.format("No model available! \n");
      return Double.POSITIVE_INFINITY;
    }

    Variable<UnivariateAffineFunction> lFunctionVariable =
                                                         lLightSheetDevice.getAlphaFunction();

    System.out.format("Correction function: %s \n",
                      lUnivariateAffineFunction);

    lFunctionVariable.get().composeWith(lUnivariateAffineFunction);
    lFunctionVariable.setCurrent();

    System.out.format("New alpha function: %s \n",
                      lFunctionVariable.get());

    double lError = abs(lUnivariateAffineFunction.getSlope() - 1)
                    + abs(lUnivariateAffineFunction.getConstant());

    System.out.format("Error: %g \n", lError);

    return lError;
  }

  /**
   * Resets the Alpha angle calibration
   */
  @Override
  public void reset()
  {
    super.reset();
    mModels.clear();
  }

}
