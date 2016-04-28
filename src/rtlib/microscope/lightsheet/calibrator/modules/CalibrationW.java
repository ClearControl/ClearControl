package rtlib.microscope.lightsheet.calibrator.modules;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.math3.stat.StatUtils;

import gnu.trove.list.array.TDoubleArrayList;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.bounded.BoundedVariable;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.calibrator.utils.ImageAnalysisUtils;
import rtlib.microscope.lightsheet.component.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheet;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.stack.StackInterface;

public class CalibrationW
{

	private final LightSheetMicroscope mLightSheetMicroscope;
	private int mNumberOfDetectionArmDevices;
	private int mNumberOfLightSheetDevices;
	private HashMap<Integer, TDoubleArrayList> mIntensityLists;
	private TDoubleArrayList mWList = new TDoubleArrayList();
	private MultiPlot mAverageIntensityCurves;

	public CalibrationW(LightSheetMicroscope pLightSheetMicroscope)
	{
		mLightSheetMicroscope = pLightSheetMicroscope;

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																												.getNumberOfDevices(DetectionArmInterface.class);

		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																											.getNumberOfDevices(LightSheetInterface.class);

		mAverageIntensityCurves = MultiPlot.getMultiPlot(this.getClass()
																													.getSimpleName() + "W-calibration: average intensity curves");
		mAverageIntensityCurves.setVisible(false);

	}

	public boolean calibrate(	int pDetectionArmIndex,
														int pNumberOfSamples)
	{
		mIntensityLists.clear();
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			double[] lAverageIntensities = calibrate(	l,
																								pDetectionArmIndex,
																								3);
			if (lAverageIntensities == null)
				return false;

			mIntensityLists.put(l,
													new TDoubleArrayList(lAverageIntensities));

			if (ScriptingEngine.isCancelRequestedStatic())
				return false;
		}

