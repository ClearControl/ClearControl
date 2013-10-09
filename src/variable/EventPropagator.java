package variable;

import java.util.ArrayList;

public class EventPropagator
{
	private static final ThreadLocal<EventPropagator> sEventPropagatorThreadLocal = new ThreadLocal<EventPropagator>();

	public static final EventPropagator getEventPropagator()
	{
		EventPropagator lEventPropagator = sEventPropagatorThreadLocal.get();
		if (lEventPropagator == null)
		{
			lEventPropagator = new EventPropagator();
			sEventPropagatorThreadLocal.set(lEventPropagator);
		}
		return lEventPropagator;
	}

	public static final void clear()
	{
		getEventPropagator().mTraversedObjectList.clear();
	}

	public static final void add(final Object pObject)
	{
		getEventPropagator().mTraversedObjectList.add(pObject);
	}

	public static final boolean hasBeenTraversed(final Object pObject)
	{
		return getEventPropagator().mTraversedObjectList.contains(pObject);
	}

	public static final boolean hasNotBeenTraversed(final Object pObject)
	{
		return !getEventPropagator().mTraversedObjectList.contains(pObject);
	}

	public static final ArrayList<Object> getListOfTraversedObjects()
	{
		return getEventPropagator().mTraversedObjectList;
	}
	
	private final ArrayList<Object> mTraversedObjectList = new ArrayList<Object>();

	public EventPropagator()
	{
		super();
	}
	
	

}
