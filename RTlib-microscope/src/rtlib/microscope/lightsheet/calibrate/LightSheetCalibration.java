package rtlib.microscope.lightsheet.calibrate;

import static java.lang.Math.abs;
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
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.math.regression.linear.TheilSenEstimator;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.fragmented.FragmentedMemoryInterface;

public class LightSheetCalibration
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private final DCTS2D mDCTS2D;
	private ArgMaxFinder1DInterface mArgMaxFinder;
	private MultiPlot mMultiPlotZFocusCurves, mMultiPlotZModels,
			mMultiPlotIntensityCurves;
	private MultiKeyMap<Integer, UnivariateAffineFunction> mModels;
	private int mNumberOfDetectionArmDevices;
	private int mIteration;
	private int mNumberOfLightSheetDevices;

	@SuppressWarnings("unchecked")
	public LightSheetCalibration(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mDCTS2D = new DCTS2D();

		mMultiPlotZFocusCurves = MultiPlot.getMultiPlot(this.getClass()
																												.getSimpleName() + " focus curves");

		mMultiPlotZModels = MultiPlot.getMultiPlot(this.getClass()
																									.getSimpleName() + " models");

		mMultiPlotIntensityCurves = MultiPlot.getMultiPlot(this.getClass()
																														.getSimpleName() + "power versus avg intensity");

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																												.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfLightSheetDevices();

		mModels = new MultiKeyMap<>();
	}


	public void calibrateLaserPower(int pLightSheetIndex,
																	double pMin,
																	double pMax,
																	double pStep)	throws InterruptedException,
																								ExecutionException,
																								TimeoutException
	{
		mLightSheetMicroscope.getDeviceLists()
													.getLightSheetSelectorDevice()
													.setPosition(pLightSheetIndex);

		mMultiPlotIntensityCurves.clear();
		mMultiPlotIntensityCurves.setVisible(true);

		mLightSheetMicroscope.clearQueue();
		mLightSheetMicroscope.zero();

		mLightSheetMicroscope.selectI(pLightSheetIndex);

		mLightSheetMicroscope.setIZ(pLightSheetIndex, 0.5);

		for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
		{
			mLightSheetMicroscope.setDZ(i, 0.5);
		}

		mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
		mLightSheetMicroscope.setIW(pLightSheetIndex, 0);

		final TDoubleArrayList lPowerList = new TDoubleArrayList();
		for (double lPower = pMin; lPower <= pMax; lPower += pStep)
		{
			lPowerList.add(lPower);
			mLightSheetMicroscope.setIP(pLightSheetIndex, lPower);
			mLightSheetMicroscope.addCurrentStateToQueue();
		}
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

				final double[] lIntensityArray = computeImageAverageIntensityPerPlane(lImage);

				PlotTab lPlot = mMultiPlotIntensityCurves.getPlot(String.format("D=%d, I=%d",
																																				i,
																																				pLightSheetIndex));
				lPlot.setScatterPlot("samples");

				System.out.format("dcts array: \n");
				for (int j = 0; j < lIntensityArray.length; j++)
				{
					lPlot.addPoint(	"samples",
													lPowerList.get(j),
													lIntensityArray[j]);
					System.out.format("%d,%d\t%g\t%g\n",
														i,
														j,
														lPowerList.get(j),
														lIntensityArray[j]);
				}
				lPlot.ensureUpToDate();

			}

	}



	public void calibrateZ(	int pLightSheetIndex,
													double pMin,
													double pMax,
													double pStep)
	{
		calibrateZ(	pLightSheetIndex,
								pMin,
								pMax,
								pStep,
								pMin,
								pMax,
								(pMax - pMin) / 7);
	}

	public void calibrateZ(	int pLightSheetIndex,
													double pMinDZ,
													double pMaxDZ,
													double pStepDZ,
													double pMinIZ,
													double pMaxIZ,
													double pStepIZ)
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

		for (double iz = pMinIZ; iz < pMaxIZ; iz += pStepIZ)
		{
			final double[] dz = focusZ(	pLightSheetIndex,
																	pMinDZ,
																	pMaxDZ,
																	pStepDZ,
																	iz);

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				if (!Double.isNaN(dz[i]))
				{
					lTheilSenEstimators[i].enter(dz[i], iz);
					lPlots[i].addPoint("D" + i, dz[i], iz);
				}
		}

		for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
		{
			final UnivariateAffineFunction lModel = lTheilSenEstimators[i].getModel();

			System.out.println(lModel);

			mModels.put(pLightSheetIndex,
									i,
									lTheilSenEstimators[i].getModel());

			for (double z = pMinDZ; z <= pMaxDZ; z += 0.01)
			{
				lPlots[i].addPoint(	"fit D" + i,
														z,
														mModels.get(pLightSheetIndex, i).value(z));
			}

			lPlots[i].ensureUpToDate();
		}

	}

	public double[] focusZ(	int pLightSheetIndex,
													double pMinDZ,
													double pMaxDZ,
													double pStep,
													double pIZ)
	{
		try
		{
			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			mLightSheetMicroscope.selectI(pLightSheetIndex);

			final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																		.getNumberOfDetectionArmDevices();

			final double[] dz = new double[lNumberOfDetectionArmDevices];

			final TDoubleArrayList lDZList = new TDoubleArrayList();

			for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
			{
				mLightSheetMicroscope.setIZ(pLightSheetIndex, pMinDZ);
				mLightSheetMicroscope.setDZ(i, pMinDZ);
				mLightSheetMicroscope.setC(i, false);
			}
			mLightSheetMicroscope.addCurrentStateToQueue();

			for (double z = pMinDZ; z <= pMaxDZ; z += pStep)
			{
				lDZList.add(z);

				for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
				{
					mLightSheetMicroscope.setDZ(i, z);
					mLightSheetMicroscope.setC(i, true);
				}

				mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
				mLightSheetMicroscope.setIZ(pLightSheetIndex, pIZ);

				mLightSheetMicroscope.addCurrentStateToQueue();
			}

			for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
			{
				mLightSheetMicroscope.setDZ(i, pMinDZ);
				mLightSheetMicroscope.setC(i, false);
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
				for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
				{
					final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(i)
																																																							.get();

					OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

					// final double[] lDCTSArray =
					// mDCTS2D.computeImageQualityMetric(lImage);
					final double[] lMetricArray = computeSumPowerIntensityPerPlane(lImage);

					PlotTab lPlot = mMultiPlotZFocusCurves.getPlot(String.format(	"D=%d, I=%d, Iz=%g",
																																			i,
																																			pLightSheetIndex,
																																			pIZ));
					lPlot.setScatterPlot("samples");

					System.out.format("metric array: \n");
					for (int j = 0; j < lMetricArray.length; j++)
					{
						lPlot.addPoint("samples", lDZList.get(j), lMetricArray[j]);
						System.out.format("%d,%d\t%g\t%g\n",
															i,
															j,
															lDZList.get(j),
															lMetricArray[j]);
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

	public void reset()
	{
		mMultiPlotZFocusCurves.clear();
		mMultiPlotZFocusCurves.setVisible(true);

		mIteration = 0;
		mMultiPlotZModels.clear();
		mMultiPlotZModels.setVisible(true);

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			final DetectionArmInterface lDetectionArmDevice = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(i);
			lDetectionArmDevice.reset();

		}

		final int lNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																																.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfLightSheetDevices; i++)
		{
			final LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																					.getLightSheetDevice(i);

			lLightSheetDevice.reset();

		}

	}

	@SuppressWarnings("unchecked")
	public double applyZ(int pLightSheetIndex, boolean pAdjustDetectionZ)
	{
		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		if (lNumberOfDetectionArmDevices == 2)
		{
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

			if (abs(lSlope) > 0.01)
			{
				lLightSheetDevice.getLightSheetZFunction()
													.get()
													.composeWith(new UnivariateAffineFunction(lSlope,
																																		lOffset));
				lLightSheetDevice.getLightSheetZFunction().setCurrent();
			}

			System.out.println("lLightSheetDevice.getLightSheetZFunction()=" + lLightSheetDevice.getLightSheetZFunction());

			if (pAdjustDetectionZ)
			{

				final double lFocalPlanesHalfOffset = 0.5 * ((-b0 / a0) - (-b1 / a1));

				System.out.println("lFocalPlanesHalfOffset=" + lFocalPlanesHalfOffset);

				final DetectionArmInterface lDetectionArmDevice1 = mLightSheetMicroscope.getDeviceLists()
																																								.getDetectionArmDevice(0);
				final DetectionArmInterface lDetectionArmDevice2 = mLightSheetMicroscope.getDeviceLists()
																																								.getDetectionArmDevice(1);

				lDetectionArmDevice1.getDetectionFocusZFunction()
														.get()
														.composeWith(new UnivariateAffineFunction(1,
																																			-lFocalPlanesHalfOffset));
				lDetectionArmDevice2.getDetectionFocusZFunction()
														.get()
														.composeWith(new UnivariateAffineFunction(1,
																																			lFocalPlanesHalfOffset));

				lDetectionArmDevice1.getDetectionFocusZFunction()
														.setCurrent();
				lDetectionArmDevice2.getDetectionFocusZFunction()
														.setCurrent();

				System.out.println("lDetectionArmDevice1.getDetectionFocusZFunction()=" + lDetectionArmDevice1.getDetectionFocusZFunction());
				System.out.println("lDetectionArmDevice2.getDetectionFocusZFunction()=" + lDetectionArmDevice2.getDetectionFocusZFunction());
			}

			double lError = abs(1 - lSlope) + abs(lOffset);

			return lError;
		}
		return Double.NaN;

	}

	private static double[] computeImageAverageIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double[] lIntensityArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			double lSum = (double) 0;
			long lCount = 0;

			while (lBuffer.hasRemaining())
			{
				lSum += lBuffer.readChar();
				lCount++;
			}
			lIntensityArray[p] = lSum / lCount;
		}

		return lIntensityArray;
	}

	private static double[] computeSumPowerIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double[] lIntensityArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			double lSumOfPowers = (double) 0;
			long lCount = 0;

			while (lBuffer.hasRemaining())
			{
				float lValue = 1.0f * lBuffer.readChar();
				float lSquareValue = lValue * lValue;
				float lQuarticValue = lSquareValue * lSquareValue;
				float lOcticValue = lQuarticValue * lQuarticValue;
				lSumOfPowers += lOcticValue;
				lCount++;
			}
			lIntensityArray[p] = lSumOfPowers / lCount;
		}

		return lIntensityArray;
	}

	private static double computeImageSumIntensity(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double lSumIntensity = 0;
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			while (lBuffer.hasRemaining())
			{
				lSumIntensity += lBuffer.readChar();
			}
		}

		return lSumIntensity;
	}

}