		return true;
	}

	public double[] calibrate(int pLightSheetIndex,
														int pDetectionArmIndex,
														int pNumberOfSamples)
	{
		if (!mAverageIntensityCurves.isVisible())
			mAverageIntensityCurves.setVisible(true);

		try
		{
			LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																		.getDevice(	LightSheetInterface.class,
																																								pLightSheetIndex);

			BoundedVariable<Double> lWVariable = lLightSheetDevice.getWidthVariable();

			UnivariateAffineFunction lWFunction = lLightSheetDevice.getWidthFunction()
																															.get();
			double lMinW = lWVariable.getMin();
			double lMaxW = lWVariable.getMax();
			double lStep = (lMaxW - lMinW) / pNumberOfSamples;

			// Building queue start:
			mLightSheetMicroscope.clearQueue();
			mLightSheetMicroscope.zero();

			mLightSheetMicroscope.setI(pLightSheetIndex);
			mLightSheetMicroscope.setIX(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIY(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIZ(pLightSheetIndex, 0);
			mLightSheetMicroscope.setIH(pLightSheetIndex, 0);

			mLightSheetMicroscope.setDZ(pDetectionArmIndex, 0);
			mLightSheetMicroscope.setC(pDetectionArmIndex, false);

			mLightSheetMicroscope.setIZ(pLightSheetIndex, lMinW);
			mLightSheetMicroscope.addCurrentStateToQueue();

			mWList.clear();
			for (double w = lMinW; w <= lMaxW; w += lStep)
			{
				mWList.add(w);
				mLightSheetMicroscope.setIZ(pLightSheetIndex, w);

				mLightSheetMicroscope.setC(pDetectionArmIndex, false);
				for (int i = 0; i < 10; i++)
					mLightSheetMicroscope.addCurrentStateToQueue();

				mLightSheetMicroscope.setC(pDetectionArmIndex, true);
				mLightSheetMicroscope.addCurrentStateToQueue();
			}
			mLightSheetMicroscope.finalizeQueue();
			// Building queue end.

			mLightSheetMicroscope.useRecycler("adaptation", 1, 4, 4);
			final Boolean lPlayQueueAndWait = mLightSheetMicroscope.playQueueAndWaitForStacks(mLightSheetMicroscope.getQueueLength(),
																																												TimeUnit.SECONDS);

			if (!lPlayQueueAndWait)
				return null;

			final StackInterface lStackInterface = mLightSheetMicroscope.getStackVariable(pDetectionArmIndex)
																																	.get();

			OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lStackInterface.getImage();

			System.out.println("Image: " + lImage);

			long lWidth = lImage.dimension(0);
			long lHeight = lImage.dimension(1);

			System.out.format("Image: width=%d, height=%d \n",
												lWidth,
												lHeight);

			double[] lAverageIntensities = ImageAnalysisUtils.computeImageAverageIntensityPerPlane(lImage);

			System.out.format("Image: average intensities: \n");

			PlotTab lPlot = mAverageIntensityCurves.getPlot(String.format("D=%d, I=%d",
																																		pDetectionArmIndex,
																																		pLightSheetIndex));
			lPlot.clearPoints();
			lPlot.setScatterPlot("avg. intensity");

			for (int i = 0; i < lAverageIntensities.length; i++)
			{
				System.out.println(lAverageIntensities[i]);
				lPlot.addPoint(	"avg. intensity",
												mWList.get(i),
												lAverageIntensities[i]);
			}

			lPlot.ensureUpToDate();

			return lAverageIntensities;
		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public double apply()
	{
		double lError = 0;

		double lIntensityMin = Double.POSITIVE_INFINITY;
		double lIntensityMax = Double.NEGATIVE_INFINITY;

		TDoubleArrayList lSums = new TDoubleArrayList();
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			TDoubleArrayList lIntensityList = mIntensityLists.get(l);
			lSums.add(lIntensityList.sum());

			lIntensityMin = min(lIntensityMin, lIntensityList.min());
			lIntensityMax = max(lIntensityMax, lIntensityList.max());
		}

		double lLargestSum = Double.NEGATIVE_INFINITY;
		int lIndexOfLargestSum = -1;
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			double lSum = lSums.get(l);
			if (lSum > lLargestSum)
			{
				lIndexOfLargestSum = l;
				lLargestSum = lSum;
			}
		}

		TDoubleArrayList lReferenceIntensityList = mIntensityLists.get(lIndexOfLargestSum);

		TDoubleArrayList[] lOffsetsLists = new TDoubleArrayList[mNumberOfLightSheetDevices];
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			lOffsetsLists[l] = new TDoubleArrayList();

		double lStep = (lIntensityMax - lIntensityMin) / (lReferenceIntensityList.size());

		for (double i = lIntensityMin; i <= lIntensityMax; i += lStep)
		{
			for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			{

				int lReferenceIndex = searchFirstAbove(	lReferenceIntensityList,
																								i);
				double lReferenceW = mWList.get(lReferenceIndex);

				int lOtherIndex = searchFirstAbove(lReferenceIntensityList, i);

				double lOtherW = mWList.get(lOtherIndex);

				double lOffsets = lOtherW - lReferenceW;

				lOffsetsLists[l].add(lOffsets);

			}
		}

		TDoubleArrayList lMedianOffsets = new TDoubleArrayList();
		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			double lMedianOffset = StatUtils.percentile(lOffsetsLists[l].toArray(),
																									50);
			lMedianOffsets.add(lMedianOffset);
		}

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
		{
			LightSheetInterface lLightSheetDevice = mLightSheetMicroscope.getDeviceLists()
																																		.getDevice(	LightSheet.class,
																																								l);

			UnivariateAffineFunction lFunction = lLightSheetDevice.getWidthFunction()
																														.get();

			double lOffset = lMedianOffsets.get(l);

			System.out.format("Applying offset: %g to lightsheet %d \n",
												lOffset,
												l);

			lFunction.composeWith(UnivariateAffineFunction.axplusb(	1,
																															lOffset));

			System.out.format("Width function for lightsheet %d is now: %s \n",
												l,
												lFunction);

			lError += abs(lOffset);
		}

		System.out.format("Error after applying width offset correction: %g \n",
											lError);

		return lError;
	}

	private int searchFirstAbove(TDoubleArrayList pList, double pValue)
	{
		int lSize = pList.size();
		for (int i = 0; i < lSize; i++)
			if (pList.getQuick(i) >= pValue)
				return i;
		return lSize - 1;
	}

	public void reset()
	{
		mAverageIntensityCurves.clear();

	}
}
