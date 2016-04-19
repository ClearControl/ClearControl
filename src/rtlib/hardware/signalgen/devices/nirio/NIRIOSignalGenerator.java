package rtlib.hardware.signalgen.devices.nirio;

import static java.lang.Math.toIntExact;

import nirioj.direttore.Direttore;
import rtlib.hardware.signalgen.SignalGeneratorBase;
import rtlib.hardware.signalgen.SignalGeneratorInterface;
import rtlib.hardware.signalgen.devices.nirio.compiler.NIRIOCompiledScore;
import rtlib.hardware.signalgen.devices.nirio.compiler.NIRIOScoreCompiler;
import rtlib.hardware.signalgen.score.ScoreInterface;

public class NIRIOSignalGenerator extends SignalGeneratorBase	implements
																															SignalGeneratorInterface

{

	double mWaitTimeInMilliseconds = 0;
	private final Direttore mDirettore;
	private final NIRIOCompiledScore mNIRIOCompiledScore = new NIRIOCompiledScore();

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

	@Override
	public boolean playScore(ScoreInterface pScore)
	{
		final Thread lCurrentThread = Thread.currentThread();
		final int lCurrentThreadPriority = lCurrentThread.getPriority();
		lCurrentThread.setPriority(Thread.MAX_PRIORITY);
		mTriggerVariable.set(true);

		boolean lPlayed = false;

		NIRIOScoreCompiler.compile(mNIRIOCompiledScore, pScore);

		lPlayed = mDirettore.play(mNIRIOCompiledScore.getDeltaTimeBuffer()
																									.getContiguousMemory()
																									.getBridJPointer(Integer.class),
															mNIRIOCompiledScore.getNumberOfTimePointsBuffer()
																									.getContiguousMemory()
																									.getBridJPointer(Integer.class),
															mNIRIOCompiledScore.getSyncBuffer()
																									.getContiguousMemory()
																									.getBridJPointer(Integer.class),
															toIntExact(mNIRIOCompiledScore.getNumberOfMovements()),
															mNIRIOCompiledScore.getScoreBuffer()
																									.getContiguousMemory()
																									.getBridJPointer(Short.class));

		lCurrentThread.setPriority(lCurrentThreadPriority);
		mTriggerVariable.set(false);

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

	public boolean resume()
	{
		System.out.println(this.getClass().getSimpleName() + ": resume()");
		return true;
	}

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
