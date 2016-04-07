package rtlib.core.variable.types.booleanv;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rtlib.core.variable.VariableListener;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class BooleanVariable extends ObjectVariable<Boolean> implements
																														BooleanInputOutputVariableInterface

{

	private CopyOnWriteArrayList<BooleanEventListenerInterface> mEdgeListenerList;
	private CopyOnWriteArrayList<BooleanEventListenerInterface> mLowToHighEdgeListenerList;
	private CopyOnWriteArrayList<BooleanEventListenerInterface> mHighToLowEdgeListenerList;

	public BooleanVariable(	final String pVariableName,
													final boolean pInitialState)
	{
		super(pVariableName, pInitialState);

		mEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();
		mLowToHighEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();
		mHighToLowEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();

		addListener(new VariableListener<Boolean>()
		{

			@Override
			public void getEvent(final Boolean pCurrentValue)
			{
			}

			@Override
			public void setEvent(	final Boolean pCurrentValue,
														final Boolean pNewValue)
			{
				if (pNewValue == pCurrentValue)
				{
					return;
				}

				for (final BooleanEventListenerInterface lEdgeListener : mEdgeListenerList)
				{
					lEdgeListener.fire(pNewValue);
				}

				if (pNewValue)
				{
					for (final BooleanEventListenerInterface lEdgeListener : mLowToHighEdgeListenerList)
					{
						lEdgeListener.fire(pNewValue);
					}
				}
				else if (!pNewValue)
				{
					for (final BooleanEventListenerInterface lEdgeListener : mHighToLowEdgeListenerList)
					{
						lEdgeListener.fire(pNewValue);
					}
				}
			}

		});
	}

	public void addEdgeListener(final BooleanEventListenerInterface pEdgeListener)
	{
		mEdgeListenerList.add(pEdgeListener);
	}

	public void removeEdgeListener(final BooleanEventListenerInterface pEdgeListener)
	{
		mEdgeListenerList.remove(pEdgeListener);
	}

	public void addLowToHighEdgelistener(final BooleanEventListenerInterface pLowToHighEdgeListener)
	{
		mLowToHighEdgeListenerList.add(pLowToHighEdgeListener);
	}

	public void removeLowToHighEdgelistener(final BooleanEventListenerInterface pLowToHighEdgeListener)
	{
		mLowToHighEdgeListenerList.add(pLowToHighEdgeListener);
	}

	public void addHighToLowEdgeWith(final BooleanEventListenerInterface pHighToLowEdgeListener)
	{
		mHighToLowEdgeListenerList.add(pHighToLowEdgeListener);
	}

	public void removeHighToLowEdgeWith(final BooleanEventListenerInterface pHighToLowEdgeListener)
	{
		mHighToLowEdgeListenerList.add(pHighToLowEdgeListener);
	}

	@Override
	public void setValue(boolean pNewValue)
	{
		set(pNewValue);
	}

	public final void toggle()
	{
		final boolean lOldValue = getBooleanValue();
		final boolean lNewToggledValue = !lOldValue;

		setValue(lNewToggledValue);
	}

	public void setEdge(boolean pState)
	{
		setValue(!pState);
		setValue(pState);
	}

	protected void setBooleanValueInternal(final boolean pNewBooleanValue)
	{
		setReferenceInternal(pNewBooleanValue);
	}

	@Override
	public boolean getBooleanValue()
	{
		final Boolean lBooleanValue = get();
		if (lBooleanValue == null)
			return false;
		return lBooleanValue;
	}

	public void waitForTrueAndToggleToFalse()
	{
		waitForStateAndToggle(true, 1, 20000, TimeUnit.MILLISECONDS);
	}

	public void waitForFalseAndToggle()
	{
		waitForStateAndToggle(false, 1, 20000, TimeUnit.MILLISECONDS);
	}

	public void waitForStateAndToggle(final boolean pState,
																		final long pMaxPollingPeriod,
																		final long pTimeOut,
																		final TimeUnit pTimeUnit)
	{
		System.out.println("waitForStateAndToggle");
		final CountDownLatch lIsTrueSignal = new CountDownLatch(1);
		final BooleanVariable lThis = this;
		final BooleanEventListenerInterface lBooleanEventListenerInterface = new BooleanEventListenerInterface()
		{
			@Override
			public void fire(final boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue == pState)
				{
					lIsTrueSignal.countDown();
					lThis.setValue(!pState);
					System.out.println("lThis.setValue(!pState);");
				}
			}
		};

		addEdgeListener(lBooleanEventListenerInterface);

		long lTimeOutCounter = 0;
		while (getBooleanValue() != pState)
		{
			System.out.println("while (getBooleanValue() != pState)");
			try
			{
				if (lIsTrueSignal.await(pMaxPollingPeriod, pTimeUnit))
				{
					System.out.println("System.out.println(while (getBooleanValue() != pState));");
					break;
				}
				else
				{
					System.out.println("lTimeOutCounter += pMaxPollingPeriod;");
					lTimeOutCounter += pMaxPollingPeriod;
					if (lTimeOutCounter >= pTimeOut)
					{
						break;
					}
				}
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		removeEdgeListener(lBooleanEventListenerInterface);

		setValue(!pState);
	}


}
