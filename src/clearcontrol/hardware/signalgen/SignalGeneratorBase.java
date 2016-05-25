package clearcontrol.hardware.signalgen;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.queue.QueueProvider;
import clearcontrol.device.queue.QueueProviderUsingDeviceInterface;
import clearcontrol.hardware.signalgen.movement.MovementInterface;
import clearcontrol.hardware.signalgen.score.Score;
import clearcontrol.hardware.signalgen.score.ScoreInterface;

public abstract class SignalGeneratorBase extends VirtualDevice implements
																																		SignalGeneratorInterface,
																																		AsynchronousExecutorServiceAccess,
																																		QueueProviderUsingDeviceInterface
{

	protected final ScoreInterface mStagingScore;
	protected final ScoreInterface mQueuedScore;

	protected volatile int mEnqueuedStateCounter = 0;
	protected QueueProvider<?> mQueueProvider;

	protected final Variable<Boolean> mTriggerVariable = new Variable<Boolean>(	"Trigger",
																																													false);
	protected volatile boolean mIsPlaying;

	public SignalGeneratorBase(String pDeviceName)
	{
		super(pDeviceName);
		mQueuedScore = new Score(pDeviceName + ".queuedscore");
		mStagingScore = new Score(pDeviceName + ".stagingscore");
	}

	@Override
	public Variable<Boolean> getTriggerVariable()
	{
		return mTriggerVariable;
	}

	@Override
	public ScoreInterface getStagingScore()
	{
		return mStagingScore;
	}

	@Override
	public ScoreInterface getQueuedScore()
	{
		return mQueuedScore;
	}

	@Override
	public void clearQueue()
	{
		mEnqueuedStateCounter = 0;
		mQueuedScore.clear();
	}

	@Override
	public void addCurrentStateToQueue()
	{
		mQueuedScore.addScoreCopy(mStagingScore);
		mEnqueuedStateCounter++;
	}

	@Override
	public void finalizeQueue()
	{

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setQueueProvider(QueueProvider<?> pQueueProvider)
	{
		mQueueProvider = pQueueProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buildQueueFromProvider()
	{
		if (mQueueProvider != null)
		{
			mQueuedScore.clear();
			((QueueProvider<SignalGeneratorBase>) mQueueProvider).buildQueue(this);
		}
	}

	@Override
	public int getQueueLength()
	{
		return mEnqueuedStateCounter;
	}

	@Override
	public Future<Boolean> playQueue()
	{
		final Callable<Boolean> lCall = () -> {
			final Thread lCurrentThread = Thread.currentThread();
			final int lCurrentThreadPriority = lCurrentThread.getPriority();
			lCurrentThread.setPriority(Thread.MAX_PRIORITY);
			mIsPlaying = true;
			// System.out.println("Symphony: playQueue() begin");
			final boolean lPlayed = playScore(getQueuedScore());
			// System.out.println("Symphony: playQueue() end");
			mIsPlaying = false;
			lCurrentThread.setPriority(lCurrentThreadPriority);
			return lPlayed;
		};
		final Future<Boolean> lFuture = executeAsynchronously(lCall);
		return lFuture;
	}

	@Override
	public long estimatePlayTime(TimeUnit pTimeUnit)
	{
		long lDuration = 0;
		for (final MovementInterface lMovement : mQueuedScore.getMovements())
		{
			lDuration += lMovement.getDuration(pTimeUnit);
		}
		lDuration *= mQueuedScore.getNumberOfMovements();
		return lDuration;
	}

	@Override
	public boolean isPlaying()
	{
		return mIsPlaying;
	}

}