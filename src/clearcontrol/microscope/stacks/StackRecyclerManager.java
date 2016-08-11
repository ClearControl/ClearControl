package clearcontrol.microscope.stacks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.device.change.ChangeListener;
import clearcontrol.device.change.ChangeListeningBase;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

/**
 * StackRecyclerManager handle a colection of named stack recyclers used for
 * different purposes. Methods are available to clear all recyclers.
 * 
 * @author royer
 */
public class StackRecyclerManager extends ChangeListeningBase<StackRecyclerManager>
{

	final private ContiguousOffHeapPlanarStackFactory mOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

	final private ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> mRecyclerMap = new ConcurrentHashMap<>();

	
	/**
	 * Creates StackRecyclerManager
	 */
	public StackRecyclerManager()
	{
		super();
	}


	/**
	 * Requests a recycler with given characteristics, if it already exists and it
	 * has the right characteristics then it is used, otherwise a new one is
	 * created.
	 * 
	 * @param pName recycler's name
	 * @param pMaximumNumberOfLiveObjects maximum number of live objects
	 * @param pMaximumNumberOfAvailableObjects maximum number of available objects
	 * @return requested recycler
	 */
	public RecyclerInterface<StackInterface, StackRequest> getRecycler(	String pName,
																																			int pMaximumNumberOfLiveObjects,
																																			int pMaximumNumberOfAvailableObjects)
	{
		RecyclerInterface<StackInterface, StackRequest> lRecycler = getRecyclerMap().get(pName);

		if (lRecycler == null || lRecycler.getNumberOfAvailableObjects() != pMaximumNumberOfAvailableObjects
				|| lRecycler.getNumberOfLiveObjects() != pMaximumNumberOfLiveObjects)
		{
			lRecycler = new BasicRecycler<>(mOffHeapPlanarStackFactory,
																			pMaximumNumberOfLiveObjects,
																			pMaximumNumberOfAvailableObjects,
																			true);
			getRecyclerMap().put(pName, lRecycler);
			notifyListeners();
		}

		if (lRecycler != null)
			lRecycler.clearReleased();

		return lRecycler;
	}

	/**
	 * Clears recyclers with given name.
	 * 
	 * @param pName
	 */
	public void clear(String pName)
	{
		getRecyclerMap().remove(pName);
		notifyListeners();
	}

	/**
	 * Clears all recyclers.
	 */
	public void clearAll()
	{
		getRecyclerMap().clear();
		notifyListeners();
	}


	public ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> getRecyclerMap()
	{
		return mRecyclerMap;
	}





}
