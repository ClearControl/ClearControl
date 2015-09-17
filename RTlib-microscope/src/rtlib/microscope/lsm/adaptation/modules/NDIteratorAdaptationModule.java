package rtlib.microscope.lsm.adaptation.modules;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import gnu.trove.list.array.TDoubleArrayList;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.argmax.SmartArgMaxFinder;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.adaptation.utils.NDIterator;
import rtlib.stack.StackInterface;

public abstract class NDIteratorAdaptationModule extends
												AdaptationModuleBase
{

	private int mNumberOfSamples;
	private double mProbabilityThreshold;
	private NDIterator mNDIterator;

	public NDIteratorAdaptationModule(	int pNumberOfSamples,
										double pProbabilityThreshold)
	{
		super();
		setNumberOfSamples(pNumberOfSamples);
		setProbabilityThreshold(pProbabilityThreshold);

	}

	public NDIterator getNDIterator()
	{
		return mNDIterator;
	}

	public void setNDIterator(NDIterator pNDIterator)
	{
		mNDIterator = pNDIterator;
	}

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

		int lNumberOfControlPlanes = lStackAcquisition.getCurrentAcquisitionState()
														.getNumberOfControlPlanes();

		int lNumberOfLighSheets = lLightSheetMicroscope.getDeviceLists()
														.getNumberOfLightSheetDevices();

		setNDIterator(new NDIterator(	lNumberOfControlPlanes,
										lNumberOfLighSheets));

	}

	@Override
	public boolean step()
	{
		if (getNDIterator() == null)
			reset();

		boolean lHasNext = getNDIterator().hasNext();

		if (lHasNext)
		{
			int[] lNext = getNDIterator().next();
			int lControlPlaneIndex = lNext[0];
			int lLightSheetIndex = lNext[1];
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
											LightSheetMicroscope lLSM,
											StackAcquisitionInterface lStackAcquisition,
											final TDoubleArrayList lDOFValueList)
	{
		try
		{
			final Boolean lPlayQueueAndWait = lLSM.playQueueAndWaitForStacks(	lLSM.getQueueLength(),
																				TimeUnit.SECONDS);

			if (lPlayQueueAndWait)
			{
				Runnable lRunnable = () -> {
					DCTS2D lDCTS2D = new DCTS2D();

					int lBestDetectionArmSeletion = lStackAcquisition.getBestDetectioArm(pControlPlaneIndex);

					final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = lLSM.getStackVariable(lBestDetectionArmSeletion)
																										.get();

					OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

					final double[] lMetricArray = lDCTS2D.computeImageQualityMetric(lImage);

					SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();

					Double lArgmax = lSmartArgMaxFinder.argmax(	lDOFValueList.toArray(),
																lMetricArray);

					if (lArgmax != null && !Double.isNaN(lArgmax))
					{
						double lFitProbability = lSmartArgMaxFinder.getLastFitProbability();

						if (lFitProbability > getProbabilityThreshold())
						{
							updateNewState(	pControlPlaneIndex,
											pLightSheetIndex,
											lArgmax);
						}
					}

				};

				Future<?> lFuture = executeAsynchronously(lRunnable);

				return lFuture;
			}
		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public abstract void updateNewState(int pControlPlaneIndex,
										int pLightSheetIndex,
										Double pArgmax);

	@Override
	public boolean isReady()
	{
		return !getNDIterator().hasNext() && super.isReady();
	}

}
