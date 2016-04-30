package clearcontrol.stack.sourcesink;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.units.Magnitude;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.RecyclerInterface;

public class RandomStackSource implements StackSourceInterface
{

	private RecyclerInterface<StackInterface, StackRequest> mStackBasicRecycler;
	private long mWidth;
	private long mHeight;
	private long mDepth;

	public RandomStackSource(	long pWidth,
														long pHeight,
														long pDepth,
														final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
	{
		mWidth = pWidth;
		mHeight = pHeight;
		mDepth = pDepth;
		mStackBasicRecycler = pStackRecycler;
	}

	@Override
	public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
	{
		mStackBasicRecycler = pStackRecycler;
	}

	@Override
	public boolean update()
	{
		return true;
	}

	@Override
	public long getNumberOfStacks()
	{
		return Long.MAX_VALUE;
	}

	@Override
	public double getStackTimeStampInSeconds(long pStackIndex)
	{
		return Magnitude.nano2unit(System.nanoTime());
	}

	@Override
	public StackInterface getStack(final long pStackIndex)
	{
		return getStack(pStackIndex, 1, TimeUnit.NANOSECONDS);
	}

	@Override
	public StackInterface getStack(	final long pStackIndex,
																	long pTime,
																	TimeUnit pTimeUnit)
	{
		if (mStackBasicRecycler == null)
		{
			return null;
		}
		try
		{
			final StackRequest lStackRequest = StackRequest.build(mWidth,
																														mHeight,
																														mDepth);

			final StackInterface lStack = mStackBasicRecycler.getOrWait(pTime,
																																	pTimeUnit,
																																	lStackRequest);

			if (lStack.getContiguousMemory() != null)
			{
				final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lStack.getContiguousMemory());
				lContiguousBuffer.rewind();
				for (int z = 0; z < mDepth; z++)
				{
					for (int y = 0; y < mHeight; y++)
					{
						for (int x = 0; x < mWidth; x++)
						{
							final short lValue = (short) (pStackIndex + x ^ y ^ z);
							lContiguousBuffer.writeShort(lValue);
						}
					}
				}

			}

			lStack.setTimeStampInNanoseconds(System.nanoTime());
			lStack.setIndex(pStackIndex);

			return lStack;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}

	}

}
