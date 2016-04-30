package clearcontrol.microscope;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class StackRecyclerManager
{

	final private ContiguousOffHeapPlanarStackFactory mOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

	final private ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> mRecyclerMap = new ConcurrentHashMap<>();

	public RecyclerInterface<StackInterface, StackRequest> getRecycler(	String pName,
																																			int pMaximumNumberOfAvailableObjects,
																																			int pMaximumNumberOfLiveObjects)
	{

		RecyclerInterface<StackInterface, StackRequest> lRecycler = mRecyclerMap.get(pName);

		if (lRecycler == null)
		{

			lRecycler = new BasicRecycler<>(mOffHeapPlanarStackFactory,
																			pMaximumNumberOfAvailableObjects,
																			pMaximumNumberOfLiveObjects,
																			true);
			mRecyclerMap.put(pName, lRecycler);
		}

		return lRecycler;

	}

	public void clear(String pName)
	{
		mRecyclerMap.remove(pName);
	}

	public void clearAll()
	{
		mRecyclerMap.clear();
	}

}
