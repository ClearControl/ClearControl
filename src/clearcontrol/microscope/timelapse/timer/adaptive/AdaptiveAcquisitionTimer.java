package clearcontrol.microscope.timelapse.timer.adaptive;

import java.util.concurrent.TimeUnit;

import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerBase;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;
import clearcontrol.stack.StackInterface;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class AdaptiveAcquisitionTimer extends TimelapseTimerBase
                                      implements
                                      TimelapseTimerInterface
{

  private static final int cPower = 2;

  private InterpolatedAcquisitionState mStackAcquisition;
  private AdaptiveAcquisitionTimerVisualizer mAdaptiveAcquisitionTimerVisualizer;

  private double mProbabilityThreshold;

  private volatile StackInterface mMonitoringStackAtLastAcquisition;
  private volatile StackInterface mLastMonitoredStack;

  private NormalDistribution mCalibrationNoiseGaussian;
  private TDoubleArrayList mMetricHistory = new TDoubleArrayList();

  private volatile boolean mAcquireNow = false;

  public AdaptiveAcquisitionTimer(InterpolatedAcquisitionState pStackAcquisition)
  {
    this(pStackAcquisition, 0.05);
  }

  public AdaptiveAcquisitionTimer(InterpolatedAcquisitionState pStackAcquisition,
                                  double pPValueThreshold)
  {
    super(0, TimeUnit.NANOSECONDS);
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
    if (mMonitoringStackAtLastAcquisition != null
        && !mMonitoringStackAtLastAcquisition.isFree())
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
        double lMetric =
                       StackUtils.computeAverageDifference(lLastStack,
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

    mCalibrationNoiseGaussian =
                              new NormalDistribution(lMean, lStdVar);

  }

  public void monitor()
  {
    StackInterface lMonitoringStack;

    lMonitoringStack = acquireMonitoringStack();

    mAdaptiveAcquisitionTimerVisualizer.visualizeStack(lMonitoringStack);

    if (mLastMonitoredStack != null)
    {
      double lAvgDifference =
                            StackUtils.computeAverageDifference(mMonitoringStackAtLastAcquisition,
                                                                lMonitoringStack,
                                                                cPower);

      mMetricHistory.add(lAvgDifference);

      mAdaptiveAcquisitionTimerVisualizer.append(mMetricHistory.size()
                                                 - 1, lAvgDifference);

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
      lPValue =
              1 - mCalibrationNoiseGaussian.cumulativeProbability(pMetric);
    }
    else
    {
      lPValue =
              mCalibrationNoiseGaussian.cumulativeProbability(pMetric);
    }

    return (1 - lPValue) < getProbabilityThreshold();
  }

  public StackInterface acquireMonitoringStack()
  {
    // TODO: fix this method
    return mLastMonitoredStack;
    /*LightSheetMicroscopeInterface lLSM = mStackAcquisition.getLightSheetMicroscope();
    
    int lNumberOfLightSheets = lLSM.getDeviceLists()
    																.getNumberOfDevices(LightSheetInterface.class);
    
    lLSM.clearQueue();
    
    int lNumberOfControlPlanes = mStackAcquisition.getNumberOfControlPlanes();
    
    for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
    {
    	mStackAcquisition.applyStateAtControlPlane(czi);
    
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
    /**/
  }

}
