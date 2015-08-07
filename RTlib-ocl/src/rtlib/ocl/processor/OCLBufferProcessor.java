package rtlib.ocl.processor;

import java.nio.ShortBuffer;

import org.bridj.Pointer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLMem.Usage;

public class OCLBufferProcessor extends OCLProcessor
{

	private CLBuffer<Short> mInputCLBuffer, mOutputCLBuffer;
	private int Nx, Ny;

	public void allocateCLBuffer(final int Nx0, final int Ny0)
	{
		Nx = Nx0;
		Ny = Ny0;
		mInputCLBuffer = mCLContext.createShortBuffer(	Usage.Input,
														Nx * Ny * 2);
		mOutputCLBuffer = mCLContext.createShortBuffer(	Usage.Output,
														Nx * Ny * 2);
	}

	public CLEvent setInputShortBuffer(final ShortBuffer buf)
	{
		return mInputCLBuffer.write(mCLQueue, buf, true);
	}

	public Pointer<Short> getOutPutShortBuffer()
	{
		return mOutputCLBuffer.read(mCLQueue);

	}

}
