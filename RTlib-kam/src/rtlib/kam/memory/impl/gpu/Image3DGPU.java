package rtlib.kam.memory.impl.gpu;

import org.bridj.Pointer;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MemoryTyped;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.PointerAccessible;
import rtlib.kam.memory.impl.gpu.util.JavaCLUtils;

import com.nativelibs4java.opencl.CLImageFormat;
import com.nativelibs4java.opencl.CLMem;

public class Image3DGPU<T> extends ImageGPU<T> implements
																							MemoryTyped,
																							SizedInBytes,
																							Freeable,
																							NDStructured

{

	public Image3DGPU(ContextGPU pOpenCLContext,
										final Class<T> pType,
										final boolean pRead,
										final boolean pWrite,
										final long pWidth,
										final long pHeight,
										final long pDepth)
	{
		super(pOpenCLContext,
					pType,
					pRead,
					pWrite,
					pWidth,
					pHeight,
					pDepth);

		CLMem.Usage lUsage = getMemoryUsage();
		CLImageFormat lCLImageFormat = getImageFormat();

		mCLImage = mOpenCLContext.getPeer().createImage3D(lUsage,
																											lCLImageFormat,
																											pWidth,
																											pHeight,
																											pDepth);

	}

	@Override
	public long getDimension()
	{
		return 3;
	}

	@Override
	public void readFromMapped(MappableMemory pMappableMemory)
	{
		final long lAddress = pMappableMemory.map();

		Pointer<T> lPointerToAddress = Pointer.pointerToAddress(lAddress,
																														mElementType,
																														null);
		readFrom(	lPointerToAddress,
							0,
							0,
							0,
							getWidth(),
							getHeight(),
							getDepth());
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
		writeTo(lPointerToAddress,
						0,
						0,
						0,
						getWidth(),
						getHeight(),
						getDepth());
		getCurrentQueue().waitForCompletion();
		pMappableMemory.unmap();
	}

	@Override
	public void readFrom(PointerAccessible pPointerAccessible)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAccessible.getAddress(),
																														pPointerAccessible.getSizeInBytes(),
																														null)
																					.as(mElementType);
		;
		readFrom(	lPointerToAddress,
							0,
							0,
							0,
							getWidth(),
							getHeight(),
							getDepth());
	}

	@Override
	public void writeTo(PointerAccessible pPointerAccessible)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAccessible.getAddress(),
																														pPointerAccessible.getSizeInBytes(),
																														null)
																					.as(mElementType);

		writeTo(lPointerToAddress,
						0,
						0,
						0,
						getWidth(),
						getHeight(),
						getDepth());
	}

	public void read(long pPointerAddress)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAddress,
																														getSizeInBytes(),
																														null)
																					.as(mElementType);

		writeTo(lPointerToAddress,
						0,
						0,
						0,
						getWidth(),
						getHeight(),
						getDepth());
	}

	public void write(long pPointerAddress)
	{
		Pointer<?> lPointerToAddress = Pointer.pointerToAddress(pPointerAddress,
																														getSizeInBytes(),
																														null)
																					.as(mElementType);
		readFrom(	lPointerToAddress,
							0,
							0,
							0,
							getWidth(),
							getHeight(),
							getDepth());
	}

	public void read(Pointer<?> pPointer)
	{
		writeTo(pPointer, 0, 0, 0, getWidth(), getHeight(), getDepth());
	}

	public void write(Pointer<?> pPointer)
	{
		readFrom(pPointer, 0, 0, 0, getWidth(), getHeight(), getDepth());
	}

	public void writeTo(Pointer<?> pPointer,
											long pX,
											long pY,
											long pZ,
											long pWidth,
											long pHeight,
											long pDepth)
	{

		JavaCLUtils.readImage3D(mCLImage,
														(getCurrentQueue().getPeer()),
														pPointer,
														pX,
														pY,
														pZ,
														pWidth,
														pHeight,
														pDepth);
	}

	public void readFrom(	Pointer<?> pPointer,
												long pX,
												long pY,
												long pZ,
												long pWidth,
												long pHeight,
												long pDepth)
	{

		JavaCLUtils.writeImage3D(	mCLImage,
															(getCurrentQueue().getPeer()),
															pPointer,
															pX,
															pY,
															pZ,
															pWidth,
															pHeight,
															pDepth);
	}

	@Override
	public String toString()
	{
		return "Image3DGPU [mOpenCLContext=" + mOpenCLContext
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
