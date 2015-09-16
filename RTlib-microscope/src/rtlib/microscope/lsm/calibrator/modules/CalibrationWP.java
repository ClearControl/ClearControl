package rtlib.microscope.lsm.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.StatUtils;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import rtlib.core.math.argmax.Fitting1D;
import rtlib.core.math.argmax.SmartArgMaxFinder;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.calibrator.utils.ImageAnalysisUtils;
import rtlib.microscope.lsm.lightsheet.LightSheetInterface;
import rtlib.stack.StackInterface;

public class CalibrationWP
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private MultiPlot mMultiPlotAdjustPCurves, mMultiPlotHPPCurves;
	private MultiKeyMap<Integer, UnivariateFunction> mWPFunctions;
	private int mNumberOfDetectionArmDevices;

	public CalibrationWP(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mMultiPlotAdjustPCurves = MultiPlot.getMultiPlot(this.getClass()
																.getSimpleName() + " calibration: adjust power curves");

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfDetectionArmDevices();

		mWPFunctions = new MultiKeyMap<>();
	}

	public void calibrate(	int pLightSheetIndex,
							int pDetectionArmIndex,
							int pNumberOfSamplesW,
							int pNumberOfSamplesP)
	{

		mMultiPlotAdjustPCurves.clear();
		mMultiPlotAdjustPCurves.setVisible(true);

		LightSheetInterface lLightSheet = mLightSheetMicroscope.getDeviceLists()
																.getLightSheetDevice(pLightSheetIndex);

		UnivariateAffineComposableFunction lWidthFunction = lLightSheet.getWidthFunction()
																		.get();
		UnivariateAffineComposableFunction lPowerFunction = lLightSheet.getWidthFunction()
																		.get();

		double lMinP = lPowerFunction.getMin();
		double lMaxP = lPowerFunction.getMax();
		double lReferencePower = (lMaxP - lMinP) / 2;

		double lMinW = lWidthFunction.getMin();
		double lMaxW = lWidthFunction.getMax();
		double lStepW = (lMaxW - lMinW) / pNumberOfSamplesW;
		double lReferenceW = (lMaxW - lMinW) / 2;

		final double lReferenceIntensity = adjustP(	pLightSheetIndex,
													pDetectionArmIndex,
													lReferencePower,
													lReferencePower,
													pNumberOfSamplesP,
													lReferenceW,
													0,
													true);

		final WeightedObservedPoints lObservations = new WeightedObservedPoints();
		TDoubleArrayList lWList = new TDoubleArrayList();
		TDoubleArrayList lPRList = new TDoubleArrayList();
		
		for (double w = lMinW; w <= lMaxW; w += lStepW)
		{
			final double lPower = adjustP(	pLightSheetIndex,
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

		final PolynomialCurveFitter lPolynomialCurveFitter = PolynomialCurveFitter.create(3);

		final double[] lCoeficients = lPolynomialCurveFitter.fit(lObservations.toList());

		PolynomialFunction lPowerRatioFunction = new PolynomialFunction(lCoeficients);

		mWPFunctions.put(	pLightSheetIndex,
							pDetectionArmIndex,
							lPowerRatioFunction);

		PlotTab lPlot = mMultiPlotHPPCurves.getPlot(String.format(	" D=%d, I=%d, W=%g",
																	pDetectionArmIndex,
																	pLightSheetIndex));

		lPlot.setScatterPlot("samples");
		lPlot.setScatterPlot("fit");
		List<WeightedObservedPoint> lObservationsList = lObservations.toList();

		for (int j = 0; j < lWList.size(); j++)
		{
			lPlot.addPoint("samples", lWList.get(j), lPRList.get(j));
			lPlot.addPoint(	"fit",
							lWList.get(j),
							lObservationsList.get(j).getY());

		}
		lPlot.ensureUpToDate();

	}

	private Double adjustP(	int pLightSheetIndex,
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
			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			mLightSheetMicroscope.selectI(pLightSheetIndex);
			mLightSheetMicroscope.setIW(pLightSheetIndex, pW);

			final TDoubleArrayList lPList = new TDoubleArrayList();

			mLightSheetMicroscope.setIP(pLightSheetIndex, pMinP);

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
			{
				mLightSheetMicroscope.setDZ(i, 0);
				mLightSheetMicroscope.setC(i, false);
			}
			mLightSheetMicroscope.addCurrentStateToQueue();

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				mLightSheetMicroscope.setC(i, true);

			double lStep = (pMaxP - pMinP) / pNumberOfSamples;

			for (double p = pMinP, i = 0; p <= pMaxP && i < pNumberOfSamples; p += lStep, i++)
			{
				lPList.add(p);

				mLightSheetMicroscope.setIP(pLightSheetIndex, p);
				mLightSheetMicroscope.addCurrentStateToQueue();
			}

			mLightSheetMicroscope.setIP(pLightSheetIndex, pMinP);
			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
			{
				mLightSheetMicroscope.setDZ(i, 0);
				mLightSheetMicroscope.setC(i, false);
			}
			mLightSheetMicroscope.addCurrentStateToQueue();

			mLightSheetMicroscope.finalizeQueue();

			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(	mLightSheetMicroscope.getQueueLength(),
																								TimeUnit.SECONDS);

			if (lPlayQueueAndWait)
			{
				final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(pDetectionArmIndex)
																													.get();

				OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

				// final double[] lDCTSArray =
				// mDCTS2D.computeImageQualityMetric(lImage);
				final double[] lAvgIntensityArray = ImageAnalysisUtils.computeAveragePowerIntensityPerPlane(lImage);

				smooth(lAvgIntensityArray, 1);

				PlotTab lPlot = mMultiPlotAdjustPCurves.getPlot(String.format(	"Mode=%s, D=%d, I=%d, W=%g",
																				pReturnIntensity ? "ret_int"
																								: "ret_pow",
																				pDetectionArmIndex,
																				pLightSheetIndex,
																				pW));
				lPlot.setScatterPlot("samples");

				// System.out.format("metric array: \n");
				for (int j = 0; j < lAvgIntensityArray.length; j++)
				{
					lPlot.addPoint(	"samples",
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
					double lAvgIntensity = StatUtils.percentile(lAvgIntensityArray,
																50);
					return lAvgIntensity;
				}
				else
				{
					int lIndex = find(	lAvgIntensityArray,
										pTargetIntensity);

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
				pMetricArray[i] = (pMetricArray[i - 1] + pMetricArray[i] + pMetricArray[i + 1]) / 3;
			}

			for (int i = pMetricArray.length - 2; i >= 1; i--)
			{
				pMetricArray[i] = (pMetricArray[i - 1] + pMetricArray[i] + pMetricArray[i + 1]) / 3;
			}
		}

	}

	public double apply(int pLightSheetIndex, int pDetectionArmIndex)
	{
		System.out.println("LightSheet index: " + pLightSheetIndex);

		LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																		.getLightSheetDevice(pLightSheetIndex);

		UnivariateFunction lNewWidthPowerFunction = mWPFunctions.get(	pLightSheetIndex,
																		pDetectionArmIndex);

		ObjectVariable<UnivariateFunction> lFunctionVariable = lLightSheetDevice.getWidthPowerFunction();

		System.out.format(	"Current WidthPower function: %s \n",
							lFunctionVariable.get());

		lFunctionVariable.set(lNewWidthPowerFunction);

		System.out.format(	"New WidthPower function: %s \n",
							lFunctionVariable.get());

		double lError = 0;

		return lError;
	}

	public void reset()
	{
		mMultiPlotAdjustPCurves.clear();
		mMultiPlotAdjustPCurves.setVisible(false);
		mWPFunctions.clear();
	}

}
