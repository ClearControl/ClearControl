package clearcontrol.stack;

import java.util.Arrays;

import coremem.recycling.RecyclerRequestInterface;

public class StackRequest implements RecyclerRequestInterface
{

	private final long[] mDimensions;

	public StackRequest(final long... pDimensions)
	{
		mDimensions = Arrays.copyOf(pDimensions, pDimensions.length);
	}

	public static StackRequest build(final long... pDimensions)
	{
		return new StackRequest(pDimensions);
	}

	public static StackRequest buildFrom(final StackInterface pStack)
	{
		return new StackRequest(pStack.getDimensions());
	}

	public long[] getDimensions()
	{
		return Arrays.copyOf(mDimensions, mDimensions.length);
	}

	public long getWidth()
	{
		return mDimensions[0];
	}

	public long getHeight()
	{
		return mDimensions[1];
	}

	public long getDepth()
	{
		return mDimensions[2];
	}

	@Override
	public String toString()
	{
		return String.format(	"StackRequest [mDimensions=%s]",
													Arrays.toString(mDimensions));
	}

}
