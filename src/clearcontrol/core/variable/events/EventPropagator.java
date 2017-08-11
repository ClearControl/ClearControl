package clearcontrol.core.variable.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Event propagator.
 * 
 * This is used for example to prevent variable updates to pass twice through
 * the same variable -- and thus to go on forever -- which is not good.
 *
 * @author royer
 */
public class EventPropagator
{
  private static final ThreadLocal<EventPropagator> sEventPropagatorThreadLocal =
                                                                                new ThreadLocal<EventPropagator>();

  /**
   * Returns a thread-local event propagator.
   * 
   * @return thread-local event propagator
   */
  public static final EventPropagator getEventPropagator()
  {
    EventPropagator lEventPropagator =
                                     sEventPropagatorThreadLocal.get();
    if (lEventPropagator == null)
    {
      lEventPropagator = new EventPropagator();
      sEventPropagatorThreadLocal.set(lEventPropagator);
    }
    return lEventPropagator;
  }

  /**
   * Clears the traversed object list
   */
  public static final void clear()
  {
    getEventPropagator().mTraversedObjectList.clear();
  }

  /**
   * Adds the given object to the list of traversed objects.
   * 
   * @param pObject
   *          traversed object to add
   */
  public static final void add(final Object pObject)
  {
    getEventPropagator().mTraversedObjectList.add(pObject);
  }

  /**
   * Returns true if the given object has already been traversed.
   * 
   * @param pObject
   *          object to test for traversal
   * @return true if traversed
   */
  public static final boolean hasBeenTraversed(final Object pObject)
  {
    return getEventPropagator().mTraversedObjectList.contains(pObject);
  }

  /**
   * Returns true if the given object has not been traversed.
   * 
   * @param pObject
   *          object to test for traversal
   * @return true if not yet traversed
   */
  public static final boolean hasNotBeenTraversed(final Object pObject)
  {
    return !getEventPropagator().mTraversedObjectList.contains(pObject);
  }

  /**
   * Returns the list of traversed objects
   * 
   * @return list of traversed objects
   */
  public static final ArrayList<Object> getListOfTraversedObjects()
  {
    return getEventPropagator().mTraversedObjectList;
  }

  /**
   * Returns the list of traversed objects
   * 
   * @return list of traversed objects
   */
  public static final ArrayList<Object> getCopyOfListOfTraversedObjects()
  {
    return new ArrayList<Object>(getEventPropagator().mTraversedObjectList);
  }

  /**
   * Sets the list of traversed objects.
   * 
   * @param pListOfTraversedObjects
   *          new list of traversed objects.
   */
  public static void setListOfTraversedObjects(final List<Object> pListOfTraversedObjects)
  {
    final ArrayList<Object> lTraversedObjectList =
                                                 getEventPropagator().mTraversedObjectList;
    lTraversedObjectList.clear();
    lTraversedObjectList.addAll(pListOfTraversedObjects);
  }

  /**
   * Adds all the elements of the given list to the list of traversed objects.
   * 
   * @param pListOfTraversedObjects
   *          list of traversed objects
   */
  public static void addAllToListOfTraversedObjects(final Collection<?> pListOfTraversedObjects)
  {
    final ArrayList<Object> lTraversedObjectList =
                                                 getEventPropagator().mTraversedObjectList;
    lTraversedObjectList.addAll(pListOfTraversedObjects);
  }

  private final ArrayList<Object> mTraversedObjectList =
                                                       new ArrayList<Object>();

  EventPropagator()
  {
    super();
  }

}
