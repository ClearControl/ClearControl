package clearcontrol.core.collection.weak;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * List that holds weak references to objects added. If the last reference of an
 * object is. This list uses internally a CopyOnWriteArrayList, and is thus
 * thread-safe.
 * 
 * @author royer
 *
 * @param <T>
 *          type of object that this list holds.
 */
public class WeakArrayList<T> extends AbstractList<T>
{

  private CopyOnWriteArrayList<WeakReference<T>> items;

  /**
   * Instantiates an empty weak array list
   */
  public WeakArrayList()
  {
    items = new CopyOnWriteArrayList<WeakReference<T>>();
  }

  /**
   * Instantiates a weak array list and initializes it with a given collection
   * 
   * @param pCollection
   *          collection
   */
  public WeakArrayList(Collection<T> pCollection)
  {
    items = new CopyOnWriteArrayList<WeakReference<T>>();
    addAll(0, pCollection);
  }

  @Override
  public void add(int index, T element)
  {
    removeReleased();
    items.add(index, new WeakReference<T>(element));
  }

  @Override
  public Iterator<T> iterator()
  {
    return new WeakListIterator();
  }

  @Override
  public int size()
  {
    removeReleased();
    return items.size();
  }

  @Override
  public T get(int index)
  {
    return items.get(index).get();
  }

  private void removeReleased()
  {
    try
    {
      for (Iterator<WeakReference<T>> it =
                                         items.iterator(); it.hasNext();)
      {
        WeakReference<T> ref = it.next();
        if (ref.get() == null)
          items.remove(ref);
      }
    }
    catch (Throwable e)
    {
      // we don't ever want to have this cleanup mess up.
      e.printStackTrace();
    }
  }

  private class WeakListIterator implements Iterator<T>
  {
    private int n;
    private int i;

    public WeakListIterator()
    {
      n = size();
      i = 0;
    }

    @Override
    public boolean hasNext()
    {
      return i < n;
    }

    @Override
    public T next()
    {
      return get(i++);
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }

  }

}
