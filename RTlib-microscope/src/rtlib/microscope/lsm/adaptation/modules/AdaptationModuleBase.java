package rtlib.microscope.lsm.adaptation.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.microscope.lsm.adaptation.Adaptator;

public abstract class AdaptationModuleBase	implements
									AdaptationModuleInterface, AsynchronousExecutorServiceAccess
{
	private int mPriority=1;

	protected ArrayList<Future<?>> mListOfFuturTasks = new ArrayList<>();

	private Adaptator mLSMAdaptator;

	@Override
	public void setAdaptator(Adaptator pLSMAdaptator)
	{
		mLSMAdaptator = pLSMAdaptator;
	}
	
	@Override
	public Adaptator getAdaptator()
	{
		return mLSMAdaptator;
	}
	
	@Override
	public void setPriority(int pPriority)
	{
		mPriority = pPriority;
	}

	@Override
	public int getPriority()
	{
		return mPriority;
	}
	


	@Override
	public abstract boolean step();
	
	@Override
	public boolean isReady()
	{
		boolean lAllDone = true;
		for (Future<?> lTask : mListOfFuturTasks)
			lAllDone &= lTask.isDone();

		return lAllDone;
	}
	
	
	@Override
	public void reset()
	{
		mListOfFuturTasks.clear();
	}

	/**
	 * Interface method implementation
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder lBuilder = new StringBuilder();
		lBuilder.append("AdaptationModuleBase [mPriority=");
		lBuilder.append(mPriority);
		lBuilder.append(", mListOfFuturTasks=");
		lBuilder.append(mListOfFuturTasks);
		lBuilder.append(", mLSMAdaptator=");
		lBuilder.append(mLSMAdaptator);
		lBuilder.append(", isReady()=");
		lBuilder.append(isReady());
		lBuilder.append("]");
		return lBuilder.toString();
	};

	
	




}
