package rtlib.symphony.devices;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.device.queue.QueueProvider;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.interfaces.ScoreInterface;
import rtlib.symphony.score.CompiledScore;
import rtlib.symphony.score.Score;

public abstract class SignalGeneratorBase extends NamedVirtualDevice implements
																																		SignalGeneratorInterface,
																																		AsynchronousExecutorServiceAccess
{

	protected final ScoreInterface mStagingScore;
	protected final CompiledScore mCompiledScore;

	protected volatile int mEnqueuedStateCounter = 0;
	protected QueueProvider<?> mQueueProvider;

	protected final BooleanVariable mTriggerVariable = new BooleanVariable(	"Trigger",
																																					false);
	protected volatile boolean mIsPlaying;

	public SignalGeneratorBase(String pDeviceName)
	{
		super(pDeviceName);
		mCompiledScore = new CompiledScore(pDeviceName + ".compiledscore");
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
	public void clearQueue()
	{
		mEnqueuedStateCounter = 0;
		mCompiledScore.clear();
	}

	@Override
	public void addCurrentStateToQueueNotCounting()
	{
		mCompiledScore.addScore(mStagingScore);
	}

	@Override
	public void addCurrentStateToQueue()
	{
		addCurrentStateToQueueNotCounting();
		mEnqueuedStateCounter++;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setQueueProvider(QueueProvider<?> pQueueProvider)
	{
		mQueueProvider = pQueueProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void ensureQueueIsUpToDate()
	{
		mCompiledScore.clear();
		((QueueProvider<SignalGeneratorBase>) mQueueProvider).buildQueue(this);
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
			final boolean lPlayed = playScore(mCompiledScore);
			;
			mIsPlaying = false;
			lCurrentThread.setPriority(lCurrentThreadPriority);
			return lPlayed;
		};
		final Future<Boolean> lFuture = executeAsynchronously(lCall);
		return lFuture;
	}

	@Override
	public boolean isPlaying()
	{
		return mIsPlaying;
	}


}