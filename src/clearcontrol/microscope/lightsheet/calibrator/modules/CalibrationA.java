package clearcontrol.microscope.lightsheet.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.Fitting1D;
import clearcontrol.core.math.argmax.SmartArgMaxFinder;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;

public class CalibrationA
{

  private final LightSheetMicroscope mLightSheetMicroscope;
  private ArgMaxFinder1DInterface mArgMaxFinder;
  private MultiPlot mMultiPlotAFocusCurves;
  private HashMap<Integer, UnivariateAffineFunction> mModels;
  private int mNumberOfDetectionArmDevices;
  private int mNumberOfLightSheetDevices;

  public CalibrationA(Calibrator pCalibrator)
  {
    super();
    mLightSheetMicroscope = pCalibrator.getLightSheetMicroscope();

    mMultiPlotAFocusCurves =
                           MultiPlot.getMultiPlot(this.getClass()
                                                      .getSimpleName()
                                                  + " calibration: focus curves");
    mMultiPlotAFocusCurves.setVisible(false);

    mNumberOfDetectionArmDevices =
                                 mLightSheetMicroscope.getDeviceLists()
                                                      .getNumberOfDevices(DetectionArmInterface.class);

    mNumberOfLightSheetDevices =
                               mLightSheetMicroscope.getDeviceLists()
                                                    .getNumberOfDevices(LightSheetInterface.class);

    mModels = new HashMap<>();
  }

