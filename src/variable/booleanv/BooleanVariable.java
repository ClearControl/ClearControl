package variable.booleanv;

import variable.doublev.DoubleVariable;

public class BooleanVariable extends DoubleVariable	implements
																										BooleanInputOutputVariableInterface

{

	private BooleanEventListenerInterface mEdgeListener,
			mLowToHighEdgeListener, mHighToLowEdgeListener;

	public BooleanVariable(	final String pVariableName,
													final boolean pInitialState)
	{
		super(pVariableName, boolean2double(pInitialState));
	}

	@Override
	public void setValue(final double pNewValue)
	{
		final boolean lOldBooleanValue = double2boolean(mValue);
		final boolean lNewBooleanValue = double2boolean(pNewValue);

		if (lNewBooleanValue == lOldBooleanValue)
			return;

		if (mEdgeListener != null)
		{
			mEdgeListener.fire(lNewBooleanValue);
		}

		if (mLowToHighEdgeListener != null && lNewBooleanValue)
		{
			mLowToHighEdgeListener.fire(lNewBooleanValue);
		}
		else if (mHighToLowEdgeListener != null && !lNewBooleanValue)
		{
			mHighToLowEdgeListener.fire(lNewBooleanValue);
		}

		super.setValue(pNewValue);
	}

	public void detectEdgeWith(final BooleanEventListenerInterface pEdgeListener)
	{
		mEdgeListener = pEdgeListener;
	}

	public void detectLowToHighEdgeWith(final BooleanEventListenerInterface pLowToHighEdgeListener)
	{
		mLowToHighEdgeListener = pLowToHighEdgeListener;
	}

	public void detectHighToLowEdgeWith(final BooleanEventListenerInterface pHighToLowEdgeListener)
	{
		mHighToLowEdgeListener = pHighToLowEdgeListener;
	}

	public final void setValue(final boolean pNewBooleanValue)
	{
		setValue(boolean2double(pNewBooleanValue));
	}

	public final void toggle(final Object pDoubleEventSource)
	{
		final double lOldValue = getValue();
		final double lNewToggledValue = lOldValue > 0 ? 0 : 1;
		// System.out.println("lOldValue="+lOldValue);
		// System.out.println("lNewToggledValue="+lNewToggledValue);
		setValue(lNewToggledValue);
	}

	@Override
	public void setValue(	final Object pDoubleEventSource,
												final boolean pNewBooleanValue)
	{
		setValue(boolean2double(pNewBooleanValue));
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

}
