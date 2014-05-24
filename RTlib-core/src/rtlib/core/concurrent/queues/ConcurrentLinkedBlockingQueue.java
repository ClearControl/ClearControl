package rtlib.core.concurrent.queues;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentLinkedBlockingQueue<E> extends
																							ConcurrentLinkedQueue<E> implements
																																			BlockingQueue<E>
{
	private static final long serialVersionUID = 1L;

	final long mCapacity;
	AtomicInteger mLength = new AtomicInteger();

	public ConcurrentLinkedBlockingQueue(final int pCapacity)
	{
		super();
		mCapacity = pCapacity;
	}

	public ConcurrentLinkedBlockingQueue(Collection<? extends E> pC)
	{
		super(pC);
		mCapacity = pC.size();
	}

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions, returning {@code true}
	 * upon success and throwing an {@code IllegalStateException} if no space is
	 * currently available.
	 *
	 * @param e
	 *          the element to add
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws IllegalStateException
	 *           if the element cannot be added at this time due to capacity
	 *           restrictions
	 * @throws ClassCastException
	 *           if the class of the specified element prevents it from being
	 *           added to this queue
	 * @throws NullPointerException
	 *           if the specified element is null and this queue does not permit
	 *           null elements
	 * @throws IllegalArgumentException
	 *           if some property of this element prevents it from being added to
	 *           this queue
	 */
	@Override
	public boolean add(E event)
	{
		boolean lAdd = super.add(event);
		mLength.incrementAndGet();
		return lAdd;
	}

	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions. When using a
	 * capacity-restricted queue, this method is generally preferable to
	 * {@link #add}, which can fail to insert an element only by throwing an
	 * exception.
	 *
	 * @param e
	 *          the element to add
	 * @return {@code true} if the element was added to this queue, else
	 *         {@code false}
	 * @throws ClassCastException
	 *           if the class of the specified element prevents it from being
	 *           added to this queue
	 * @throws NullPointerException
	 *           if the specified element is null and this queue does not permit
	 *           null elements
	 * @throws IllegalArgumentException
	 *           if some property of this element prevents it from being added to
	 *           this queue
	 */
	@Override
	public boolean offer(E event)
	{
		boolean lOffer = super.offer(event);
		if (lOffer)
			mLength.incrementAndGet();
		return lOffer;
	};

	/**
	 * Retrieves and removes the head of this queue. This method differs from
	 * {@link #poll poll} only in that it throws an exception if this queue is
	 * empty.
	 *
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *           if this queue is empty
	 */
	@Override
	public E remove()
	{
		E lRemove = super.remove();
		mLength.decrementAndGet();
		return lRemove;
	}

	/**
	 * Retrieves and removes the head of this queue, or returns {@code null} if
	 * this queue is empty.
	 *
	 * @return the head of this queue, or {@code null} if this queue is empty
	 */
	@Override
	public E poll()
	{
		E lPoll = super.poll();
		if (lPoll != null)
			mLength.decrementAndGet();
		return lPoll;
	}

	/**
	 * Retrieves, but does not remove, the head of this queue. This method differs
	 * from {@link #peek peek} only in that it throws an exception if this queue
	 * is empty.
	 *
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *           if this queue is empty
	 */
	@Override
	public E element()
	{
		return super.element();
	}

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns
	 * {@code null} if this queue is empty.
	 *
	 * @return the head of this queue, or {@code null} if this queue is empty
	 */
	@Override
	public E peek()
	{
		return super.peek();
	}

	@Override
	public void put(E event) throws InterruptedException
	{
		do
		{
			final int lCurrentLength = mLength.get();
			if (lCurrentLength < mCapacity)
			{
				final int lNewLength = lCurrentLength + 1;

				if (mLength.compareAndSet(lCurrentLength, lNewLength))
				{
					super.offer(event);
					return;
				}
			}
			Thread.yield();
		}
		while (true);
	}

	@Override
	public E take() throws InterruptedException
	{
		E result;
		do
		{
			final int lCurrentLength = mLength.get();
			if (lCurrentLength > 0)
			{
				final int lNewLength = lCurrentLength - 1;

				if (mLength.compareAndSet(lCurrentLength, lNewLength))
				{
					while ((result = super.poll()) == null)
					{
						Thread.yield();
					}

					assert (result != null);

					return result;
				}
			}
			Thread.yield();
		}
		while (true);
	}

	@Override
	public int drainTo(Collection<? super E> arg0)
	{
		throw new UnsupportedOperationException("Cannot drain this queue");
	}

	@Override
	public int drainTo(Collection<? super E> arg0, int arg1)
	{
		throw new UnsupportedOperationException("Cannot drain this queue");
	}

	@Override
	public boolean offer(E arg0, long arg1, TimeUnit arg2) throws InterruptedException
	{
		return offer(arg0);
	}

	@Override
	public E poll(long arg0, TimeUnit arg1) throws InterruptedException
	{
		return poll();
	}

	@Override
	public int remainingCapacity()
	{
		return (int) (mCapacity - mLength.get());
	}

}
