package rtlib.microscope.lightsheet.calibrate;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.argmax.SmartArgMaxFinder;
import rtlib.core.math.regression.linear.TheilSenEstimator;
import rtlib.core.math.regression.linear.UnivariateAffineFunction;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.stack.StackInterface;

public class LightSheetCalibration
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private final DCTS2D mDCTS2D;
	private final SmartArgMaxFinder mSmartArgMaxFinder;
	private MultiPlot mMultiPlot;
	private UnivariateAffineFunction[] mModelDn;

	@SuppressWarnings("unchecked")
	public LightSheetCalibration(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mDCTS2D = new DCTS2D();

		mMultiPlot = MultiPlot.getMultiPlot(this.getClass()
																						.getSimpleName());

		mSmartArgMaxFinder = new SmartArgMaxFinder();

		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		mModelDn = new UnivariateAffineFunction[lNumberOfDetectionArmDevices];
	}

	public void calibrate(int pLightSheetIndex,
												double pMinDZ,
												double pMaxDZ,
												double pStepDZ,
												double pMinIZ,
												double pMaxIZ,
												double pStepIZ)
	{
		mMultiPlot.clear();

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
		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			lTheilSenEstimators[i] = new TheilSenEstimator();
		}

		for (double iz = pMinIZ; iz < pMaxIZ; iz += pStepIZ)
		{
			final double[] dz = focus(pLightSheetIndex,
																pMinDZ,
																pMaxDZ,
																pStepDZ,
																iz);
			for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
			{
				lTheilSenEstimators[i].enter(dz[i], iz);
			}
		}

		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			final UnivariateAffineFunction lModel = lTheilSenEstimators[i].getModel();

			System.out.println(lModel);

			mModelDn[i] = lTheilSenEstimators[i].getModel();
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

					mMultiPlot.setVisible(true);
					PlotTab lPlot = mMultiPlot.getPlot(String.format(	"D=%d, I=%d, Iz=%g",
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
						System.out.format("argmax=%s fitprob=%g \n",
															lArgMax.toString(),
															mSmartArgMaxFinder.getLastFitProbability());

						lPlot.setScatterPlot("argmax");
						lPlot.addPoint("argmax", lArgMax, 0);

						dz[i] = lArgMax;
					}
					else
					{
						System.out.println("Argmax is NULL!");
					}
				}
			else
			{

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

	public void apply()
	{
		final int lNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																																	.getNumberOfDetectionArmDevices();

		if (lNumberOfDetectionArmDevices == 2)
		{
			final double lOffset = (-mModelDn[0].getConstant() / mModelDn[0].getSlope()) - (-mModelDn[1].getConstant() / mModelDn[1].getSlope())
															/ 2;

			final DetectionArmInterface lDetectionArmDevice1 = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(0);
			final DetectionArmInterface lDetectionArmDevice2 = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(1);

			lDetectionArmDevice1.getDetectionFocusZFunction()
													.set(new UnivariateAffineFunction(1,
																														-lOffset));
			lDetectionArmDevice2.getDetectionFocusZFunction()
													.set(new UnivariateAffineFunction(1,
																														lOffset));

		}

	}
}
