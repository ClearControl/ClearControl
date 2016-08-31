package clearcontrol.core.concurrent.timing;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public interface WaitingInterface
{

	/**
	 * Waits until call to Callable returns true.
	 * 
	 * @param pCallable
	 * @return last boolean state returned
	 */
	default public Boolean waitFor(Callable<Boolean> pCallable)
	{
		return waitFor(Long.MAX_VALUE, TimeUnit.DAYS, pCallable);
	}

	/**
	 * Waits until call to Callable returns true.
	 * 
	 * @param pTimeOut
	 *          time out
	 * @param pTimeUnit
	 *          time out unit
	 * @param pCallable
	 *          callable returning boolean state
	 * @return last boolean state returned
	 */
	default public Boolean waitFor(	Long pTimeOut,
																	TimeUnit pTimeUnit,
																	Callable<Boolean> pCallable)
	{

		synchronized (this)
		{
			try
			{
				AtomicLong lCounter = new AtomicLong();
				long lTimeOutInMillis = pTimeUnit == null	? 0
																									: pTimeUnit.toMillis(pTimeOut);
				while (!pCallable.call() && (pTimeOut == null || lCounter.incrementAndGet() < lTimeOutInMillis))
				{
					try
					{
						wait(1);
					}
					catch (InterruptedException e)
					{
					}
				}
				return pCallable.call();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
