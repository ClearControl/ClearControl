package rtlib.kam.context;

import java.nio.ByteOrder;

import rtlib.core.rgc.Freeable;
import rtlib.kam.HasPeer;
import rtlib.kam.queues.Queue;

import com.nativelibs4java.opencl.CLQueue;

public interface Context<T> extends HasPeer<T>, Freeable
{

	ByteOrder getByteOrder();

	Queue<CLQueue> getDefaultQueue();

	int getMaxWorkVolume();

	int[] getMaxThreadNDRange();

}
