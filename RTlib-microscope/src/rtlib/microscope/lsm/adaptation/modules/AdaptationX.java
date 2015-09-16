package rtlib.microscope.lsm.adaptation.modules;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.MultiKeyMap;

import gnu.trove.list.array.TDoubleArrayList;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.argmax.SmartArgMaxFinder;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.lightsheet.LightSheetInterface;
import rtlib.stack.StackInterface;

public class AdaptationX extends AdaptationModuleBase	implements
														AdaptationModuleInterface
{

	private MultiKeyMap<Integer, Double> mBestXFound = new MultiKeyMap<>();
	private int mNumberOfSamples;
	private double mProbabilityThreshold;

	public AdaptationX(	int pNumberOfSamples,
						double pProbabilityThreshold)
	{
		super();
		mNumberOfSamples = pNumberOfSamples;
		mProbabilityThreshold = pProbabilityThreshold;
	}

	@Override
	public boolean step()
	{
		LightSheetMicroscope lLightSheetMicroscope = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		int lNumberOfControlPlanes = lStackAcquisition.getCurrentAcquisitionState()
														.getNumberOfControlPlanes();

		int lNumberOfLighSheets = lLightSheetMicroscope.getDeviceLists()
														.getNumberOfLightSheetDevices();

		// TODO:
		Future<?> lFuture = null; // atomicStep(pi, lsi, mNumberOfSamples);

		mListOfFuturTasks.add(lFuture);

		return false;
	}

	private Future<?> atomicStep(	int pControlPlaneIndex,
									int pLightSheetIndex,
									int pNumberOfSamples)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		StackAcquisitionInterface lStackAcquisition = getAdaptator().getStackAcquisition();

		LightSheetInterface lLightSheetDevice = lLSM.getDeviceLists()
													.getLightSheetDevice(pLightSheetIndex);
		double lMinX = lLightSheetDevice.getXFunction()
										.get()
										.getMin();
		double lMaxX = lLightSheetDevice.getXFunction()
										.get()
										.getMax();
		double lStepX = (lMaxX - lMinX) / pNumberOfSamples;

		lLSM.clearQueue();

		lStackAcquisition.setToControlPlane(pControlPlaneIndex);

		final TDoubleArrayList lIXList = new TDoubleArrayList();

		lLSM.setC(false);
		lLSM.setIX(pLightSheetIndex, lMinX);
		lLSM.addCurrentStateToQueue();

		for (double x = lMinX; x <= lMaxX; x += lStepX)
		{
			lLSM.setC(false);

			lIXList.add(x);
			lLSM.setIX(pLightSheetIndex, x);

			lLSM.addCurrentStateToQueue();
		}

		lLSM.finalizeQueue();

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

					Double lArgmaxX = lSmartArgMaxFinder.argmax(lIXList.toArray(),
																lMetricArray);

					if (lArgmaxX != null && !Double.isNaN(lArgmaxX))
					{
						double lFitProbability = lSmartArgMaxFinder.getLastFitProbability();

						if (lFitProbability > mProbabilityThreshold)
						{
							getAdaptator().getNewAcquisitionState()
											.setAtControlPlaneIX(	pControlPlaneIndex,
																	pLightSheetIndex,
																	lArgmaxX);
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

}
