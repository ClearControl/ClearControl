package rtlib.kam.queues.impl.gpu;

import rtlib.kam.queues.Queue;

import com.nativelibs4java.opencl.CLQueue;

public class GPUQueue implements Queue<CLQueue>
{
	CLQueue mQueuePeer;

	public GPUQueue(CLQueue pQueuePeer)
	{
		super();
		mQueuePeer = pQueuePeer;
	}

	@Override
	public CLQueue getPeer()
	{
		return mQueuePeer;
	}

	@Override
	public void waitForCompletion()
	{
		mQueuePeer.finish();
	}

}
