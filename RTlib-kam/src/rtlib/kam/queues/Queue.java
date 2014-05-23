package rtlib.kam.queues;

import rtlib.kam.HasPeer;

public interface Queue<T> extends HasPeer<T>
{
	void waitForCompletion();

}
