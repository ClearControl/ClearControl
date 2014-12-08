package rtlib.kam.context.impl.gpu;

import static java.lang.Math.toIntExact;

import java.nio.ByteOrder;
import java.util.Arrays;

import rtlib.kam.context.Context;
import rtlib.kam.queues.Queue;
import rtlib.kam.queues.impl.gpu.GPUQueue;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;

import coremem.rgc.FreeableBase;

public class ContextGPU extends FreeableBase implements
																						Context<CLContext>
{
	private static final ContextGPU mBestContext = new ContextGPU();

	public static final ContextGPU getBestOpenCLContext()
	{
		return new ContextGPU(getContextWithMostMemory(true, null));
	}

	public static final ContextGPU getBestOpenCLContext(String pHint)
	{
		return new ContextGPU(getContextWithMostMemory(true, pHint));
	}

	private static CLContext getContextWithMostMemory(boolean pGpuDevice,
																										String pHintString)
	{
		long lMaxGlobalMemSize = 0;
		long lMaxMaxComputeUnits = 0;
		CLDevice lDeviceWithMostDeviceMemory = null;

		CLPlatform[] lListPlatforms = JavaCL.listPlatforms();

		for (CLPlatform lCLPlatform : lListPlatforms)
		{
			CLDevice[] lListAllDevices;

			if (pGpuDevice)
				lListAllDevices = lCLPlatform.listGPUDevices(false);
			else
				lListAllDevices = lCLPlatform.listCPUDevices(false);

			for (CLDevice lDevice : lListAllDevices)
				if (pHintString == null || lDevice.getName()
																					.contains(pHintString))
				{
					long lDeviceGlobalMemSize = lDevice.getGlobalMemSize();
					long lDeviceMaxComputeUnits = lDevice.getMaxComputeUnits();
					if (lDeviceGlobalMemSize >= lMaxGlobalMemSize && lDeviceMaxComputeUnits >= lMaxMaxComputeUnits)
					{
						lMaxGlobalMemSize = lDeviceGlobalMemSize;
						lMaxMaxComputeUnits = lDeviceMaxComputeUnits;
						lDeviceWithMostDeviceMemory = lDevice;
					}
				}
		}

		if (lDeviceWithMostDeviceMemory == null && pHintString != null)
			return getContextWithMostMemory(pGpuDevice, null);

		if (lDeviceWithMostDeviceMemory == null && pHintString == null)
			throw new RuntimeException(String.format(	"Cannot find OpenCL %s device (hint: '%s') \n",
																								pGpuDevice ? "gpu"
																													: "cpu",
																								pHintString));

		CLContext lCreateContext = lDeviceWithMostDeviceMemory.getPlatform()
																													.createContext(	null,
																																					lDeviceWithMostDeviceMemory);

		return lCreateContext;
	}

	private CLContext mCLContext;
	private GPUQueue mDefaultQueue;
	private final ByteOrder mByteOrder;

	private ContextGPU(CLContext pCLContext)
	{
		mCLContext = pCLContext;
		mByteOrder = mCLContext.getByteOrder();
		CLQueue lQueuePeer = mCLContext.getDevices()[0].createQueue(mCLContext);
		mDefaultQueue = new GPUQueue(lQueuePeer);
	}

	private ContextGPU()
	{
		this(JavaCL.createBestContext());
	}

	public CLDevice setDeviceIndex(final int pDeviceIndex)
	{
		complainIfFreed();
		System.out.println(Arrays.toString(mCLContext.getDevices()));
		CLDevice lClDevice = mCLContext.getDevices()[pDeviceIndex];
		CLQueue lQueuePeer = lClDevice.createQueue(mCLContext);
		mDefaultQueue = new GPUQueue(lQueuePeer);
		if (mDefaultQueue.getPeer().isOutOfOrder())
			throw new RuntimeException("Default Queue is out of order!");
		return lClDevice;
	}

	@Override
	public final CLContext getPeer()
	{
		complainIfFreed();
		return mCLContext;
	}

	@Override
	public ByteOrder getByteOrder()
	{
		complainIfFreed();
		return mByteOrder;
	}

	@Override
	public Queue<CLQueue> getDefaultQueue()
	{
		complainIfFreed();
		return mDefaultQueue;
	}

	@Override
	public int[] getMaxThreadNDRange()
	{
		complainIfFreed();
		long[] lDimensions = getPeer().getDevices()[0].getMaxWorkItemSizes();
		int[] lDimensionsInt = new int[lDimensions.length];
		for (int i = 0; i < lDimensions.length; i++)
			lDimensionsInt[i] = toIntExact(lDimensions[i]);
		return lDimensionsInt;
	}

	@Override
	public int getMaxWorkVolume()
	{
		complainIfFreed();
		int lVolume = 1;
		for (long lSize : getMaxThreadNDRange())
			lVolume *= lSize;
		return lVolume;
	}

	@Override
	public void free()
	{
		mDefaultQueue.waitForCompletion();
		mDefaultQueue = null;
		mCLContext.release();
	}

	@Override
	public boolean isFree()
	{
		return mDefaultQueue == null;
	}

}
