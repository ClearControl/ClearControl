package rtlib.symphony.devices.nirio.compiler;

import java.util.concurrent.locks.ReentrantLock;

import coremem.buffers.ContiguousBuffer;

public class NIRIOCompiledScore

{


	private volatile long mNumberOfMovements;
	private ContiguousBuffer mDeltaTimeBuffer;
	private ContiguousBuffer mSyncBuffer;
	private ContiguousBuffer mNumberOfTimePointsBuffer;
	private ContiguousBuffer mScoreBuffer;

	public ReentrantLock mReentrantLock = new ReentrantLock();

	public NIRIOCompiledScore()
	{
	}

	@Override
	public String toString()
	{
		return String.format(	"NIRIOCompiledScore:\n mNumberOfMovements=%s\n mDeltaTimeBuffer=%s\n mSyncBuffer=%s\n mNumberOfTimePointsBuffer=%s\n mScoreBuffer=%s\n\n",
													getNumberOfMovements(),
													getDeltaTimeBuffer(),
													getSyncBuffer(),
													getNumberOfTimePointsBuffer(),
													getScoreBuffer());
	}



	public ContiguousBuffer getDeltaTimeBuffer()
	{
		return mDeltaTimeBuffer;
	}

	public void setDeltaTimeBuffer(ContiguousBuffer pDeltaTimeBuffer)
	{
		mDeltaTimeBuffer = pDeltaTimeBuffer;
	}

	public ContiguousBuffer getSyncBuffer()
	{
		return mSyncBuffer;
	}

	public void setSyncBuffer(ContiguousBuffer pSyncBuffer)
	{
		mSyncBuffer = pSyncBuffer;
	}

	public ContiguousBuffer getNumberOfTimePointsBuffer()
	{
		return mNumberOfTimePointsBuffer;
	}

	public void setNumberOfTimePointsBuffer(ContiguousBuffer pNumberOfTimePointsBuffer)
	{
		mNumberOfTimePointsBuffer = pNumberOfTimePointsBuffer;
	}

	public ContiguousBuffer getScoreBuffer()
	{
		return mScoreBuffer;
	}

	public void setScoreBuffer(ContiguousBuffer pScoreBuffer)
	{
		mScoreBuffer = pScoreBuffer;
	}

	public void setNumberOfMovements(long pNumberOfMovements)
	{
		mNumberOfMovements = pNumberOfMovements;
	}

	public long getNumberOfMovements()
	{
		return mNumberOfMovements;
	}
}
