package rtlib.microscope.lsm.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.apache.commons.collections4.map.MultiKeyMap;

import rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import rtlib.core.math.argmax.Fitting1D;
import rtlib.core.math.argmax.methods.ModeArgMaxFinder;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.math.regression.linear.TheilSenEstimator;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.calibrator.utils.ImageAnalysisUtils;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.stack.StackInterface;

public class CalibrationZ
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private ArgMaxFinder1DInterface mArgMaxFinder;
	private MultiPlot mMultiPlotZFocusCurves, mMultiPlotZModels;
	private MultiKeyMap<Integer, UnivariateAffineFunction> mModels;
	private int mNumberOfDetectionArmDevices;
	private int mIteration;
	private int mNumberOfLightSheetDevices;

	@SuppressWarnings("unchecked")
	public CalibrationZ(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mMultiPlotZFocusCurves = MultiPlot.getMultiPlot(this.getClass()
																												.getSimpleName() + " calibration: focus curves");
		mMultiPlotZFocusCurves.setVisible(false);

		mMultiPlotZModels = MultiPlot.getMultiPlot(this.getClass()
																										.getSimpleName() + " calibration: models");
		mMultiPlotZModels.setVisible(false);

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																												.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfLightSheetDevices();

		mModels = new MultiKeyMap<>();
	}

	public boolean calibrate(	int pLightSheetIndex,
														int pNumberOfDSamples,
														int pNumberOfISamples)
	{
		mArgMaxFinder = new ModeArgMaxFinder();

		mMultiPlotZFocusCurves.clear();
		mMultiPlotZFocusCurves.setVisible(true);

		mIteration++;
		mMultiPlotZModels.setVisible(true);

		final TheilSenEstimator[] lTheilSenEstimators = new TheilSenEstimator[mNumberOfDetectionArmDevices];
		final PlotTab[] lPlots = new PlotTab[mNumberOfDetectionArmDevices];
		for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
		{
			lTheilSenEstimators[i] = new TheilSenEstimator();

			lPlots[i] = mMultiPlotZModels.getPlot(String.format("iter=%d, D=%d, I=%d",
																													mIteration,
																													i,
																													pLightSheetIndex));

			lPlots[i].setScatterPlot("D" + i);
			lPlots[i].setLinePlot("fit D" + i);
		}

		LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																	.getLightSheetDevice(pLightSheetIndex);

		double lMinIZ = lLightSheetDevice.getZFunction().get().getMin();
		double lMaxIZ = lLightSheetDevice.getZFunction().get().getMax();

		double lStepIZ = (lMaxIZ - lMinIZ) / (pNumberOfISamples - 1);

		for (double iz = lMinIZ; iz <= lMaxIZ; iz += lStepIZ)
		{
			final double[] dz = focusZ(	pLightSheetIndex,
																	pNumberOfDSamples,
																	iz);

			if (dz == null)
				return false;

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				if (!Double.isNaN(dz[i]))
				{
					lTheilSenEstimators[i].enter(dz[i], iz);
					lPlots[i].addPoint("D" + i, dz[i], iz);
				}

			if (ScriptingEngine.isCancelRequestedStatic())
				return false;
		}

		for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
		{
			final UnivariateAffineFunction lModel = lTheilSenEstimators[d].getModel();

			System.out.println(lModel);

			mModels.put(pLightSheetIndex,
									d,
									lTheilSenEstimators[d].getModel());

			UnivariateAffineComposableFunction lDetectionFocusZFunction = mLightSheetMicroscope.getDeviceLists()
																																													.getDetectionArmDevice(d)
																																													.getZFunction()
																																													.get();

			double lMinDZ = lDetectionFocusZFunction.getMin();
			double lMaxDZ = lDetectionFocusZFunction.getMax();
			double lStepDZ = (lMaxDZ - lMinDZ) / 1000;

			for (double z = lMinDZ; z <= lMaxDZ; z += lStepDZ)
			{
				lPlots[d].addPoint(	"fit D" + d,
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
				UnivariateAffineComposableFunction lDetectionFocusZFunction = mLightSheetMicroscope.getDeviceLists()
																																														.getDetectionArmDevice(d)
																																														.getZFunction()
																																														.get();

				lMinDZ = max(lMinDZ, lDetectionFocusZFunction.getMin());
				lMaxDZ = min(lMaxDZ, lDetectionFocusZFunction.getMax());
			}

			double lStep = (lMaxDZ - lMinDZ) / (pNumberOfDSamples - 1);

			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			mLightSheetMicroscope.setI(pLightSheetIndex);
			mLightSheetMicroscope.setIX(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIY(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);

			final double[] dz = new double[mNumberOfDetectionArmDevices];

			final TDoubleArrayList lDZList = new TDoubleArrayList();

			for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
			{
				mLightSheetMicroscope.setIZ(pLightSheetIndex, lMinDZ);
				mLightSheetMicroscope.setDZ(d, lMinDZ);
				mLightSheetMicroscope.setC(d, false);
			}
			mLightSheetMicroscope.addCurrentStateToQueue();

			for (double z = lMinDZ; z <= lMaxDZ; z += lStep)
			{
				lDZList.add(z);

				for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
				{
					mLightSheetMicroscope.setDZ(d, z);
					mLightSheetMicroscope.setC(d, true);
				}

				mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
				mLightSheetMicroscope.setIZ(pLightSheetIndex, pIZ);

				mLightSheetMicroscope.addCurrentStateToQueue();
			}

			for (int d = 0; d < mNumberOfDetectionArmDevices; d++)
			{
				mLightSheetMicroscope.setDZ(d, lMinDZ);
				mLightSheetMicroscope.setC(d, false);
			}
			mLightSheetMicroscope.addCurrentStateToQueue();

			mLightSheetMicroscope.finalizeQueue();

			/*ScoreVisualizerJFrame.visualize("queuedscore",
																			mLightSheetMicroscope.getDeviceLists()
																														.getSignalGeneratorDevice(0)
																														.getQueuedScore());/**/

			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(mLightSheetMicroscope.getQueueLength(),
																																												TimeUnit.SECONDS);

			if (lPlayQueueAndWait)
				for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				{
					final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(i)
																																																							.get();

					OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

					// final double[] lDCTSArray =
					// mDCTS2D.computeImageQualityMetric(lImage);
					final double[] lMetricArray = ImageAnalysisUtils.computeAveragePowerIntensityPerPlane(lImage);

					PlotTab lPlot = mMultiPlotZFocusCurves.getPlot(String.format(	"D=%d, I=%d, Iz=%g",
																																				i,
																																				pLightSheetIndex,
																																				pIZ));
					lPlot.setScatterPlot("samples");

					// System.out.format("metric array: \n");
					for (int j = 0; j < lMetricArray.length; j++)
					{
						lPlot.addPoint("samples", lDZList.get(j), lMetricArray[j]);
						/*System.out.format(	"%d,%d\t%g\t%g\n",
											i,
											j,
											lDZList.get(j),
											lMetricArray[j]);/**/
					}
					lPlot.ensureUpToDate();

					final Double lArgMax = mArgMaxFinder.argmax(lDZList.toArray(),
																											lMetricArray);

					if (lArgMax != null)
					{
						TDoubleArrayList lDCTSList = new TDoubleArrayList(lMetricArray);

						double lAmplitudeRatio = (lDCTSList.max() - lDCTSList.min()) / lDCTSList.max();

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

							double[] lFit = lFitting1D.fit(	lDZList.toArray(),
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

			final double b0 = mModels.get(pLightSheetIndex, 0)
																.getConstant();
			final double a0 = mModels.get(pLightSheetIndex, 0).getSlope();

			final double b1 = mModels.get(pLightSheetIndex, 1)
																.getConstant();
			final double a1 = mModels.get(pLightSheetIndex, 1).getSlope();

			final LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																					.getLightSheetDevice(pLightSheetIndex);

			double lSlope = 0.5 * (a0 + a1);
			double lOffset = 0.5 * (b0 + b1);

			System.out.println("before: getZFunction()=" + lLightSheetDevice.getZFunction());

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
			System.out.println("after: getZFunction()=" + lLightSheetDevice.getZFunction());

			System.out.println("before: getYFunction()=" + lLightSheetDevice.getYFunction());
			lLightSheetDevice.getYFunction()
												.set(UnivariateAffineFunction.axplusb(lLightSheetDevice.getZFunction()
																																								.get()
																																								.getSlope(),
																																			0));
			lLightSheetDevice.getYFunction()
												.get()
												.setMin(lLightSheetDevice.getZFunction()
																									.get()
																									.getMin());
			lLightSheetDevice.getYFunction()
												.get()
												.setMax(lLightSheetDevice.getZFunction()
																									.get()
																									.getMax());
			System.out.println("after: getYFunction()=" + lLightSheetDevice.getYFunction());

			if (pAdjustDetectionZ)
			{

				final double lFocalPlanesHalfOffset = 0.5 * ((-b0 / a0) - (-b1 / a1));

				System.out.println("lFocalPlanesHalfOffset=" + lFocalPlanesHalfOffset);

				final DetectionArmInterface lDetectionArmDevice1 = mLightSheetMicroscope.getDeviceLists()
																																								.getDetectionArmDevice(0);
				final DetectionArmInterface lDetectionArmDevice2 = mLightSheetMicroscope.getDeviceLists()
																																								.getDetectionArmDevice(1);

				lDetectionArmDevice1.getZFunction()
														.get()
														.composeWith(new UnivariateAffineFunction(1,
																																			-lFocalPlanesHalfOffset));
				lDetectionArmDevice2.getZFunction()
														.get()
														.composeWith(new UnivariateAffineFunction(1,
																																			lFocalPlanesHalfOffset));

				lDetectionArmDevice1.getZFunction().setCurrent();
				lDetectionArmDevice2.getZFunction().setCurrent();

				System.out.println("lDetectionArmDevice1.getDetectionFocusZFunction()=" + lDetectionArmDevice1.getZFunction());
				System.out.println("lDetectionArmDevice2.getDetectionFocusZFunction()=" + lDetectionArmDevice2.getZFunction());
			}

			double lError = (abs(1 - lSlope) + abs(lOffset));

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
