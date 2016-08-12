package clearcontrol.microscope.lightsheet.autopilot.modules;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import clearcontrol.core.math.argmax.methods.ModeArgMaxFinder;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.ip.iqm.DCTS2D;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.LightSheetAcquisitionStateInterface;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class AdaptationA extends NDIteratorAdaptationModule	implements
																														AdaptationModuleInterface
{

	private double mMaxDefocus;

	public AdaptationA(	double pMaxDefocus,
											int pNumberOfSamples,
											double pProbabilityThreshold)
	{
		super(pNumberOfSamples, pProbabilityThreshold);
		mMaxDefocus = pMaxDefocus;
	}

	@Override
	public Future<?> atomicStep(int pControlPlaneIndex,
															int pLightSheetIndex,
															int pNumberOfSamples)
	{
		LightSheetMicroscope lLSM = getAdaptator().getLightSheetMicroscope();
		LightSheetAcquisitionStateInterface lStackAcquisition = getAdaptator().getStackAcquisitionVariable().get();

		int lBestDetectionArm = getAdaptator().getStackAcquisitionVariable().get()
																					.getBestDetectionArm(pControlPlaneIndex);

		final TDoubleArrayList lDZList = new TDoubleArrayList();

		lLSM.clearQueue();

		lStackAcquisition.applyStateAtControlPlane(pControlPlaneIndex);

		double lCurrentDZ = lLSM.getDZ(lBestDetectionArm);
		double lCurrentH = lLSM.getIH(pLightSheetIndex);
		double lIY = 0.6 * lCurrentH;

		addOneSeqToQueue(	pControlPlaneIndex,
											pLightSheetIndex,
											lLSM,
											lDZList,
											lCurrentDZ,
											lCurrentH,
											-lIY);

		addOneSeqToQueue(	pControlPlaneIndex,
											pLightSheetIndex,
											lLSM,
											lDZList,
											lCurrentDZ,
											lCurrentH,
											lIY);

		lLSM.finalizeQueue();

		return findBestAlphaValue(pControlPlaneIndex,
															pLightSheetIndex,
															lLSM,
															lStackAcquisition,
															lIY,
															lDZList);

	}

	public void addOneSeqToQueue(	int pControlPlaneIndex,
																int pLightSheetIndex,
																LightSheetMicroscope pLSM,
																final TDoubleArrayList pDZList,
																double pCurrentDZ,
																double pCurrentH,
																double pIY)
	{
		int lBestDetectionArm = getAdaptator().getStackAcquisitionVariable().get()
																					.getBestDetectionArm(pControlPlaneIndex);

		double lMinZ = -mMaxDefocus;
		double lMaxZ = +mMaxDefocus;
		double lStepZ = (lMaxZ - lMinZ) / (getNumberOfSamples() - 1);

		pLSM.setIY(pLightSheetIndex, pIY);
		pLSM.setIH(pLightSheetIndex, pCurrentH / 3);
		// pLSM.setIP(pLightSheetIndex, 1.0 / 3);

		pLSM.setDZ(lBestDetectionArm, pCurrentDZ + lMinZ);
		pLSM.setC(false);
		pLSM.setILO(false);
		pLSM.setI(pLightSheetIndex);
		pLSM.addCurrentStateToQueue();
		pLSM.addCurrentStateToQueue();

		pLSM.setC(true);
		for (double z = lMinZ; z <= lMaxZ; z += lStepZ)
		{
			pDZList.add(z);
			pLSM.setDZ(lBestDetectionArm, pCurrentDZ + z);

			pLSM.setILO(true);
			pLSM.setC(true);
			pLSM.setI(pLightSheetIndex);
			pLSM.addCurrentStateToQueue();
		}

		pLSM.setC(false);
		pLSM.setILO(false);
		pLSM.setDZ(lBestDetectionArm, pCurrentDZ);
		pLSM.setI(pLightSheetIndex);
		pLSM.addCurrentStateToQueue();
	}

	protected Future<?> findBestAlphaValue(	int pControlPlaneIndex,
																					int pLightSheetIndex,
																					LightSheetMicroscope pLSM,
																					LightSheetAcquisitionStateInterface lStackAcquisition,
																					double pIY,
																					final TDoubleArrayList lDOFValueList)
	{

		try
		{
			pLSM.useRecycler("adaptation", 1, 4, 4);
			final Boolean lPlayQueueAndWait = pLSM.playQueueAndWaitForStacks(	10 + pLSM.getQueueLength(),
																																				TimeUnit.SECONDS);

			if (!lPlayQueueAndWait)
				return null;

			final int lBestDetectioArm = getAdaptator().getStackAcquisitionVariable().get()
																									.getBestDetectionArm(pControlPlaneIndex);

			final StackInterface lStackInterface = pLSM.getStackVariable(lBestDetectioArm)
																									.get();
			StackInterface lDuplicateStack = lStackInterface.duplicate();

			Runnable lRunnable = () -> {

				try
				{

					final double[] lMetricArray = computeMetricForAlpha(pControlPlaneIndex,
																															pLightSheetIndex,
																															lBestDetectioArm,
																															lDOFValueList,
																															lDuplicateStack);

					lDuplicateStack.free();

					int lLength = lMetricArray.length / 2;

					double[] lAngleAlphaArray = Arrays.copyOfRange(	lDOFValueList.toArray(),
																													0,
																													lLength - 1);

					double[] lArrayN = Arrays.copyOfRange(lMetricArray,
																								0,
																								lLength - 1);
					double[] lArrayP = Arrays.copyOfRange(lMetricArray,
																								lLength,
																								2 * lLength - 1);

					smooth(lArrayN, 6);
					smooth(lArrayP, 6);

					ArgMaxFinder1DInterface lSmartArgMaxFinder = new ModeArgMaxFinder();

					Double lArgmaxN = lSmartArgMaxFinder.argmax(lAngleAlphaArray,
																											lArrayN);

					Double lArgmaxP = lSmartArgMaxFinder.argmax(lAngleAlphaArray,
																											lArrayP);

					System.out.println("lArgmaxN = " + lArgmaxN);
					System.out.println("lArgmaxP = " + lArgmaxP);

					if (lArgmaxN != null && lArgmaxP != null
							&& !Double.isNaN(lArgmaxN)
							&& !Double.isNaN(lArgmaxP))
					{
						double lObservedAngleInRadians = atan((lArgmaxP - lArgmaxN) / (2 * pIY));
						double lObservedAngleInDegrees = toDegrees(lObservedAngleInRadians);

						System.out.println("lArgmaxP - lArgmaxN=" + (lArgmaxP - lArgmaxN));
						System.out.println("2 * pIY * mMicronsPerPixel=" + 2
																* pIY);
						System.out.println("lObservedAngleInDegrees=" + lObservedAngleInDegrees);

						updateNewState(	pControlPlaneIndex,
														pLightSheetIndex,
														lObservedAngleInDegrees);
					}
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			};

			Future<?> lFuture = getAdaptator().executeAsynchronously(lRunnable);

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

	protected double[] computeMetricForAlpha(	int pControlPlaneIndex,
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

		PlotTab lPlot = mMultiPlotZFocusCurves.getPlot(String.format(	"LS=%d, D=%d CPI=%d",
																																	pLightSheetIndex,
																																	pDetectionArmIndex,
																																	pControlPlaneIndex));
		lPlot.clearPoints();
		lPlot.setScatterPlot("samples");

		int lLength = lMetricArray.length / 2;

		for (int i = 0; i < lLength; i++)
		{
			System.out.format("%g\t%g \n",
												lDOFValueList.get(i),
												lMetricArray[i]);
			lPlot.addPoint(	"samples N",
											lDOFValueList.get(i),
											lMetricArray[i]);
		}

		for (int i = lLength; i < 2 * lLength; i++)
		{
			System.out.format("%g\t%g \n",
												lDOFValueList.get(i),
												lMetricArray[i]);
			lPlot.addPoint(	"samples P",
											lDOFValueList.get(i),
											lMetricArray[i]);
		}

		lPlot.ensureUpToDate();
		return lMetricArray;
	}

	public void updateNewState(	int pControlPlaneIndex,
															int pLightSheetIndex,
															double pObservedAngle)
	{
		double lCorrection = -pObservedAngle;
		getAdaptator().getNewAcquisitionState()
									.addAtControlPlaneIA(	pControlPlaneIndex,
																				pLightSheetIndex,
																				lCorrection);
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

}
