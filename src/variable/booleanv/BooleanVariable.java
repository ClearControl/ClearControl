package variable.booleanv;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import variable.VariableListener;
import variable.doublev.DoubleVariable;

public class BooleanVariable extends DoubleVariable	implements
																										BooleanInputOutputVariableInterface

{

	private CopyOnWriteArrayList<BooleanEventListenerInterface> mEdgeListenerList;
	private CopyOnWriteArrayList<BooleanEventListenerInterface> mLowToHighEdgeListenerList;
	private CopyOnWriteArrayList<BooleanEventListenerInterface> mHighToLowEdgeListenerList;

	public BooleanVariable(	final String pVariableName,
													final boolean pInitialState)
	{
		super(pVariableName, boolean2double(pInitialState));

		mEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();
		mLowToHighEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();
		mHighToLowEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();

		addListener(new VariableListener<Double>()
		{

			@Override
			public void getEvent(Double pCurrentValue)
			{
			}

			@Override
			public void setEvent(Double pCurrentValue, Double pNewValue)
			{
				final boolean lOldBooleanValue = double2boolean(pCurrentValue);
				final boolean lNewBooleanValue = double2boolean(pNewValue);

				if (lNewBooleanValue == lOldBooleanValue)
					return;

				for (BooleanEventListenerInterface lEdgeListener : mEdgeListenerList)
				{
					lEdgeListener.fire(lNewBooleanValue);
				}

				if (lNewBooleanValue)
				{
					for (BooleanEventListenerInterface lEdgeListener : mLowToHighEdgeListenerList)
					{
						lEdgeListener.fire(lNewBooleanValue);
					}
				}
				else if (!lNewBooleanValue)
				{
					for (BooleanEventListenerInterface lEdgeListener : mHighToLowEdgeListenerList)
					{
						lEdgeListener.fire(lNewBooleanValue);
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

	public final void setValue(final boolean pNewBooleanValue)
	{
		setValue(boolean2double(pNewBooleanValue));
	}

	public final void toggle(final Object pDoubleEventSource)
	{
		final double lOldValue = getValue();
		final double lNewToggledValue = lOldValue > 0 ? 0 : 1;

		setValue(lNewToggledValue);
	}

	@Override
	public void setValue(	final Object pDoubleEventSource,
												final boolean pNewBooleanValue)
	{
		setValue(boolean2double(pNewBooleanValue));
	}

	protected void setBooleanValueInternal(boolean pNewBooleanValue)
	{
		setValueInternal(boolean2double(pNewBooleanValue));
	}

	@Override
	public boolean getBooleanValue()
	{
		final double lValue = getValue();
		final boolean lBooleanValue = double2boolean(lValue);
		return lBooleanValue;
	}

	public static boolean double2boolean(final double pDoubleValue)
	{
		return pDoubleValue > 0;
	}

	public static double boolean2double(final boolean pBooleanValue)
	{
		return pBooleanValue ? 1 : 0;
	}

	public void waitForTrueAndToggle()
	{
		waitForStateAndToggle(true, 1, TimeUnit.MILLISECONDS);
	}

	public void waitForFalseAndToggle()
	{
		waitForStateAndToggle(true, 1, TimeUnit.MILLISECONDS);
	}

	public void waitForStateAndToggle(final boolean pState,
																		final long pMaxPollingPeriod,
																		final TimeUnit pTimeUnit)
	{
		final CountDownLatch lIsTrueSignal = new CountDownLatch(1);
		final BooleanVariable lThis = this;
		BooleanEventListenerInterface lBooleanEventListenerInterface = new BooleanEventListenerInterface()
		{
			@Override
			public void fire(boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue == pState)
				{
					lIsTrueSignal.countDown();
					lThis.setBooleanValueInternal(false);
				}
			}
		};

		addEdgeListener(lBooleanEventListenerInterface);

		while (getBooleanValue() != pState)
		{
			try
			{
				lIsTrueSignal.await(pMaxPollingPeriod, pTimeUnit);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		removeEdgeListener(lBooleanEventListenerInterface);
	}

}
