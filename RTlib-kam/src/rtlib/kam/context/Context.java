package rtlib.kam.context;

import java.nio.ByteOrder;

import rtlib.kam.queues.Queue;

import com.nativelibs4java.opencl.CLQueue;

import coremem.interfaces.HasPeer;
import coremem.rgc.Freeable;

public interface Context<T> extends HasPeer<T>, Freeable
{

	ByteOrder getByteOrder();

	Queue<CLQueue> getDefaultQueue();

	int getMaxWorkVolume();

	int[] getMaxThreadNDRange();

}