  public void calibrate(int pLightSheetIndex, int pNumberOfAngles)
  {
    mArgMaxFinder = new SmartArgMaxFinder();

    mMultiPlotAFocusCurves.clear();
    if (!mMultiPlotAFocusCurves.isVisible())
      mMultiPlotAFocusCurves.setVisible(true);

    LightSheetInterface lLightSheet =
                                    mLightSheetMicroscope.getDeviceLists()
                                                         .getDevice(LightSheetInterface.class,
                                                                    pLightSheetIndex);

    System.out.println("Current Alpha function: "
                       + lLightSheet.getAlphaFunction());

    double lMinA = -25;
    double lMaxA = 25;

    double lMinY = lLightSheet.getYVariable().getMin();
    double lMaxY = lLightSheet.getYVariable().getMax();

    double[] angles = new double[mNumberOfDetectionArmDevices];
    int lCount = 0;

    double y = 0.5 * min(abs(lMinY), abs(lMaxY));

    {
      System.out.format("Searching for optimal alpha angles for lighsheet at y=+/-%g \n",
                        y);

      final double[] anglesM = focusA(pLightSheetIndex,
                                      lMinA,
                                      lMaxA,
                                      (lMaxA - lMinA)
                                             / (pNumberOfAngles - 1),
                                      -y);

      final double[] anglesP = focusA(pLightSheetIndex,
                                      lMinA,
                                      lMaxA,
                                      (lMaxA - lMinA)
                                             / (pNumberOfAngles - 1),
                                      +y);

      System.out.format("Optimal alpha angles for lighsheet at y=%g: %s \n",
                        -y,
                        Arrays.toString(anglesM));
      System.out.format("Optimal alpha angles for lighsheet at y=%g: %s \n",
                        +y,
                        Arrays.toString(anglesP));

      boolean lValid = true;

      for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
        lValid &=
               !Double.isNaN(anglesM[i]) && !Double.isNaN(anglesM[i]);

      if (lValid)
      {
        System.out.format("Angle values are valid, we proceed... \n");
        for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
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

    for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
      angles[i] = angles[i] / lCount;

    System.out.format("Averaged alpha angles: %s \n",
                      Arrays.toString(angles));

    double angle = 0;
    for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
      angle += angles[i];
    angle /= mNumberOfDetectionArmDevices;

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
                          double pY)
  {
    try
    {
      mLightSheetMicroscope.clearQueue();
      mLightSheetMicroscope.zero();

      mLightSheetMicroscope.setI(pLightSheetIndex);

      final TDoubleArrayList lAList = new TDoubleArrayList();
      double[] angles = new double[mNumberOfDetectionArmDevices];

      mLightSheetMicroscope.setIY(pLightSheetIndex, 0);
      mLightSheetMicroscope.setIY(pLightSheetIndex, pY);
      mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
      mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
      mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
      mLightSheetMicroscope.setIA(pLightSheetIndex, pMinA);
      for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
      {
        mLightSheetMicroscope.setDZ(i, 0);
        mLightSheetMicroscope.setC(i, false);
      }
      mLightSheetMicroscope.addCurrentStateToQueue();

      for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
        mLightSheetMicroscope.setC(i, true);

      for (double a = pMinA; a <= pMaxA; a += pStep)
      {
        lAList.add(a);

        mLightSheetMicroscope.setIA(pLightSheetIndex, a);

        mLightSheetMicroscope.addCurrentStateToQueue();
      }

      mLightSheetMicroscope.setIA(pLightSheetIndex, pMinA);
      for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
      {
        mLightSheetMicroscope.setDZ(i, 0);
        mLightSheetMicroscope.setC(i, false);
      }
      mLightSheetMicroscope.addCurrentStateToQueue();

      mLightSheetMicroscope.finalizeQueue();

      mLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      mLightSheetMicroscope.playQueueAndWaitForStacks(mLightSheetMicroscope.getQueueLength(),
                                                                                      TimeUnit.SECONDS);

      if (lPlayQueueAndWait)
        for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
        {
          final StackInterface lStackInterface =
                                               mLightSheetMicroscope.getStackVariable(i)
                                                                    .get();

          OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage =
                                                                         (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

          // final double[] lDCTSArray =
          // mDCTS2D.computeImageQualityMetric(lImage);
          final double[] lAvgIntensityArray =
                                            ImageAnalysisUtils.computeAveragePowerIntensityPerPlane(lImage);

          smooth(lAvgIntensityArray, 10);

          PlotTab lPlot =
                        mMultiPlotAFocusCurves.getPlot(String.format("D=%d, I=%d, IY=%g",
                                                                     i,
                                                                     pLightSheetIndex,
                                                                     pY));
          lPlot.setScatterPlot("samples");

          // System.out.format("metric array: \n");
          for (int j = 0; j < lAvgIntensityArray.length; j++)
          {
            lPlot.addPoint("samples",
                           lAList.get(j),
                           lAvgIntensityArray[j]);
            /*System.out.format("%d,%d\t%g\t%g\n",
            									i,
            									j,
            									lAList.get(j),
            									lMetricArray[j]);/**/
          }
          lPlot.ensureUpToDate();

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

            lPlot.setScatterPlot("argmax");
            lPlot.addPoint("argmax", lArgMax, 0);

            if (lAmplitudeRatio > 0.1 && lArgMax > lAList.get(0))
              angles[i] = lArgMax;
            else
              angles[i] = Double.NaN;

            if (mArgMaxFinder instanceof Fitting1D)
            {
              Fitting1D lFitting1D = (Fitting1D) mArgMaxFinder;

              double[] lFit =
                            lFitting1D.fit(lAList.toArray(),
                                           new double[lAList.size()]);

              for (int j = 0; j < lAList.size(); j++)
              {
                lPlot.setScatterPlot("fit");
                lPlot.addPoint("fit", lAList.get(j), lFit[j]);
              }
            }

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

  public double apply(int pLightSheetIndex)
  {
    System.out.println("LightSheet index: " + pLightSheetIndex);

    LightSheetInterface lLightSheetDevice =
                                          mLightSheetMicroscope.getDeviceLists()
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

  public void reset()
  {
    mMultiPlotAFocusCurves.clear();
    mMultiPlotAFocusCurves.setVisible(false);
    mModels.clear();
  }

}
