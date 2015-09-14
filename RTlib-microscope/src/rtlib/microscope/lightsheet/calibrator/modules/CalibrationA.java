package rtlib.microscope.lightsheet.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.HashMap;
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
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.fragmented.FragmentedMemoryInterface;

public class CalibrationA
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private ArgMaxFinder1DInterface mArgMaxFinder;
	private MultiPlot mMultiPlotZFocusCurves;
	private HashMap<Integer, UnivariateAffineFunction> mModels;
	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;

	public CalibrationA(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mMultiPlotZFocusCurves = MultiPlot.getMultiPlot(this.getClass()
															.getSimpleName() + "A-calibration: focus curves");

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfLightSheetDevices();

		mModels = new HashMap<>();
	}

	public void calibrate(	int pLightSheetIndex,
							int pNumberOfAngles,
							int pNumberOfYPositions)
	{
		mArgMaxFinder = new ModeArgMaxFinder();

		mMultiPlotZFocusCurves.clear();
		mMultiPlotZFocusCurves.setVisible(true);

		LightSheetInterface lLightSheet = mLightSheetMicroscope.getDeviceLists()
																.getLightSheetDevice(pLightSheetIndex);

		double lMinA = lLightSheet.getAlphaFunction().get().getMin();
		double lMaxA = lLightSheet.getAlphaFunction().get().getMax();

		double lMinY = lLightSheet.getYFunction().get().getMin();
		double lMaxY = lLightSheet.getYFunction().get().getMax();

		double lRangeY = min(lMinY, lMaxY);

		double[] angles = new double[mNumberOfDetectionArmDevices];
		int lCount = 0;

		for (double y = 0; y <= lRangeY; y += (lRangeY / pNumberOfYPositions))
		{
			System.out.format(	"Searching for optimal alpha angles for lighsheet at y=+/-%g \n",
								y);

			final double[] anglesM = focusA(pLightSheetIndex,
											lMinA,
											lMaxA,
											(lMaxA - lMinA) / pNumberOfYPositions,
											-y);

			final double[] anglesP = focusA(pLightSheetIndex,
											lMinA,
											lMaxA,
											(lMaxA - lMinA) / pNumberOfYPositions,
											+y);

			System.out.format(	"Optimal alpha angles for lighsheet at y=%g: %s \n",
								-y,
								Arrays.toString(anglesM));
			System.out.format(	"Optimal alpha angles for lighsheet at y=%g: %s \n",
								+y,
								Arrays.toString(anglesP));

			boolean lValid = true;

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				lValid &= !Double.isNaN(anglesM[i]) && !Double.isNaN(anglesM[i]);

			if (lValid)
			{
				System.out.format("Angle values are valid, we proceed... \n");
				for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				{
					angles[i] += 0.5 * anglesM[i] + 0.5 * anglesP[i];
				}

				lCount++;
			}
			else
				System.out.format("Angle are not valid, we continue with next set of y values... \n");
		}

		for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
			angles[i] = angles[i] / lCount;

		System.out.format(	"Averaged alpha angles: %s \n",
							Arrays.toString(angles));

		double angle = 0;
		for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
			angle += angles[i];

		System.out.format(	"Average alpha angle for all detection arms (assumes that the cameras are well aligned): %s \n",
							angle);

		UnivariateAffineFunction lUnivariateAffineFunction = new UnivariateAffineFunction(	1,
																							angle);
		mModels.put(pLightSheetIndex, lUnivariateAffineFunction);

		System.out.format(	"Corresponding model: %s \n",
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

			mLightSheetMicroscope.selectI(pLightSheetIndex);

			final TDoubleArrayList lAList = new TDoubleArrayList();
			double[] angles = new double[mNumberOfDetectionArmDevices];

			mLightSheetMicroscope.setIY(pLightSheetIndex, pY);
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

			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(	mLightSheetMicroscope.getQueueLength(),
																								TimeUnit.SECONDS);

			if (lPlayQueueAndWait)
				for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				{
					final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(i)
																														.get();

					OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

					// final double[] lDCTSArray =
					// mDCTS2D.computeImageQualityMetric(lImage);
					final double[] lMetricArray = ImageAnalysisUtils.computeSumPowerIntensityPerPlane(lImage);

					PlotTab lPlot = mMultiPlotZFocusCurves.getPlot(String.format(	"D=%d, I=%d, IY=%g",
																					i,
																					pLightSheetIndex,
																					pY));
					lPlot.setScatterPlot("samples");

					System.out.format("metric array: \n");
					for (int j = 0; j < lMetricArray.length; j++)
					{
						lPlot.addPoint(	"samples",
										lAList.get(j),
										lMetricArray[j]);
						System.out.format(	"%d,%d\t%g\t%g\n",
											i,
											j,
											lAList.get(j),
											lMetricArray[j]);
					}
					lPlot.ensureUpToDate();

					final Double lArgMax = mArgMaxFinder.argmax(lAList.toArray(),
																lMetricArray);

					if (lArgMax != null)
					{
						TDoubleArrayList lDCTSList = new TDoubleArrayList(lMetricArray);

						double lAmplitudeRatio = (lDCTSList.max() - lDCTSList.min()) / lDCTSList.max();

						System.out.format(	"argmax=%s amplratio=%s \n",
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

							double[] lFit = lFitting1D.fit(	lAList.toArray(),
															new double[lAList.size()]);

							for (int j = 0; j < lAList.size(); j++)
							{
								lPlot.setScatterPlot("fit");
								lPlot.addPoint(	"fit",
												lAList.get(j),
												lFit[j]);
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


	public double apply(int pLightSheetIndex)
	{
		LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																		.getLightSheetDevice(pLightSheetIndex);

		UnivariateAffineFunction lUnivariateAffineFunction = mModels.get(lLightSheetDevice);

		lLightSheetDevice.getAlphaFunction()
							.get()
							.composeWith(lUnivariateAffineFunction);

		double lError = abs(lUnivariateAffineFunction.getSlope() - 1) + abs(lUnivariateAffineFunction.getConstant());

		return lError;
	}
	
	
	public void reset()
	{
		mMultiPlotZFocusCurves.clear();
		mMultiPlotZFocusCurves.setVisible(false);
	}

}
