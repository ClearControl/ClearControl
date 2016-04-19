package rtlib.microscope.lightsheet.acquisition.timming.adaptive;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import gnu.trove.list.array.TDoubleArrayList;
import rtlib.microscope.lightsheet.LightSheetMicroscopeInterface;
import rtlib.microscope.lightsheet.acquisition.StackAcquisition;
import rtlib.microscope.lightsheet.acquisition.timming.AcquisitionTimerBase;
import rtlib.microscope.lightsheet.acquisition.timming.AcquisitionTimerInterface;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import rtlib.stack.StackInterface;

public class AdaptiveAcquisitionTimer extends AcquisitionTimerBase implements
																																	AcquisitionTimerInterface
{

	private static final int cPower = 2;

	private StackAcquisition mStackAcquisition;
	private AdaptiveAcquisitionTimerVisualizer mAdaptiveAcquisitionTimerVisualizer;

	private double mProbabilityThreshold;

	private volatile StackInterface mMonitoringStackAtLastAcquisition;
	private volatile StackInterface mLastMonitoredStack;

	private NormalDistribution mCalibrationNoiseGaussian;
	private TDoubleArrayList mMetricHistory = new TDoubleArrayList();

	private volatile boolean mAcquireNow = false;

	public AdaptiveAcquisitionTimer(StackAcquisition pStackAcquisition)
	{
		this(pStackAcquisition, 0.05);
	}

	public AdaptiveAcquisitionTimer(StackAcquisition pStackAcquisition,
																	double pPValueThreshold)
	{
		super();
		mStackAcquisition = pStackAcquisition;
		setProbabilityThreshold(pPValueThreshold);
	}

	public double getProbabilityThreshold()
	{
		return mProbabilityThreshold;
	}

	public void setProbabilityThreshold(double pPValueThreshold)
	{
		mProbabilityThreshold = pPValueThreshold;
	}

	public TDoubleArrayList getMetricHistory()
	{
		return mMetricHistory;
	}

	@Override
	public void notifyAcquisition()
	{
		super.notifyAcquisition();
		mAcquireNow = false;
		if (mMonitoringStackAtLastAcquisition != null && !mMonitoringStackAtLastAcquisition.isFree())
			mMonitoringStackAtLastAcquisition.free();
		mMonitoringStackAtLastAcquisition = mLastMonitoredStack;
	}

	@Override
	public long timeLeftBeforeNextTimePoint(TimeUnit pTimeUnit)
	{
		if (mAcquireNow)
			return 0;
		return super.timeLeftBeforeNextTimePoint(pTimeUnit);
	}

	public void calibrate(int pRounds)
	{
		StackInterface lLastStack = null;

		DescriptiveStatistics lStats = new DescriptiveStatistics();

		for (int i = 0; i <= pRounds; i++)
		{
			StackInterface lNewStack;
			lNewStack = acquireMonitoringStack();

			if (lLastStack != null)
			{
				double lMetric = StackUtils.computeAverageDifference(	lLastStack,
																															lNewStack,
																															cPower);

				lStats.addValue(lMetric);
			}

			if (lLastStack != null)
				lLastStack.free();

			lLastStack = lNewStack;
		}

		double lMean = lStats.getMean();
		double lStdVar = lStats.getStandardDeviation();

		mCalibrationNoiseGaussian = new NormalDistribution(lMean, lStdVar);

	}

	public void monitor()
	{
		StackInterface lMonitoringStack;

		lMonitoringStack = acquireMonitoringStack();

		mAdaptiveAcquisitionTimerVisualizer.visualizeStack(lMonitoringStack);

		if (mLastMonitoredStack != null)
		{
			double lAvgDifference = StackUtils.computeAverageDifference(mMonitoringStackAtLastAcquisition,
																																	lMonitoringStack,
																																	cPower);

			mMetricHistory.add(lAvgDifference);

			mAdaptiveAcquisitionTimerVisualizer.append(	mMetricHistory.size() - 1,
																									lAvgDifference);

			if (isSignificant(lAvgDifference))
				mAcquireNow = true;

			if (!mLastMonitoredStack.isFree())
				mLastMonitoredStack.free();
			mLastMonitoredStack = lMonitoringStack;
		}
		else
			mLastMonitoredStack = lMonitoringStack;

	}

	private boolean isSignificant(double pMetric)
	{
		double lPValue;
		if (pMetric > mCalibrationNoiseGaussian.getMean())
		{
			lPValue = 1 - mCalibrationNoiseGaussian.cumulativeProbability(pMetric);
		}
		else
		{
			lPValue = mCalibrationNoiseGaussian.cumulativeProbability(pMetric);
		}

		return (1 - lPValue) < getProbabilityThreshold();
	}

	public StackInterface acquireMonitoringStack()
	{
		LightSheetMicroscopeInterface lLSM = mStackAcquisition.getLightSheetMicroscope();

		int lNumberOfLightSheets = lLSM.getDeviceLists()
																		.getNumberOfDevices(LightSheetInterface.class);

		lLSM.clearQueue();

		int lNumberOfControlPlanes = mStackAcquisition.getCurrentState()
																									.getNumberOfControlPlanes();

		for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
		{
			mStackAcquisition.setToControlPlane(czi);

			lLSM.setC(false);
			lLSM.setILO(false);
			lLSM.addCurrentStateToQueue();
			lLSM.addCurrentStateToQueue();

			for (int l = 0; l < lNumberOfLightSheets; l++)
			{
				lLSM.setI(l);
				lLSM.setC(true);
				lLSM.setILO(true);
				lLSM.addCurrentStateToQueue();
			}
		}

		mStackAcquisition.addStackMargin(1);

		lLSM.finalizeQueue();

		try
		{
			lLSM.useRecycler("adaptive_timing", 1, 1, 1);
			Boolean lSuccess = lLSM.playQueueAndWaitForStacks(lLSM.getQueueLength(),
																												TimeUnit.SECONDS);

			if (lSuccess != null && lSuccess)
			{
				StackInterface lFusedStack = StackUtils.fuse(lLSM);
				return lFusedStack;
			}
			else
				return null;

		}
		catch (InterruptedException | ExecutionException
				| TimeoutException e)
		{
			e.printStackTrace();
			return null;
		}

	}

}
