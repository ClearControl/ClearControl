package rtlib.kam.memory.impl.gpu;

import org.bridj.Pointer;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MemoryTyped;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.PointerAccessible;

import com.nativelibs4java.opencl.CLImage2D;
import com.nativelibs4java.opencl.CLImageFormat;
import com.nativelibs4java.opencl.CLMem;

public class Image2DGPU<T> extends ImageGPU<T> implements
																							MemoryTyped,
																							SizedInBytes,
																							Freeable,
																							NDStructured

{

	public Image2DGPU(ContextGPU pOpenCLContext,
										final Class<T> pType,
										final boolean pRead,
										final boolean pWrite,
										final long pWidth,
										final long pHeight)
	{
		super(pOpenCLContext, pType, pRead, pWrite, pWidth, pHeight, 1);

		CLMem.Usage lUsage = getMemoryUsage();
		CLImageFormat lCLImageFormat = getImageFormat();

		mCLImage = mOpenCLContext.getPeer().createImage2D(lUsage,
																											lCLImageFormat,
																											pWidth,
																											pHeight);
	}

	@Override
	public long getDimension()
	{
		return 2;
	}

	@Override
	public void readFromMapped(MappableMemory pMappableMemory)
	{
		final long lAddress = pMappableMemory.map();

		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(lAddress,
																														mElementType,
																														null);
		readFrom(lPointerToAddress, 0, 0, getWidth(), getHeight());
		getCurrentQueue().waitForCompletion();
		pMappableMemory.unmap();
	}

	@Override
	public void writeToMapped(MappableMemory pMappableMemory)
	{
		final long lAddress = pMappableMemory.map();

		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(lAddress,
																														mElementType,
																														null);
		writeTo(lPointerToAddress, 0, 0, getWidth(), getHeight());
		getCurrentQueue().waitForCompletion();
		pMappableMemory.unmap();
	}

	@Override
	public void readFrom(PointerAccessible pPointerAccessible)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAccessible.getAddress(),
																														pPointerAccessible.getSizeInBytes(),
																														null);
		readFrom(lPointerToAddress, 0, 0, getWidth(), getHeight());
	}

	@Override
	public void writeTo(PointerAccessible pPointerAccessible)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAccessible.getAddress(),
																														pPointerAccessible.getSizeInBytes(),
																														null);
		writeTo(lPointerToAddress, 0, 0, getWidth(), getHeight());
	}

	public void read(long pPointerAddress)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAddress,
																														getSizeInBytes(),
																														null);
		writeTo(lPointerToAddress, 0, 0, getWidth(), getHeight());
	}

	public void write(long pPointerAddress)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAddress,
																														getSizeInBytes(),
																														null);
		readFrom(lPointerToAddress, 0, 0, getWidth(), getHeight());
	}

	public void read(Pointer<?> pPointer)
	{
		writeTo(pPointer, 0, 0, getWidth(), getHeight());
	}

	public void write(Pointer<?> pPointer)
	{
		readFrom(pPointer, 0, 0, getWidth(), getHeight());
	}

	public void writeTo(Pointer<?> pPointer,
											long pX,
											long pY,
											long pWidth,
											long pHeight)
	{
		CLImage2D lClImage2D = (CLImage2D) mCLImage;

		lClImage2D.read((getCurrentQueue().getPeer()),
										pX,
										pY,
										pWidth,
										pHeight,
										0,
										pPointer,
										false);
	}

	public void readFrom(	Pointer<?> pPointer,
												long pX,
												long pY,
												long pWidth,
												long pHeight)
	{
		((CLImage2D) mCLImage).write(	(getCurrentQueue().getPeer()),
																	pX,
																	pY,
																	pWidth,
																	pHeight,
																	0,
																	pPointer,
																	false);
	}

	@Override
	public String toString()
	{
		return "Image2DGPU [mOpenCLContext=" + mOpenCLContext
						+ ", mCLImage="
						+ mCLImage
						+ ", mIsFree="
						+ mIsFree
						+ ", mElementType="
						+ mElementType
						+ ", mBytesPerVoxel="
						+ mBytesPerVoxel
						+ ", mRead="
						+ mRead
						+ ", mWrite="
						+ mWrite
						+ ", mWidth="
						+ mWidth
						+ ", mHeight="
						+ mHeight
						+ ", mDepth="
						+ mDepth
						+ ", getDimension()="
						+ getDimension()
						+ ", getMemoryType()="
						+ getMemoryType()
						+ "]";
	}

}
