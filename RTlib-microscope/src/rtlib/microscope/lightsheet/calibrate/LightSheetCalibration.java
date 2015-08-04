package rtlib.microscope.lightsheet.calibrate;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.apache.commons.collections4.map.MultiKeyMap;

import rtlib.core.math.argmax.SmartArgMaxFinder;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.math.regression.linear.TheilSenEstimator;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.stack.StackInterface;

public class LightSheetCalibration
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private final DCTS2D mDCTS2D;
	private final SmartArgMaxFinder mSmartArgMaxFinder;
	private MultiPlot mMultiPlotFocusCurves, mMultiPlotModels;
	private MultiKeyMap<Integer, UnivariateAffineFunction> mModels;

	@SuppressWarnings("unchecked")
	public LightSheetCalibration(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mDCTS2D = new DCTS2D();

		mMultiPlotFocusCurves = MultiPlot.getMultiPlot(this.getClass()
																												.getSimpleName() + " focus curves");

		mMultiPlotModels = MultiPlot.getMultiPlot(this.getClass()
																									.getSimpleName() + " models");

		mSmartArgMaxFinder = new SmartArgMaxFinder();

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		mModels = new MultiKeyMap<>();
	}

	public void calibrate(int pLightSheetIndex,
												double pMinDZ,
												double pMaxDZ,
												double pStepDZ,
												double pMinIZ,
												double pMaxIZ,
												double pStepIZ)
	{
		mMultiPlotFocusCurves.clear();
		mMultiPlotFocusCurves.setVisible(true);

		mMultiPlotModels.clear();
		mMultiPlotModels.setVisible(true);

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			mLightSheetMicroscope.getDeviceLists()
														.getDetectionArmDevice(0)
														.getDetectionFocusZFunction()
														.setReference(new UnivariateAffineFunction());
		}

		final TheilSenEstimator[] lTheilSenEstimators = new TheilSenEstimator[lNumberOfDetectionArmDevices];
		final PlotTab[] lPlots = new PlotTab[lNumberOfDetectionArmDevices];
		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			lTheilSenEstimators[i] = new TheilSenEstimator();

			lPlots[i] = mMultiPlotModels.getPlot(String.format(	"D=%d, I=%d",
																													i,
																													pLightSheetIndex));

			lPlots[i].setScatterPlot("D" + i);
			lPlots[i].setLinePlot("fit D" + i);
		}

		for (double iz = pMinIZ; iz < pMaxIZ; iz += pStepIZ)
		{
			final double[] dz = focus(pLightSheetIndex,
																pMinDZ,
																pMaxDZ,
																pStepDZ,
																iz);
			for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
				if (!Double.isNaN(dz[i]))
				{
					lTheilSenEstimators[i].enter(dz[i], iz);
					lPlots[i].addPoint("D" + i, dz[i], iz);
				}
		}

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
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

	public double[] focus(int pLightSheetIndex,
												double pMinDZ,
												double pMaxDZ,
												double pStep,
												double pIZ)
	{
		try
		{
			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																		.getNumberOfDetectionArmDevices();

			final double[] dz = new double[lNumberOfDetectionArmDevices];

			final TDoubleArrayList lDZList = new TDoubleArrayList();
			for (double z = pMinDZ; z <= pMaxDZ; z += pStep)
			{
				lDZList.add(z);

				for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
				{
					mLightSheetMicroscope.setDZ(i, z);
				}

				mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
				mLightSheetMicroscope.setIZ(pLightSheetIndex, pIZ);

				// TODO: remove, this is for debug purposes:
				// mLightSheetMicroscope.setIZ(0, pIZ);
				// mLightSheetMicroscope.setIZ(1, pIZ);
				// mLightSheetMicroscope.setIZ(2, pIZ);
				// mLightSheetMicroscope.setIZ(3, pIZ);

				mLightSheetMicroscope.addCurrentStateToQueue();
			}
			mLightSheetMicroscope.addCurrentStateToQueueNotCounting();

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

					final double[] lDCTSArray = mDCTS2D.computeImageQualityMetric(lImage);

					PlotTab lPlot = mMultiPlotFocusCurves.getPlot(String.format("D=%d, I=%d, Iz=%g",
																																			i,
																																			pLightSheetIndex,
																																			pIZ));
					lPlot.setScatterPlot("samples");

					System.out.format("dcts array: \n");
					for (int j = 0; j < lDCTSArray.length; j++)
					{
						lPlot.addPoint("samples", lDZList.get(j), lDCTSArray[j]);
						System.out.format("%d,%d\t%g\t%g\n",
															i,
															j,
															lDZList.get(j),
															lDCTSArray[j]);
					}
					lPlot.ensureUpToDate();

					final Double lArgMax = mSmartArgMaxFinder.argmax(	lDZList.toArray(),
																														lDCTSArray);

					if (lArgMax != null)
					{
						Double lLastFitProbability = mSmartArgMaxFinder.getLastFitProbability();

						System.out.format("argmax=%s fitprob=%s \n",
															lArgMax.toString(),
															lLastFitProbability);

						lPlot.setScatterPlot("argmax");
						lPlot.addPoint("argmax", lArgMax, 0);

						if (lLastFitProbability != null && lLastFitProbability > 0.9)
							dz[i] = lArgMax;
						else
							dz[i] = Double.NaN;
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
		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			final DetectionArmInterface lDetectionArmDevice = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(i);
			lDetectionArmDevice.getDetectionFocusZFunction()
													.set(new UnivariateAffineFunction(1, 0));

		}

		final int lNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																																.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfLightSheetDevices; i++)
		{
			final LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																					.getLightSheetDevice(i);

			lLightSheetDevice.getLightSheetZFunction()
												.set(new UnivariateAffineFunction(1, 0));

		}

	}

	@SuppressWarnings("unchecked")
	public void apply(int pLightSheetIndex)
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

			final double lFocalPlanesHalfOffset = 0.5 * ((-b0 / a0) - (-b1 / a1));

			System.out.println("lFocalPlanesHalfOffset=" + lFocalPlanesHalfOffset);

			final DetectionArmInterface lDetectionArmDevice1 = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(0);
			final DetectionArmInterface lDetectionArmDevice2 = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(1);

			lDetectionArmDevice1.getDetectionFocusZFunction()
													.get()
													.composeWith(new UnivariateAffineFunction(1,
																																		lFocalPlanesHalfOffset));
			lDetectionArmDevice2.getDetectionFocusZFunction()
													.get()
													.composeWith(new UnivariateAffineFunction(1,
																																		-lFocalPlanesHalfOffset));

			System.out.println("lDetectionArmDevice1.getDetectionFocusZFunction()=" + lDetectionArmDevice1.getDetectionFocusZFunction());
			System.out.println("lDetectionArmDevice2.getDetectionFocusZFunction()=" + lDetectionArmDevice2.getDetectionFocusZFunction());

			final LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																					.getLightSheetDevice(pLightSheetIndex);

			double lSlope = 0.5 * (a0 + a1);
			double lOffset = 0.5 * (b0 + b1);

			lLightSheetDevice.getLightSheetZFunction()
												.get()
												.composeWith(new UnivariateAffineFunction(lSlope,
																													lOffset));

			System.out.println("lLightSheetDevice.getLightSheetZFunction()=" + lLightSheetDevice.getLightSheetZFunction());

		}

	}
}
