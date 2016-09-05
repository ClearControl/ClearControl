package clearcontrol.microscope.timelapse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.task.LoopTaskDevice;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;
import clearcontrol.microscope.timelapse.timer.fixed.FixedIntervalTimelapseTimer;

public class Timelapse extends LoopTaskDevice	implements
																							TimelapseInterface
{
	private Variable<TimelapseTimerInterface> mTimelapseTimer = new Variable<>(	"TimelapseTimer",
																																							null);

	private Variable<Boolean> mLimitNumberOfTimePointsVariable = new Variable<>("LimitNumberOfTimePoints",
																																							true);

	private Variable<Boolean> mLimitTimelapseDurationVariable = new Variable<>(	"LimitTimelapseDuration",
																																							false);

	private Variable<Boolean> mLimitTimelapseDateTimeVariable = new Variable<>(	"LimitTimelapseDateTime",
																																							false);

	private Variable<Long> mMaxNumberOfTimePointsVariable = new Variable<Long>(	"MaxNumberOfTimePoints",
																																							1L);

	private Variable<Long> mMaxDurationVariable = new Variable<Long>(	"MaxDuration",
																																		1L);

	private Variable<TimeUnit> mMaxDurationUnitVariable = new Variable<TimeUnit>(	"MaxDurationUnit",
																																								TimeUnit.HOURS);
	
	private Variable<LocalDateTime> mMaxDateTimeVariable = new Variable<LocalDateTime>(	"MaxDateTime",
			LocalDateTime.now());

	private Variable<LocalDateTime> mStartDateTimeVariable = new Variable<LocalDateTime>(	"StartDateTime",
																																												LocalDateTime.now());
	


	private Variable<Long> mTimePointCounterVariable = new Variable<Long>("TimePointCounter",
																																				1L);

	public Timelapse(TimelapseTimerInterface pTimelapseTimer)
	{
		super("Timelapse");
		mTimelapseTimer.set(pTimelapseTimer);
	}

	public Timelapse()
	{
		this(new FixedIntervalTimelapseTimer());
	}

	public Variable<TimelapseTimerInterface> getTimelapseTimer()
	{
		return mTimelapseTimer;
	}

	@Override
	public void run()
	{
		mStartDateTimeVariable.set(LocalDateTime.now());
		super.run();
	}

	@Override
	public boolean loop()
	{
		if (mTimelapseTimer == null)
			return false;

		TimelapseTimerInterface lTimelapseTimer = mTimelapseTimer.get();

		lTimelapseTimer.waitToAcquire(1, TimeUnit.DAYS);
		acquire();


		if (mLimitNumberOfTimePointsVariable.get())
			if (mTimePointCounterVariable.get() > mMaxNumberOfTimePointsVariable.get())
				return false;

		if (mLimitTimelapseDurationVariable.get() && mMaxDurationVariable.get()!=null)
			if (checkMaxDuration())
				return false;
		
		if(mLimitTimelapseDateTimeVariable.get() && mMaxDateTimeVariable.get()!=null)
			if(checkMaxDateTime())
				return false;

		return true;
	}


	private boolean checkMaxDuration()
	{
		LocalDateTime lStartDateTime = mStartDateTimeVariable.get();

		Duration lDuration = Duration.between(lStartDateTime,
																					LocalDateTime.now());

		long lCurrentlMeasuredDurationInNanos = lDuration.getNano();

		long lMaxDurationInNanos = TimeUnit.NANOSECONDS.convert(mMaxDurationVariable.get(),
																														mMaxDurationUnitVariable.get());

		return lCurrentlMeasuredDurationInNanos > lMaxDurationInNanos;
	}
	

	private boolean checkMaxDateTime()
	{
		LocalDateTime lMaxDateTime = mMaxDateTimeVariable.get();
		LocalDateTime lNowDateTime = LocalDateTime.now();
		
		return lNowDateTime.isAfter(lMaxDateTime);
	}

	private void acquire()
	{
		// TODO: acquire TP here...
		
		mTimePointCounterVariable.increment();
	}

}
