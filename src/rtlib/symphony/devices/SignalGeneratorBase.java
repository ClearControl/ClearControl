package rtlib.symphony.devices;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.device.queue.QueueProvider;
import rtlib.core.device.queue.QueueProviderUsingDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.symphony.movement.MovementInterface;
import rtlib.symphony.score.Score;
import rtlib.symphony.score.ScoreInterface;

public abstract class SignalGeneratorBase extends NamedVirtualDevice implements
																	SignalGeneratorInterface,
																	AsynchronousExecutorServiceAccess,
																	QueueProviderUsingDeviceInterface
{

	protected final ScoreInterface mStagingScore;
	protected final ScoreInterface mQueuedScore;

	protected volatile int mEnqueuedStateCounter = 0;
	protected QueueProvider<?> mQueueProvider;

	protected final BooleanVariable mTriggerVariable = new BooleanVariable(	"Trigger",
																			false);
	protected volatile boolean mIsPlaying;

	public SignalGeneratorBase(String pDeviceName)
	{
		super(pDeviceName);
		mQueuedScore = new Score(pDeviceName + ".queuedscore");
		mStagingScore = new Score(pDeviceName + ".stagingscore");
	}

	@Override
	public BooleanVariable getTriggerVariable()
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