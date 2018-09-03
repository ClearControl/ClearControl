package clearcontrol.microscope.stacks;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

/**
 * StackRecyclerManager handle a collection of named stack recyclers used for
 * different purposes. Methods are available to clear all recyclers.
 * 
 * @author royer
 */
public class StackRecyclerManager extends VirtualDevice
{

  final private ContiguousOffHeapPlanarStackFactory mOffHeapPlanarStackFactory =
                                                                               new ContiguousOffHeapPlanarStackFactory();

  final private ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> mRecyclerMap =
                                                                                                        new ConcurrentHashMap<>();

  private boolean mAutoFree = false;

  /**
   * Creates StackRecyclerManager
   */
  public StackRecyclerManager()
  {
    super("Stack Recycler Manager");
  }

  /**
   * Requests a recycler with given characteristics, if it already exists and it
   * has the right characteristics then it is used, otherwise a new one is
   * created.
   * 
   * @param pName
   *          recycler's name
   * @param pMaximumNumberOfLiveObjects
   *          maximum number of live objects
   * @param pMaximumNumberOfAvailableObjects
   *          maximum number of available objects
   * @return requested recycler
   */
  public RecyclerInterface<StackInterface, StackRequest> getRecycler(String pName,
                                                                     int pMaximumNumberOfLiveObjects,
                                                                     int pMaximumNumberOfAvailableObjects)
  {
    RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                              getRecyclerMap().get(pName);

    if (lRecycler == null
        || lRecycler.getMaxNumberOfAvailableObjects() != pMaximumNumberOfAvailableObjects
        || lRecycler.getMaxNumberOfLiveObjects() != pMaximumNumberOfLiveObjects)
    {
      lRecycler = new BasicRecycler<>(mOffHeapPlanarStackFactory,
                                      pMaximumNumberOfLiveObjects,
                                      pMaximumNumberOfAvailableObjects,
                                      mAutoFree);
      getRecyclerMap().put(pName, lRecycler);
      notifyListeners(this);
    }

    // if (lRecycler != null)
    // lRecycler.clearReleased();

    return lRecycler;
  }

  /**
   * Clears recyclers with given name.
   * 
   * @param pName
   *          recycler name
   */
  public void clear(String pName)
  {
    getRecyclerMap().remove(pName);
    notifyListeners(this);
  }

  /**
   * Clears all recyclers.
   */
  public void clearAll()
  {
    getRecyclerMap().clear();
    notifyListeners(this);
  }

  /**
   * Returns recycler map
   * 
   * @return recycler map
   */
  public ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> getRecyclerMap()
  {
    return mRecyclerMap;
  }

  /**
   * Returns underlying stack factory
   * 
   * @return stack factory
   */
  public ContiguousOffHeapPlanarStackFactory getStackFactory()
  {
    return mOffHeapPlanarStackFactory;
  }

}
