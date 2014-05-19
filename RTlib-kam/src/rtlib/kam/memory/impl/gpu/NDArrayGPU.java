package rtlib.kam.memory.impl.gpu;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.MemoryTyped;
import rtlib.kam.memory.NDStructured;

import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLMem.Usage;

public class NDArrayGPU<T> extends BufferGPU<T>	implements
																									MemoryTyped,
																									NDStructured,
																									SizedInBytes,
																									Freeable

{
	protected long[] mDimensions;

	public NDArrayGPU(ContextGPU pOpenCLContext,
												final Class<T> pElementType,
												final boolean pRead,
												final boolean pWrite,
												long... pDimensions)
	{
		super(pOpenCLContext,
					pElementType,
					getLength(pDimensions),
					pRead,
					pWrite);
		mDimensions = pDimensions;

		Usage lMemoryUsage = getMemoryUsage();

		mCLBuffer = mOpenCLContext.getPeer()
															.createBuffer(lMemoryUsage,
																						pElementType,
																						mLengthInElements);

	}

	private static long getLength(long[] pDimensions)
	{
		long lLength = 1;
		for (int i = 0; i < pDimensions.length; i++)
		{
			lLength *= pDimensions[i];
		}
		return lLength;
	}

	@Override
	protected CLMem.Usage getMemoryUsage()
	{
		CLMem.Usage lUsage = null;
		if (mRead && mWrite)
			lUsage = CLMem.Usage.InputOutput;
		if (mRead)
			lUsage = CLMem.Usage.Input;
		else if (mWrite)
			lUsage = CLMem.Usage.Output;
		return lUsage;
	}



	@Override
	public long getDimension()
	{
		return mDimensions.length - 1;
	}

	@Override
	public long getSizeAlongDimension(int pDimensionIndex)
	{
		return mDimensions[pDimensionIndex];
	}

	public long getWidth()
	{
		if (getDimension() < 1)
			return 1;
		else
			return mDimensions[1];
	}

	public long getHeight()
	{
		if (getDimension() < 2)
			return 1;
		else
			return mDimensions[2];
	}

	public long getDepth()
	{
		if (getDimension() < 3)
			return 1;
		else
			return mDimensions[3];
	}

	@Override
	public long getVolume()
	{
		return getLength(mDimensions);
	}

	@Override
	public long getLengthInElements()
	{
		complainIfFreed();
		return getVolume();
	}

	@Override
	public long getSizeInBytes()
	{
		complainIfFreed();
		return getLengthInElements() * mBytesPerElement;
	}

	@Override
	public boolean isVectorized()
	{
		return false;
	}

}
