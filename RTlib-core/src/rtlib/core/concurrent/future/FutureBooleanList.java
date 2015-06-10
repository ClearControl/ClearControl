package rtlib.core.concurrent.future;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureBooleanList implements Future<Boolean>
{

	ArrayList<Future<Boolean>> mArrayList = new ArrayList<Future<Boolean>>();

	public FutureBooleanList()
	{
		super();
	}

	public void addFuture(Future<Boolean> pFuture)
	{
		mArrayList.add(pFuture);
	}

	@Override
	public boolean cancel(boolean pMayInterruptIfRunning)
	{
		for (final Future<Boolean> lFuture : mArrayList)
			if (!lFuture.cancel(pMayInterruptIfRunning))
				return false;
		return true;
	}

	@Override
	public boolean isCancelled()
	{
		for (final Future<Boolean> lFuture : mArrayList)
		{
			if (!lFuture.isCancelled())
				return false;
		}
		return true;
	}

	@Override
	public boolean isDone()
	{
		for (final Future<Boolean> lFuture : mArrayList)
		{
			if (!lFuture.isDone())
				return false;
		}
		return true;
	}

	@Override
	public Boolean get() throws InterruptedException,
											ExecutionException
	{
		for (final Future<Boolean> lFuture : mArrayList)
		{
			final Boolean lBoolean = lFuture.get();
			if (lBoolean == null || !lBoolean)
				return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public Boolean get(long pTimeout, TimeUnit pUnit)	throws InterruptedException,
																										ExecutionException,
																										TimeoutException
	{
		for (final Future<Boolean> lFuture : mArrayList)
		{
			if (!lFuture.get(pTimeout, pUnit))
				return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
