package variable.booleanv;

import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleOutputVariableInterface;
import variable.doublev.DoubleVariable;

public class BooleanVariable extends DoubleVariable	implements
																										BooleanInputOutputVariableInterface

{

	private BooleanEventListenerInterface mEdgeListener,
			mLowToHighEdgeListener, mHighToLowEdgeListener;

	public BooleanVariable(boolean pInitialState)
	{
		super(boolean2double(pInitialState));

		this.sendUpdatesTo(new DoubleInputVariableInterface()
		{
			@Override
			public void setValue(Object pDoubleEventSource, double pNewValue)
			{
				final boolean lOldBooleanValue = double2boolean(mValue);
				final boolean lNewBooleanValue = double2boolean(pNewValue);

				if (lNewBooleanValue == lOldBooleanValue)
					return;

				if (mEdgeListener != null)
				{
					mEdgeListener.fire(this, lNewBooleanValue);
				}

				if (mLowToHighEdgeListener != null && lNewBooleanValue)
				{
					mLowToHighEdgeListener.fire(this, lNewBooleanValue);
				}
				else if (mHighToLowEdgeListener != null && !lNewBooleanValue)
				{
					mHighToLowEdgeListener.fire(this, lNewBooleanValue);
				}
			}
		});
	}

	public void detectEdgeWith(BooleanEventListenerInterface pEdgeListener)
	{
		mEdgeListener = pEdgeListener;
	}

	public void detectLowToHighEdgeWith(BooleanEventListenerInterface pLowToHighEdgeListener)
	{
		mLowToHighEdgeListener = pLowToHighEdgeListener;
	}

	public void detectHighToLowEdgeWith(BooleanEventListenerInterface pHighToLowEdgeListener)
	{
		mHighToLowEdgeListener = pHighToLowEdgeListener;
	}

	public final void setValue(final boolean pNewBooleanValue)
	{
		setValue(this, boolean2double(pNewBooleanValue));
	}

	public final void toggle(Object pDoubleEventSource)
	{
		final double lOldValue = getValue();
		final double lNewToggledValue = lOldValue > 0 ? 0 : 1;
		// System.out.println("lOldValue="+lOldValue);
		// System.out.println("lNewToggledValue="+lNewToggledValue);
		setValue(pDoubleEventSource, lNewToggledValue);
	}

	@Override
	public void setValue(	Object pDoubleEventSource,
												boolean pNewBooleanValue)
	{
		setValue(pDoubleEventSource, boolean2double(pNewBooleanValue));
	}

	@Override
	public boolean getBooleanValue()
	{
		final double lValue = getValue();
		final boolean lBooleanValue = double2boolean(lValue);
		return lBooleanValue;
	}

	public static boolean double2boolean(double pDoubleValue)
	{
		return pDoubleValue > 0;
	}

	public static double boolean2double(boolean pBooleanValue)
	{
		return pBooleanValue ? 1 : 0;
	}

}
