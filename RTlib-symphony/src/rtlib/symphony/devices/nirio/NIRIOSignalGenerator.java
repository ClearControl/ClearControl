package rtlib.symphony.devices.nirio;

import nirioj.direttore.Direttore;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.symphony.devices.SignalGenerator;
import rtlib.symphony.score.CompiledScore;

public class NIRIOSignalGenerator extends NamedVirtualDevice implements

																														SignalGenerator
{

	private final Direttore mDirettore;

	public NIRIOSignalGenerator()
	{
		super("NIRIOSignalGenerator");
		mDirettore = new Direttore();
	}

	@Override
	public double getTemporalGranularityInMicroseconds()
	{
		return mDirettore.getTemporalGranularityInMicroseconds();
	}

	public boolean play(CompiledScore pCompiledScore)
	{
		final Thread lCurrentThread = Thread.currentThread();
		final int lCurrentThreadPriority = lCurrentThread.getPriority();
		lCurrentThread.setPriority(Thread.MAX_PRIORITY);

		boolean lPlayed = mDirettore.play(pCompiledScore.getDeltaTimeBuffer(Direttore.cNanosecondsPerTicks),
																			pCompiledScore.getNumberOfTimePointsBuffer(Direttore.cNanosecondsPerTicks),
																			pCompiledScore.getSyncBuffer(Direttore.cNanosecondsPerTicks),
																			pCompiledScore.getNumberOfMovements(),
																			pCompiledScore.getScoreBuffer(Direttore.cNanosecondsPerTicks));
		lCurrentThread.setPriority(lCurrentThreadPriority);

		return lPlayed;
	}

	@Override
	public boolean open()
	{
		try
		{
			if (!mDirettore.open())
			{
				return false;
			}

			return mDirettore.start();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean start()
	{
		System.out.println(this.getClass().getSimpleName() + ": start()");
		return true;
	}

	public boolean resume()
	{
		System.out.println(this.getClass().getSimpleName() + ": resume()");
		return true;
	}

	@Override
	public boolean stop()
	{
		System.out.println(this.getClass().getSimpleName() + ": stop()");
		return true;
	}

	double mWaitTimeInMilliseconds = 0;

	@Override
	public boolean close()
	{
		try
		{
			System.out.println(this.getClass().getSimpleName() + ": close()");
			mDirettore.stop();
			mDirettore.close();
			return true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

}
