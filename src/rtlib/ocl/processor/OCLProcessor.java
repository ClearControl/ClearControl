package rtlib.ocl.processor;

import java.net.URL;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.util.IOUtils;

public class OCLProcessor
{
	public CLContext mCLContext;
	public CLProgram mCLProgram;
	public CLQueue mCLQueue;
	public CLKernel mCLKernel;
	public ByteOrder mCLContextByteOrder;

	public void initCL()
	{
		// initialize the platform and devices OpenCL will use
		// usually chooses the best, i.e. fastest, platform/device/context
		mCLContext = JavaCL.createBestContext();
		// TODO: Question: what happens if two OCLProcessors are created? is
		// this
		// context a reentrant ressource?
		// consider setting at as a static field...
		mCLQueue = mCLContext.createDefaultQueue();
		mCLContextByteOrder = mCLContext.getByteOrder();
		mCLKernel = null;
	}

	public CLContext getContext()
	{
		return mCLContext;
	}

	public void printInfo()
	{
		final CLDevice dev = mCLContext.getDevices()[0];
		System.out.printf("Device name:    \t %s \n", dev);
		System.out.printf("Global Mem size: \t %s \n",
											dev.getGlobalMemSize());
		System.out.printf("Local Mem size: \t %s \n",
											dev.getLocalMemSize());
		System.out.printf("Max compute units: \t %s \n",
											dev.getMaxComputeUnits());

		System.out.printf("Max Workgroup size: \t %s \n",
											dev.getMaxWorkGroupSize());
		System.out.printf("Max Workitem sizes: \t %s \n",
											Arrays.toString(dev.getMaxWorkItemSizes()));
		System.out.printf("Maximal Image size: \t %s x %s x %s\n",
											dev.getImage3DMaxWidth(),
											dev.getImage3DMaxHeight(),
											dev.getImage3DMaxDepth());

		// kernel.getCompileWorkGroupSize().get(dev);

		// System.out.println(Arrays.toString(m.get(dev)));

	}

	public CLKernel compileKernel(final URL url, final String kernelName)
	{

		// Read the program sources and compile them :
		String src = "";
		try
		{
			src = IOUtils.readText(url);
		}
		catch (final Exception e)
		{
			System.err.println("couldn't read program source ");
			e.printStackTrace();
			return null;
		}

		try
		{
			mCLProgram = mCLContext.createProgram(src);
		}
		catch (final Exception e)
		{
			System.err.println("couldn't create program from " + src);
			e.printStackTrace();
			return null;
		}

		try
		{
			mCLKernel = mCLProgram.createKernel(kernelName);
		}
		catch (final Exception e)
		{
			System.err.println("couldn't create kernel '" + kernelName
													+ "'");
			e.printStackTrace();
		}

		return mCLKernel;
	}

}
