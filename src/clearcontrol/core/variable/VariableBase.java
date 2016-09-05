package clearcontrol.core.variable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class VariableBase<O>
{

	private String mVariableName;

	private final CopyOnWriteArrayList<VariableSetListener<O>> mVariableSetListeners = new CopyOnWriteArrayList<VariableSetListener<O>>();
	private final CopyOnWriteArrayList<VariableEdgeListener<O>> mVariableEdgeListeners = new CopyOnWriteArrayList<VariableEdgeListener<O>>();
	private final CopyOnWriteArrayList<VariableGetListener<O>> mVariableGetListeners = new CopyOnWriteArrayList<VariableGetListener<O>>();

	public VariableBase(final String pVariableName)
	{
		super();
		mVariableName = pVariableName;
	}

	public void addListener(final VariableListener<O> pVariableListener)
	{
		if (!mVariableSetListeners.contains(pVariableListener))
			mVariableSetListeners.add(pVariableListener);
		if (!mVariableGetListeners.contains(pVariableListener))
			mVariableGetListeners.add(pVariableListener);
	}

	public void removeListener(final VariableListener<O> pVariableListener)
	{
		mVariableSetListeners.remove(pVariableListener);
		mVariableGetListeners.remove(pVariableListener);
	}

	public void addSetListener(final VariableSetListener<O> pVariableSetListener)
	{
		if (!mVariableSetListeners.contains(pVariableSetListener))
			mVariableSetListeners.add(pVariableSetListener);
	}

	public void addEdgeListener(final VariableEdgeListener<O> pVariableEdgeListener)
	{
		if (!mVariableEdgeListeners.contains(pVariableEdgeListener))
			mVariableEdgeListeners.add(pVariableEdgeListener);
	}

	public void addGetListener(final VariableGetListener<O> pVariableGetListener)
	{
		if (!mVariableGetListeners.contains(pVariableGetListener))
			mVariableGetListeners.add(pVariableGetListener);
	}

	public void removeSetListener(final VariableSetListener<O> pVariableSetListener)
	{
		mVariableSetListeners.remove(pVariableSetListener);
	}

	public void removeGetListener(final VariableGetListener<O> pVariableGetListener)
	{
		mVariableGetListeners.remove(pVariableGetListener);
	}

	public void removeEdgeListener(final VariableEdgeListener<O> pVariableEdgeListener)
	{
		mVariableEdgeListeners.remove(pVariableEdgeListener);
	}

	public void removeAllSetListeners()
	{
		mVariableSetListeners.clear();
	}

	public void removeAllGetListeners()
	{
		mVariableGetListeners.clear();
	}

	public void removeAllListeners()
	{
		mVariableSetListeners.clear();
		mVariableGetListeners.clear();
		mVariableEdgeListeners.clear();
	}

	public CopyOnWriteArrayList<VariableSetListener<O>> getVariableSetListeners()
	{
		return mVariableSetListeners;
	}

	public CopyOnWriteArrayList<VariableEdgeListener<O>> getVariableEdgeListeners()
	{
		return mVariableEdgeListeners;
	}

	public CopyOnWriteArrayList<VariableGetListener<O>> getVariableGetListeners()
	{
		return mVariableGetListeners;
	}

	public void notifyListenersOfSetEvent(final O pCurentValue,
																				final O pNewValue)
	{
		for (final VariableSetListener<O> lVariableListener : getVariableSetListeners())
		{
			lVariableListener.setEvent(pCurentValue, pNewValue);
		}
	}

	public void notifyListenersOfEdgeEvent(	final O pCurentValue,
																					final O pNewValue)
	{
		for (final VariableEdgeListener<O> lVariableListener : getVariableEdgeListeners())
		{
			lVariableListener.fire(pNewValue);
		}
	}

	public void notifyListenersOfGetEvent(final O pCurrentValue)
	{
		for (final VariableGetListener<O> lVariableListener : getVariableGetListeners())
		{
			lVariableListener.getEvent(pCurrentValue);
		}
	}

	/**
	 * Waits for the _exact_ object reference to be set to this variable. This is
	 * rarely useful and it's often safer to use instead waitForEqualsTo(...).
	 * This method does not poll, and therefore is the best way to wait for
	 * events. (no CPU hogging)
	 * 
	 * @param pValueToWaitFor
	 *          value to wait for
	 * @param pTimeOut
	 *          timeout
	 * @param pTimeUnit
	 *          timeout unit
	 */
	public boolean waitForSameAs(	final O pValueToWaitFor,
																final long pTimeOut,
																final TimeUnit pTimeUnit)
	{
		CountDownLatch lCountDownLatch = new CountDownLatch(1);

		VariableSetListener<O> lListener = (o, n) -> {
			if (n == pValueToWaitFor)
			{
				lCountDownLatch.countDown();
			}
		};

		addSetListener(lListener);
		try
		{
			try
			{
				return lCountDownLatch.await(pTimeOut, pTimeUnit);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			removeSetListener(lListener);
		}
		return false;
	}

	/**
	 * Waits for the an object that equals the given object. This method does not
	 * poll, and therefore is the best way to wait for events. (no CPU hogging)
	 * 
	 * @param pValueToWaitFor
	 *          value to wait for
	 * @param pTimeOut
	 *          timeout
	 * @param pTimeUnit
	 *          timeout unit
	 */
	public boolean waitForEqualsTo(	final O pValueToWaitFor,
																	final long pTimeOut,
																	final TimeUnit pTimeUnit)
	{
		CountDownLatch lCountDownLatch = new CountDownLatch(1);

		VariableSetListener<O> lListener = (o, n) -> {
			if (n.equals(pValueToWaitFor))
			{
				lCountDownLatch.countDown();
			}
		};

		addSetListener(lListener);
		try
		{
			return lCountDownLatch.await(pTimeOut, pTimeUnit);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			removeSetListener(lListener);
		}
		return false;
	}

	/**
	 * Waits for the an object that equals the given object. This method does not
	 * poll, and therefore is the best way to wait for events. (no CPU hogging)
	 * 
	 * @param pValueToWaitFor
	 *          value to wait for
	 * @param pTimeOut
	 *          timeout
	 * @param pTimeUnit
	 *          timeout unit
	 */
	public boolean waitForEdge(	final O pNewValueToWaitFor,
															final long pTimeOut,
															final TimeUnit pTimeUnit)
	{
		CountDownLatch lCountDownLatch = new CountDownLatch(1);

		VariableEdgeListener<O> lListener = (n) -> {
			if (n.equals(pNewValueToWaitFor))
			{
				lCountDownLatch.countDown();
			}
		};

		addEdgeListener(lListener);
		try
		{
			return lCountDownLatch.await(pTimeOut, pTimeUnit);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			removeEdgeListener(lListener);
		}
		return false;
	}

	public String getName()
	{
		return mVariableName;
	}

	public void setVariableName(final String variableName)
	{
		mVariableName = variableName;
	}

	public abstract O get();

}
