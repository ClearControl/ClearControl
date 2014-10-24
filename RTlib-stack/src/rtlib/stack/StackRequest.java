package rtlib.stack;

import java.util.Arrays;

import rtlib.core.recycling.RecyclerRequest;

public class StackRequest<T> implements RecyclerRequest<Stack<T>>
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

	public static <LT> StackRequest<LT> build(final Class<LT> pType,
																									final long... pDimensions)
	{
		return new StackRequest<LT>(pType, pDimensions);
	}

	public static <LT> StackRequest<LT> buildFrom(final Stack<LT> pStack)
	{
		return new StackRequest<LT>(pStack.getType(),
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
