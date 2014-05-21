package rtlib.stack.processor;

import rtlib.core.variable.booleanv.BooleanVariable;

public abstract class StackProcessorBase implements
																				StackProcessorInterface
{
	private final BooleanVariable mIsActiveVariable;
	private final String mProcessorName;

	public StackProcessorBase(final String pProcessorName)
	{
		super();
		mProcessorName = pProcessorName;
		mIsActiveVariable = new BooleanVariable(pProcessorName, false);
	}

	@Override
	public boolean isActive()
	{
		return mIsActiveVariable.getBooleanValue();
	}

	@Override
	public void setActive(final boolean pIsActive)
	{
		mIsActiveVariable.setValue(pIsActive);
	}

	public BooleanVariable getIsActiveBooleanVariable()
	{
		return mIsActiveVariable;
	}

	public String getName()
	{
		return mProcessorName;
	}

	@Override
	public String toString()
	{
		return String.format(	"StackProcessor [mProcessorName=%s,mIsActiveVariable=%s]",
													mProcessorName,
													mIsActiveVariable);
	}

}
