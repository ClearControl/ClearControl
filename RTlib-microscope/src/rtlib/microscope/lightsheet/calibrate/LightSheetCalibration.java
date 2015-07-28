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
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.stack.StackInterface;

public class LightSheetCalibration
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private final DCTS2D mDCTS2D;
	private final SmartArgMaxFinder mSmartArgMaxFinder;

	@SuppressWarnings("unchecked")
	public LightSheetCalibration(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mDCTS2D = new DCTS2D();

		mSmartArgMaxFinder = new SmartArgMaxFinder();
	}

	public void calibrate(int pLightSheetIndex,
												double pMinDZ,
												double pMaxDZ,
												double pStep)
	{
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

		for (double iz = -1; iz < 1; iz += 0.05)
		{
			final double[] dz = focus(pLightSheetIndex,
																pMinDZ,
																pMaxDZ,
																pStep,
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
		}

		if (lNumberOfDetectionArmDevices == 1)
		{

		}
		if (lNumberOfDetectionArmDevices == 2)
		{

			final DetectionArmInterface lDetectionArmDevice1 = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(0);
			final DetectionArmInterface lDetectionArmDevice2 = mLightSheetMicroscope.getDeviceLists()
																																							.getDetectionArmDevice(1);

			final UnivariateAffineFunction lModel1 = lTheilSenEstimators[0].getModel();
			final UnivariateAffineFunction lModel2 = lTheilSenEstimators[0].getModel();

			final double lOffset = (-lModel1.getConstant() / lModel1.getSlope()) - (-lModel2.getConstant() / lModel2.getSlope())
															/ 2;

			lDetectionArmDevice1.getDetectionFocusZFunction()
													.set(new UnivariateAffineFunction(1,
																														-lOffset));
			lDetectionArmDevice2.getDetectionFocusZFunction()
													.set(new UnivariateAffineFunction(1,
																														lOffset));

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

				mLightSheetMicroscope.setIZ(pLightSheetIndex, pIZ);

				mLightSheetMicroscope.addCurrentStateToQueue();
			}

			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWait(	20,
																																								TimeUnit.SECONDS);

			if (lPlayQueueAndWait)
				for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
				{
					final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(i)
																																																							.get();
					final double[] lDCTSArray = mDCTS2D.computeImageQualityMetric((OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage());

					final Double lArgMax = mSmartArgMaxFinder.argmax(	lDZList.toArray(),
																														lDCTSArray);

					if (lArgMax != null)
					{
						dz[i] = lArgMax;
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
}
