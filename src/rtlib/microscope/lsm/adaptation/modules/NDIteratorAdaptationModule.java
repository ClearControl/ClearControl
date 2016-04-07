package rtlib.microscope.lsm.adaptation.modules;

import gnu.trove.list.array.TDoubleArrayList;
import ij.ImageJ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import rtlib.core.math.argmax.FitProbabilityInterface;
import rtlib.core.math.argmax.methods.ModeArgMaxFinder;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.adaptation.utils.NDIterator;
import rtlib.stack.EmptyStack;
import rtlib.stack.StackInterface;

public abstract class NDIteratorAdaptationModule extends
																								AdaptationModuleBase
{

	private int mNumberOfSamples;
	private double mProbabilityThreshold;
	private NDIterator mNDIterator;
	protected MultiPlot mMultiPlotZFocusCurves;
	private ImageJ mImageJ;

	public NDIteratorAdaptationModule(int pNumberOfSamples,
																		double pProbabilityThreshold)
	{
		super();
		setNumberOfSamples(pNumberOfSamples);
		setProbabilityThreshold(pProbabilityThreshold);

		mMultiPlotZFocusCurves = MultiPlot.getMultiPlot(this.getClass()
																												.getSimpleName() + " calibration: focus curves");
		mMultiPlotZFocusCurves.setVisible(true);

	}

	public NDIterator getNDIterator()
	{
		return mNDIterator;
	}

	public void setNDIterator(NDIterator pNDIterator)
	{
		mNDIterator = pNDIterator;
	}

	@Override
	public int getNumberOfSteps()
	{
		return mNDIterator.getNumberOfIterations();
	};

	public double getProbabilityThreshold()
	{
		return mProbabilityThreshold;
	}

	public void setProbabilityThreshold(double pProbabilityThreshold)
	{
		mProbabilityThreshold = pProbabilityThreshold;
	}

	public int getNumberOfSamples()
	{
		return mNumberOfSamples;
	}

	public void setNumberOfSamples(int pNumberOfSamples)
	{
		mNumberOfSamples = pNumberOfSamples;
	}

	@Override
	public void reset()
	{
		super.reset();

		LightSheetMicroscope lLightSheetMicroscope = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		int lNumberOfControlPlanes = lStackAcquisition.getCurrentState()
																									.getNumberOfControlPlanes();

		int lNumberOfLighSheets = lLightSheetMicroscope.getDeviceLists()
																										.getNumberOfLightSheetDevices();

		setNDIterator(new NDIterator(	lNumberOfControlPlanes,
																	lNumberOfLighSheets));

	}

	@Override
	public Boolean apply(Void pVoid)
	{
		System.out.format("NDIteratorAdaptationModule step \n");

		boolean lHasNext = getNDIterator().hasNext();

		System.out.format("lHasNext: %s \n", lHasNext);

		if (lHasNext)
		{
			int[] lNext = getNDIterator().next();

			System.out.format("lNext: %s \n", Arrays.toString(lNext));

			int lControlPlaneIndex = lNext[0];
			int lLightSheetIndex = lNext[1];

			System.out.format("controlplane: %d, lighsheetindex: %d \n",
												lControlPlaneIndex,
												lLightSheetIndex);

			Future<?> lFuture = atomicStep(	lControlPlaneIndex,
																			lLightSheetIndex,
																			getNumberOfSamples());

			mListOfFuturTasks.add(lFuture);

		}

		return getNDIterator().hasNext();
	}

	public abstract Future<?> atomicStep(	int pControlPlaneIndex,
																				int pLightSheetIndex,
																				int pNumberOfSamples);

	protected Future<?> findBestDOFValue(	int pControlPlaneIndex,
																				int pLightSheetIndex,
																				LightSheetMicroscope pLSM,
																				StackAcquisitionInterface lStackAcquisition,
																				final TDoubleArrayList lDOFValueList)
	{

		try
		{
			pLSM.useRecycler("adaptation", 1, 4, 4);
			final Boolean lPlayQueueAndWait = pLSM.playQueueAndWaitForStacks(	10 + pLSM.getQueueLength(),
																																				TimeUnit.SECONDS);

			if (!lPlayQueueAndWait)
				return null;

			final int lNumberOfDetectionArmDevices = pLSM.getDeviceLists()
																										.getNumberOfDetectionArmDevices();

			@SuppressWarnings("unchecked")
			ArrayList<StackInterface> lStacks = new ArrayList<>();
			for (int d = 0; d < lNumberOfDetectionArmDevices; d++)
				if (isRelevantDetectionArm(pControlPlaneIndex, d))
				{
					final StackInterface lStackInterface = pLSM.getStackVariable(d)
																											.get();
					lStacks.add(lStackInterface.duplicate());

				}
				else
					lStacks.add(new EmptyStack());

			Runnable lRunnable = () -> {

				try
				{
					ArgMaxFinder1DInterface lSmartArgMaxFinder = new ModeArgMaxFinder();

					ArrayList<Double> lArgMaxList = new ArrayList<Double>();

					for (int d = 0; d < lNumberOfDetectionArmDevices; d++)

					{
						if (!isRelevantDetectionArm(pControlPlaneIndex, d))
						{
							lArgMaxList.add(Double.NaN);
							continue;
						}

						final double[] lMetricArray = computeMetric(pControlPlaneIndex,
																												pLightSheetIndex,
																												d,
																												lDOFValueList,
																												lStacks.get(d));

						Double lArgmax = lSmartArgMaxFinder.argmax(	lDOFValueList.toArray(),
																												lMetricArray);

						System.out.println("lArgmax = " + lArgmax);

						if (lArgmax != null && !Double.isNaN(lArgmax))
						{
							if (lSmartArgMaxFinder instanceof FitProbabilityInterface)
							{
								double lFitProbability = ((FitProbabilityInterface) lSmartArgMaxFinder).getLastFitProbability();

								if (lFitProbability > getProbabilityThreshold())
									lArgMaxList.add(lArgmax);
								else
									lArgMaxList.add(Double.NaN);
							}
							else
							{
								lArgMaxList.add(lArgmax);
							}

						}
						else
							lArgMaxList.add(Double.NaN);

					}

					System.out.println("lArgMaxList=" + lArgMaxList.toString());

					for (StackInterface lStack : lStacks)
						lStack.free();

					updateNewState(	pControlPlaneIndex,
													pLightSheetIndex,
													lArgMaxList);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}

			};

			Future<?> lFuture = getAdaptator().executeAsynchronously(lRunnable);

			// FORCE SYNC:
			if (!getAdaptator().isConcurrentExecution())
			{
				try
				{
					lFuture.get();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}

			return lFuture;
		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean isRelevantDetectionArm(int pControlPlaneIndex,
																				int pDetectionArmIndex)
	{
		int lBestDetectionArm = getAdaptator().getStackAcquisition()
																					.getBestDetectionArm(pControlPlaneIndex);
		return (lBestDetectionArm == pDetectionArmIndex);
	};

	protected double[] computeMetric(	int pControlPlaneIndex,
																		int pLightSheetIndex,
																		int pDetectionArmIndex,
																		final TDoubleArrayList lDOFValueList,
																		StackInterface lDuplicatedStack)
	{

		DCTS2D lDCTS2D = new DCTS2D();

		OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lDuplicatedStack.getImage();

		if (lDuplicatedStack.isFree() || lImage.isFree())
		{
			System.err.println("Image freed!!");
			return null;
		}

		System.out.format("computing DCTS on %s ...\n", lImage);
		final double[] lMetricArray = lDCTS2D.computeImageQualityMetric(lImage);
		lDuplicatedStack.free();

		if (isRelevantDetectionArm(pControlPlaneIndex, pDetectionArmIndex))
		{
			PlotTab lPlot = mMultiPlotZFocusCurves.getPlot(String.format(	"LS=%d, D=%d CPI=%d",
																																		pLightSheetIndex,
																																		pDetectionArmIndex,
																																		pControlPlaneIndex));
			lPlot.clearPoints();
			lPlot.setScatterPlot("samples");

			for (int i = 0; i < lMetricArray.length; i++)
			{
				System.out.format("%g\t%g \n",
													lDOFValueList.get(i),
													lMetricArray[i]);
				lPlot.addPoint(	"samples",
												lDOFValueList.get(i),
												lMetricArray[i]);
			}
			lPlot.ensureUpToDate();
		}

		return lMetricArray;
	}

	public void updateNewState(	int pControlPlaneIndex,
															int pLightSheetIndex,
															ArrayList<Double> pArgMaxList)
	{
	}

	@Override
	public boolean isReady()
	{
		return !getNDIterator().hasNext() && super.isReady();
	}

}
