package clearcontrol.microscope.lightsheet.calibrator.modules.impl;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcl.util.ElapsedTime;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.methods.ModeArgMaxFinder;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.math.regression.linear.TheilSenEstimator;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.ip.iqm.DCTS2D;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationBase;
import clearcontrol.microscope.lightsheet.calibrator.modules.CalibrationModuleInterface;
import clearcontrol.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.stack.OffHeapPlanarStack;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.collections4.map.MultiKeyMap;

/**
 * Calibration module for the Z position of lightsheets and detection arms
 *
 * @author royer
 */
public class CalibrationZ extends CalibrationBase
                          implements CalibrationModuleInterface
{

  private ArgMaxFinder1DInterface mArgMaxFinder;
  private MultiKeyMap<Integer, UnivariateAffineFunction> mModels;
  private int mNumberOfDetectionArmDevices;

  private boolean mUseDCTS = false;
  private DCTS2D mDCTS2D;
  private double[] mMetricArray;

  /**
   * Instantiates a Z calibrator module given calibrator
   * 
   * @param pCalibrator
   *          calibrator
   */
  public CalibrationZ(CalibrationEngine pCalibrator)
  {
    super(pCalibrator);

    mNumberOfDetectionArmDevices =
                                 getLightSheetMicroscope().getDeviceLists()
                                                          .getNumberOfDevices(DetectionArmInterface.class);

    mModels = new MultiKeyMap<>();
  }

  /**
   * Performs calibration for a given lightsheet index
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pNumberOfDSamples
   *          number of detection Z samples
   * @param pNumberOfISamples
   *          number of illumination Z samples
   * @param pRestrictedSearch
   *          true -> restrict search to an interval, false not.
   * @param pSearchAmplitude
   *          search amplitude.
   * @return true -> success
   */
  public boolean calibrate(int pLightSheetIndex,
                           int pNumberOfDSamples,
                           int pNumberOfISamples,
                           boolean pRestrictedSearch,
                           double pSearchAmplitude)
  {
    info("Starting to calibrate Z for lightsheet %d, with %d D samples, %d I samples, and a search amplitude of %g ",
         pLightSheetIndex,
         pNumberOfDSamples,
         pNumberOfISamples,
         pSearchAmplitude);

    mArgMaxFinder = new ModeArgMaxFinder();

    incrementIteration();

    final TheilSenEstimator[] lTheilSenEstimators =
                                                  new TheilSenEstimator[mNumberOfDetectionArmDevices];

    for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
      lTheilSenEstimators[d] = new TheilSenEstimator();

    LightSheetInterface lLightSheetDevice =
                                          getLightSheetMicroscope().getDeviceLists()
                                                                   .getDevice(LightSheetInterface.class,
                                                                              pLightSheetIndex);

    BoundedVariable<Number> lZVariable =
                                       lLightSheetDevice.getZVariable();
    double lMinIZ = lZVariable.getMin().doubleValue();
    double lMaxIZ = lZVariable.getMax().doubleValue();

    double lStepIZ = (lMaxIZ - lMinIZ) / (pNumberOfISamples - 1);

    double lMinDZ = Double.NEGATIVE_INFINITY;
    double lMaxDZ = Double.POSITIVE_INFINITY;

    double lDZSearchRadius =
                           0.5 * pSearchAmplitude * (lMaxIZ - lMinIZ);

    info("Range for Iz values: [%g,%g] with a step size of %g, Dz search radius is %g \n",
         lMinIZ,
         lMaxIZ,
         lStepIZ,
         lDZSearchRadius);

    for (double iz = lMinIZ; iz <= lMaxIZ; iz += lStepIZ)
    {

      final double lPerturbedIZ = iz + 0.1 * lStepIZ
                                       * (2 * Math.random() - 1);

      // TODO: this does not work when the calibration is really off:
      if (pRestrictedSearch)
      {
        lMinDZ = lPerturbedIZ - lDZSearchRadius;
        lMaxDZ = lPerturbedIZ + lDZSearchRadius;
      }

      final double[] dz = focusZ(pLightSheetIndex,
                                 pNumberOfDSamples,
                                 lMinDZ,
                                 lMaxDZ,
                                 lPerturbedIZ);

      if (dz == null)
        return false;

      String lChartName =
                        this.getClass().getSimpleName() + " DZ v. IZ";

      String lSeriesName = "measured";

      getCalibrationEngine().configureChart(lChartName,
                                            lSeriesName,
                                            "DZ",
                                            "IZ",
                                            ChartType.Line);

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
        if (!Double.isNaN(dz[d]))
        {
          lTheilSenEstimators[d].enter(dz[d], lPerturbedIZ);

          getCalibrationEngine().addPoint(lChartName,
                                          lSeriesName,
                                          iz == lMinIZ,

                                          dz[d],
                                          lPerturbedIZ);

        }

      if (getCalibrationEngine().isStopRequested())
        return false;

    }

    for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
    {
      /*final UnivariateAffineFunction lModel =
                                            lTheilSenEstimators[d].getModel();/**/

      // System.out.println("lModel=" + lModel);

      mModels.put(pLightSheetIndex,
                  d,
                  lTheilSenEstimators[d].getModel());

      BoundedVariable<Number> lDetectionFocusZVariable =
                                                       getLightSheetMicroscope().getDeviceLists()
                                                                                .getDevice(DetectionArmInterface.class,
                                                                                           d)
                                                                                .getZVariable();

      lMinDZ = lDetectionFocusZVariable.getMin().doubleValue();
      lMaxDZ = lDetectionFocusZVariable.getMax().doubleValue();
      double lStepDZ = (lMaxDZ - lMinDZ) / 1000;

      String lChartName =
                        this.getClass().getSimpleName() + " DZ v. IZ";

      String lSeriesName = "fit";

      getCalibrationEngine().configureChart(lChartName,
                                            lSeriesName,
                                            "DZ",
                                            "IZ",
                                            ChartType.Line);

      for (double z = lMinDZ; z <= lMaxDZ; z += lStepDZ)
      {
        getCalibrationEngine().addPoint(lChartName,
                                        lSeriesName,
                                        z == lMinDZ,
                                        z,
                                        mModels.get(pLightSheetIndex,
                                                    d)
                                               .value(z));

      }

    }

    return true;
  }

  private double[] focusZ(int pLightSheetIndex,
                          int pNumberOfDSamples,
                          double pMinDZ,
                          double pMaxDZ,
                          double pIZ)
  {

    try
    {

      double lMinDZ = pMinDZ;
      double lMaxDZ = pMaxDZ;

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
      {
        BoundedVariable<Number> lDetectionFocusZVariable =
                                                         getLightSheetMicroscope().getDeviceLists()
                                                                                  .getDevice(DetectionArmInterface.class,
                                                                                             d)
                                                                                  .getZVariable();

        lMinDZ = max(lMinDZ,
                     lDetectionFocusZVariable.getMin().doubleValue());
        lMaxDZ = min(lMaxDZ,
                     lDetectionFocusZVariable.getMax().doubleValue());
      }

      info("Focussing for lightsheet %d at %g, with %d D samples, with Dz values within [%g,%g] \n",
           pLightSheetIndex,
           pIZ,
           pNumberOfDSamples,
           pMinDZ,
           pMaxDZ);

      double lStep = (lMaxDZ - lMinDZ) / (pNumberOfDSamples - 1);

      // info("Begin building queue");
      LightSheetMicroscopeQueue lQueue =
                                       getLightSheetMicroscope().requestQueue();
      lQueue.clearQueue();
      // lQueue.zero();

      lQueue.setFullROI();
      lQueue.setExp(0.25);

      lQueue.setI(pLightSheetIndex);
      lQueue.setIX(pLightSheetIndex, 0);
      lQueue.setIY(pLightSheetIndex, 0);
      lQueue.setIZ(pLightSheetIndex, lMinDZ);
      lQueue.setIH(pLightSheetIndex, 0);

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

        lQueue.setIZ(pLightSheetIndex, pIZ);

        lQueue.addCurrentStateToQueue();
      }

      lQueue.addVoxelDimMetaData(getLightSheetMicroscope(), 10);

      for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
      {
        lQueue.setDZ(d, lMinDZ);
        lQueue.setC(d, false);
      }
      lQueue.addCurrentStateToQueue();

      lQueue.setTransitionTime(0.1);

      lQueue.finalizeQueue();
      // info("End building queue");

      /* ScoreVisualizerJFrame.visualize("queuedscore",
      																mLightSheetMicroscope.getDeviceLists()
      																											.getDevice(NIRIOSignalGenerator.class, 0)
      																											.get());/**/

      // info("Begin play queue");
      getLightSheetMicroscope().useRecycler("adaptation", 1, 4, 4);
      final Boolean lPlayQueueAndWait =
                                      getLightSheetMicroscope().playQueueAndWaitForStacks(lQueue,
                                                                                          100 + lQueue.getQueueLength(),
                                                                                          TimeUnit.SECONDS);
      // info("End play queue");

      if (lPlayQueueAndWait)
        for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
        {
          final OffHeapPlanarStack lStack =
                                          (OffHeapPlanarStack) getLightSheetMicroscope().getCameraStackVariable(d)
                                                                                        .get();

          if (lStack == null)
            continue;

          // info("Begin compute metric");
          ElapsedTime.measureForceOutput("compute metric", () -> {
            if (mUseDCTS)
            {
              if (mDCTS2D == null)
                mDCTS2D = new DCTS2D();

              mMetricArray =
                           mDCTS2D.computeImageQualityMetric(lStack);
            }
            else
              mMetricArray =
                           ImageAnalysisUtils.computeAverageSquareVariationPerPlane(lStack);/**/
          });
          // info("Begin compute metric");

          if (lDZList.size() != mMetricArray.length)
            severe("Z position list and metric list have different lengths!");

          // System.out.format("metric array: \n");

          String lChartName = String.format("D=%d, I=%d",
                                            d,
                                            pLightSheetIndex);

          String lSeriesName = String.format("iteration=%d",
                                             getIteration());

          getCalibrationEngine().configureChart(lChartName,
                                                lSeriesName,
                                                "ΔZ",
                                                "focus metric",
                                                ChartType.Line);

          for (int j = 0; j < lDZList.size(); j++)
          {
            getCalibrationEngine().addPoint(lChartName,
                                            lSeriesName,
                                            j == 0,
                                            lDZList.get(j),
                                            mMetricArray[j]);
          }

          // info("Begin argmax");
          final Double lArgMax =
                               mArgMaxFinder.argmax(lDZList.toArray(),
                                                    mMetricArray);
          // info("End argmax");

          if (lArgMax != null)
          {
            TDoubleArrayList lDCTSList =
                                       new TDoubleArrayList(mMetricArray);

            double lAmplitudeRatio =
                                   (lDCTSList.max() - lDCTSList.min())
                                     / lDCTSList.max();

            /*System.out.format("argmax=%s amplratio=%s \n",
                              lArgMax.toString(),
                              lAmplitudeRatio);/**/

            if (lAmplitudeRatio > 0.1 && lArgMax > lDZList.get(0))
              dz[d] = lArgMax;
            else
              dz[d] = Double.NaN;

            /*if (mArgMaxFinder instanceof Fitting1D)
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
            }/**/

          }
          else
          {
            dz[d] = Double.NaN;
            severe("Argmax is NULL!");
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

  /**
   * Applies correction for a given lightsheet index.
   * 
   * @param pLightSheetIndex
   *          lightsheet index
   * @param pAdjustDetectionZ
   *          this flag determines whther the coreection should be applied to
   *          the detection arms too
   * @return calibration error
   */
  public double apply(int pLightSheetIndex, boolean pAdjustDetectionZ)
  {
    if (getCalibrationEngine().isStopRequested())
      return Double.NaN;

    double lSlope = 0, lOffset = 0;

    for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
    {
      lSlope += mModels.get(pLightSheetIndex, 0).getSlope();
      lOffset += mModels.get(pLightSheetIndex, 0).getConstant();
    }

    lSlope /= mNumberOfDetectionArmDevices;
    lOffset /= mNumberOfDetectionArmDevices;

    final LightSheetInterface lLightSheetDevice =
                                                getLightSheetMicroscope().getDeviceLists()
                                                                         .getDevice(LightSheetInterface.class,
                                                                                    pLightSheetIndex);

    /*System.out.println("before: getZFunction()="
                       + lLightSheetDevice.getZFunction());/**/

    if (abs(lSlope) > 0.00001 && !Double.isNaN(lSlope)
        && !Double.isNaN(lOffset))
    {
      lLightSheetDevice.getZFunction()
                       .get()
                       .composeWith(new UnivariateAffineFunction(lSlope,
                                                                 lOffset));
      lLightSheetDevice.getZFunction().setCurrent();
    }
    else
    {
      if (abs(lSlope) <= 0.00001)
        warning("slope too low: " + abs(lSlope));
      else
        warning("inval;id slope or offset: (y= %g x + %g)",
                lSlope,
                lOffset);

    }

    /*
    System.out.println("after: getZFunction()="
                       + lLightSheetDevice.getZFunction());
    
    System.out.println("before: getYFunction()="
                       + lLightSheetDevice.getYFunction());/**/

    adjustYFunctionScale(lLightSheetDevice);

    if (mNumberOfDetectionArmDevices == 2 && pAdjustDetectionZ)
      applyDetectionZ(pLightSheetIndex);

    double lError = abs(1 - lSlope) + abs(lOffset);

    info("Error=" + lError);

    return lError;

  }

  protected void adjustYFunctionScale(final LightSheetInterface lLightSheetDevice)
  {
    MachineConfiguration lMachineConfiguration =
                                               MachineConfiguration.get();

    Double lXYRatio =
                    lMachineConfiguration.getDoubleProperty("device.lsm.lighsheet.yzratio",
                                                            1.0);

    lLightSheetDevice.getYFunction()
                     .set(UnivariateAffineFunction.axplusb(lLightSheetDevice.getZFunction()
                                                                            .get()
                                                                            .getSlope()
                                                           * lXYRatio,
                                                           0));
  }

  protected void applyDetectionZ(int pLightSheetIndex)
  {
    double a0 = mModels.get(pLightSheetIndex, 0).getSlope();
    double b0 = mModels.get(pLightSheetIndex, 0).getConstant();
    double a1 = mModels.get(pLightSheetIndex, 1).getSlope();
    double b1 = mModels.get(pLightSheetIndex, 1).getConstant();

    double lDZIntercept0 = -b0 / a0;
    double lDZIntercept1 = -b1 / a1;

    System.out.println("lDZIntercept0=" + lDZIntercept0);
    System.out.println("lDZIntercept1=" + lDZIntercept1);

    double lDesiredIntercept = 0.5 * (lDZIntercept0 + lDZIntercept1);

    System.out.println("lDesiredIntercept=" + lDesiredIntercept);

    double lInterceptCorrection0 =
                                 -(lDesiredIntercept - lDZIntercept0);
    double lInterceptCorrection1 =
                                 -(lDesiredIntercept - lDZIntercept1);

    System.out.println("lInterceptCorrection0="
                       + lInterceptCorrection0);
    System.out.println("lInterceptCorrection1="
                       + lInterceptCorrection1);

    final DetectionArmInterface lDetectionArmDevice0 =
                                                     getLightSheetMicroscope().getDeviceLists()
                                                                              .getDevice(DetectionArmInterface.class,
                                                                                         0);
    final DetectionArmInterface lDetectionArmDevice1 =
                                                     getLightSheetMicroscope().getDeviceLists()
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

  @Override
  public void reset()
  {
    super.reset();
  }

}
