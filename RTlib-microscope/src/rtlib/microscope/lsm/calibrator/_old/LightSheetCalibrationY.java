package rtlib.microscope.lsm.calibrator._old;

import static java.lang.Math.abs;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import rtlib.core.math.argmax.Fitting1D;
import rtlib.core.math.argmax.methods.ModeArgMaxFinder;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.math.regression.linear.TheilSenEstimator;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.ip.iqm.DCTS2D;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.calibrator.utils.GeometryUtils;
import rtlib.microscope.lsm.calibrator.utils.ImageAnalysisUtils;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.fragmented.FragmentedMemoryInterface;

public class LightSheetCalibrationY
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private MultiPlot mLightSheetCentersPlot,
			mLightSheetCentersModels;
	private MultiKeyMap<Integer, UnivariateAffineFunction> mOnImageXToLightSheetYFunctions;
	private MultiKeyMap<Integer, Line> mOnImageLines;

	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;

	@SuppressWarnings("unchecked")
	public LightSheetCalibrationY(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mLightSheetCentersPlot = MultiPlot.getMultiPlot(this.getClass()
															.getSimpleName() + "Y-calibration: lightsheet centers");

		mLightSheetCentersModels = MultiPlot.getMultiPlot(this.getClass()
																.getSimpleName() + "Y-calibration: models");

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfDetectionArmDevices();

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
															.getNumberOfLightSheetDevices();

		mOnImageXToLightSheetYFunctions = new MultiKeyMap<>();
		mOnImageLines = new MultiKeyMap<>();
	}


	public void calibrate(	int pLightSheetIndex,
							double pMinY,
							double pMaxY,
							double pStep)
	{
		try
		{

			mLightSheetCentersPlot.clear();
			mLightSheetCentersPlot.setVisible(true);

			mLightSheetCentersModels.clear();
			mLightSheetCentersModels.setVisible(true);

			// Building queue start:
			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			mLightSheetMicroscope.setI(pLightSheetIndex);
			mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIH(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIZ(pLightSheetIndex, 1);

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				mLightSheetMicroscope.setDZ(i, 0);

			mLightSheetMicroscope.setIY(pLightSheetIndex, pMinY);
			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				mLightSheetMicroscope.setC(i, false);
			mLightSheetMicroscope.addCurrentStateToQueue();

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				mLightSheetMicroscope.setC(i, true);

			final TDoubleArrayList lYList = new TDoubleArrayList();
			for (double y = pMinY; y <= pMaxY; y += pStep)
			{
				lYList.add(y);
				mLightSheetMicroscope.setIY(pLightSheetIndex, y);
				mLightSheetMicroscope.addCurrentStateToQueue();
			}

			for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
			{
				mLightSheetMicroscope.setDZ(i, pMinY);
				mLightSheetMicroscope.setC(i, false);
			}
			mLightSheetMicroscope.addCurrentStateToQueue();

			mLightSheetMicroscope.finalizeQueue();
			// Building queue end.

			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(	mLightSheetMicroscope.getQueueLength(),
																								TimeUnit.SECONDS);

			if (lPlayQueueAndWait)
				for (int i = 0; i < mNumberOfDetectionArmDevices; i++)
				{
					System.out.format("DetectionArmDevice %d \n", i);

					final StackInterface<UnsignedShortType, ShortOffHeapAccess> lStackInterface = mLightSheetMicroscope.getStackVariable(i)
																														.get();

					OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

					System.out.println("lImage=" + lImage);

					long lWidth = lImage.dimension(0);
					long lHeight = lImage.dimension(1);

					System.out.format(	"image: width=%d, height=%d \n",
										lWidth,
										lHeight);

					final Vector2D[] lPoints = ImageAnalysisUtils.findCOMOfBrightestPointsForEachPlane(lImage);

					plotYLine(pLightSheetIndex, i, lPoints);

					Line lLine = GeometryUtils.computeYLineOnImage(lWidth, lPoints);


					mOnImageLines.put(pLightSheetIndex, i, lLine);

					System.out.println("lLine=" + lLine);

					UnivariateAffineFunction lOnImageXToLightSheetYFunction = computeOnImageXToLightSheetYFunction(	lYList,
																													lPoints);

					mOnImageXToLightSheetYFunctions.put(pLightSheetIndex,
														i,
														lOnImageXToLightSheetYFunction);

				}

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

	}

	private UnivariateAffineFunction computeOnImageXToLightSheetYFunction(	final TDoubleArrayList lYList,
																			final Vector2D[] lPoints)
	{
		TheilSenEstimator lTheilSenEstimator = new TheilSenEstimator();

		for (int j = 0; j < lPoints.length; j++)
			lTheilSenEstimator.enter(lPoints[j].getX(), lYList.get(j));

		UnivariateAffineFunction lOnImageXToLightSheetYFunction = lTheilSenEstimator.getModel();
		return lOnImageXToLightSheetYFunction;
	}

	private void plotYLine(	int pLightSheetIndex,
							int i,
							final Vector2D[] lPoints)
	{
		PlotTab lPlot = mLightSheetCentersPlot.getPlot(String.format(	"D=%d, I=%d",
																		i,
																		pLightSheetIndex));
		lPlot.setScatterPlot("centers");

		System.out.format("centers: \n");
		for (int j = 0; j < lPoints.length; j++)
		{

			lPlot.addPoint(	"samples",
							lPoints[j].getX(),
							lPoints[j].getY());

			System.out.format(	"%d,%d\t%g\t%g\n",
								i,
								j,
								lPoints[j].getX(),
								lPoints[j].getY());
		}
		lPlot.ensureUpToDate();
	}

	

	public void reset()
	{

	}

	@SuppressWarnings("unchecked")
	public double apply(int pLightSheetIndex,
						boolean pAdjustDetectionZ)
	{
		return -1;

	}

}
