package rtlib.ocl.processor;

import java.nio.Buffer;
import java.nio.ShortBuffer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLImage3D;
import com.nativelibs4java.opencl.CLImageFormat;
import com.nativelibs4java.opencl.CLMem.Usage;

public class OCLImg3dProcessor extends OCLProcessor
{

	private CLImage3D mInputImage3D;
	private int mInputNx, mInputNy, mInputNz;

	private CLBuffer<Short> mOutputImageCLBuffer;
	private int mOutputNx, mOutputNy, mOutputNz;

	public int getInputNx()
	{
		return mInputNx;
	}

	public int getInputNy()
	{
		return mInputNy;
	}

	public int getInputNz()
	{
		return mInputNz;
	}

	public int getOutputNx()
	{
		return mOutputNx;
	}

	public int getOutputNy()
	{
		return mOutputNy;
	}

	public int getOutputNz()
	{
		return mOutputNz;
	}

	public void allocateCLInputImage(	final int pNx,
										final int pNy,
										final int pNz)
	{
		mInputNx = pNx;
		mInputNy = pNy;
		mInputNz = pNz;

		final CLImageFormat lCLImageFormat = new CLImageFormat(	CLImageFormat.ChannelOrder.R,
																CLImageFormat.ChannelDataType.SignedInt16);

		if (mInputImage3D != null)
			mInputImage3D.release();
		mInputImage3D = mCLContext.createImage3D(	Usage.Input,
													lCLImageFormat,
													pNx,
													pNy,
													pNz);
	}

	public void allocateCLOutputImage(	final int pNx,
										final int pNy,
										final int pNz)
	{
		mOutputNx = pNx;
		mOutputNy = pNy;
		mOutputNz = pNz;

		if (mOutputImageCLBuffer != null)
			mOutputImageCLBuffer.release();
		mOutputImageCLBuffer = mCLContext.createShortBuffer(Usage.Output,
															pNx		* pNy
																	* pNz
																	* 2);

	}

	public CLEvent writeInputImage(final Buffer pBuffer)
	{

		return mInputImage3D.write(	mCLQueue,
									0,
									0,
									0,
									mInputNx,
									mInputNy,
									mInputNz,
									0,
									0,
									pBuffer,
									true);

	}

	public CLEvent readOutputImage2D(final ShortBuffer pShortBuffer)
	{
		return mOutputImageCLBuffer.read(	mCLQueue,
											0,
											mOutputNx * mOutputNy
													* mOutputNz,
											pShortBuffer,
											true);
	}

	public CLEvent readOutputImage3D(final ShortBuffer pShortBuffer)
	{
		return mOutputImageCLBuffer.read(	mCLQueue,
											0,
											mOutputNx * mOutputNy
													* mOutputNz,
											pShortBuffer,
											true);
	}

	public CLImage3D getInputImage3D()
	{
		return mInputImage3D;
	}

	public CLBuffer<Short> getOutputImageBuffer()
	{
		return mOutputImageCLBuffer;
	}

	public CLEvent run3D(final Object... pArgs)
	{
		mCLKernel.setArgs(pArgs);

		final CLEvent lEnqueueNDRange = mCLKernel.enqueueNDRange(	mCLQueue,
																	new int[]
																	{	mInputNx,
																		mInputNy,
																		mInputNz });
		lEnqueueNDRange.waitFor();
		return lEnqueueNDRange;
	}

	public CLEvent run2D(final Object... pArgs)
	{
		mCLKernel.setArgs(pArgs);

		final CLEvent lEnqueueNDRange = mCLKernel.enqueueNDRange(	mCLQueue,
																	new int[]
																	{	mInputNx,
																		mInputNy, });
		lEnqueueNDRange.waitFor();
		return lEnqueueNDRange;
	}

}