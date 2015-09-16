package rtlib.microscope.lsm.adaptation.modules;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.microscope.lsm.acquisition.StackAcquisitionInterface;
import rtlib.microscope.lsm.adaptation.LSMAdaptator;

public abstract class AdaptationModuleBase	implements
									AdaptationModuleInterface, AsynchronousExecutorServiceAccess
{
	private int mPriority=1;

	protected ArrayList<Future<?>> mListOfFuturTasks = new ArrayList<>();

	private LSMAdaptator mLSMAdaptator;

	@Override
	public void setAdaptator(LSMAdaptator pLSMAdaptator)
	{
		mLSMAdaptator = pLSMAdaptator;
	}
	
	@Override
	public LSMAdaptator getAdaptator()
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
	public void clearReady()
	{
		mListOfFuturTasks.clear();
	}
	
	




}
