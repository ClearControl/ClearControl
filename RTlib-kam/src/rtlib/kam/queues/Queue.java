package rtlib.kam.queues;

import coremem.interfaces.HasPeer;

public interface Queue<T> extends HasPeer<T>
{
	void waitForCompletion();

}
