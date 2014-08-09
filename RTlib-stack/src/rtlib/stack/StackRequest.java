package rtlib.stack;

import java.util.Arrays;

import rtlib.core.recycling.RecyclerRequest;

public class StackRequest<O> implements RecyclerRequest<O>
{

	private final Class<?> mType;
	private final long[] mDimensions;

	/*public StackRequest(final Class<?> pType,
											final long pWidth,
											final long pHeight,
											final long pDepth)
	{
		mType = pType;
		mDimensions = new long[4];
		mDimensions[0] = 1;
		mDimensions[1] = pWidth;
		mDimensions[2] = pHeight;
		mDimensions[3] = pDepth;
	}/**/

	public StackRequest(final Class<?> pType, final long... pDimensions)
	{
		mType = pType;
		mDimensions = Arrays.copyOf(pDimensions, pDimensions.length);
	}

	public static <O> StackRequest<Stack<O>> build(	final Class<O> pType,
																									final long... pDimensions)
	{
		return new StackRequest<Stack<O>>(pType, pDimensions);
	}

	public static <O> StackRequest<Stack<O>> buildFrom(final Stack<O> pStack)
	{
		return new StackRequest<Stack<O>>(pStack.getType(),
																			pStack.getDimensions());
	}

	public long[] getDimensions()
	{
		return Arrays.copyOf(mDimensions, mDimensions.length);
	}

	public long getWidth()
	{
		return mDimensions[1];
	}

	public long getHeight()
	{
		return mDimensions[2];
	}

	public long getDepth()
	{
		return mDimensions[3];
	}

	public Class<?> getType()
	{
		return mType;
	}

}
