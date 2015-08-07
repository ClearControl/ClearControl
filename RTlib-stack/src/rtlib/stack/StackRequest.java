package rtlib.stack;

import java.util.Arrays;

import coremem.recycling.RecyclerRequest;
import net.imglib2.type.NativeType;

public class StackRequest<T extends NativeType<T>>	implements
													RecyclerRequest
{

	private final T mType;
	private final long[] mDimensions;

	public StackRequest(final T pType, final long... pDimensions)
	{
		mType = pType;
		mDimensions = Arrays.copyOf(pDimensions, pDimensions.length);
	}

	public static <LT extends NativeType<LT>> StackRequest<LT> build(	final LT pType,
																		final long... pDimensions)
	{
		return new StackRequest<LT>(pType, pDimensions);
	}

	public static <LT extends NativeType<LT>> StackRequest<LT> buildFrom(final StackInterface<LT, ?> pStack)
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

	public T getType()
	{
		return mType;
	}

	@Override
	public String toString()
	{
		return String.format(	"StackRequest [mType=%s, mDimensions=%s]",
								mType,
								Arrays.toString(mDimensions));
	}

}
