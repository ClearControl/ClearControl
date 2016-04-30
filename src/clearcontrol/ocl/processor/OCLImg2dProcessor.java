package clearcontrol.ocl.processor;

import java.nio.ShortBuffer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLImage2D;
import com.nativelibs4java.opencl.CLImageFormat;
import com.nativelibs4java.opencl.CLMem.Usage;

public class OCLImg2dProcessor extends OCLProcessor
{
	private CLImage2D mInputImage;
	private CLBuffer<Short> mOutputImageCLBuffer;

	private int mNx, mNy;

	public int getNx()
	{
		return mNx;
	}

	public int getNy()
	{
		return mNy;
	}

	public void allocateCLImage(final int Nx0, final int Ny0)
	{
		mNx = Nx0;
		mNy = Ny0;

		final CLImageFormat fmt = new CLImageFormat(CLImageFormat.ChannelOrder.R,
																								CLImageFormat.ChannelDataType.SignedInt16);

		mInputImage = mCLContext.createImage2D(Usage.Input, fmt, mNx, mNy);
		mOutputImageCLBuffer = mCLContext.createShortBuffer(Usage.Output,
																												mNx * mNy * 2);

	}

	public CLEvent writeInputImage(final ShortBuffer buf)
	{
		return mInputImage.write(mCLQueue, 0, 0, mNx, mNy, 0, buf, true);

	}

	public CLBuffer getOutputImageBuffer()
	{
		return mOutputImageCLBuffer;
	}

	public void run()
	{
		mCLKernel.setArgs(mInputImage, mOutputImageCLBuffer, mNx, mNy);
		final CLEvent evt = mCLKernel.enqueueNDRange(mCLQueue, new int[]
		{ mNx, mNy });
		evt.waitFor();
	}

}