package rtlib.stack.processor;

import rtlib.core.variable.ObjectVariable;

public abstract class StackProcessorBase implements
																				StackProcessorInterface
{
	private final ObjectVariable<Boolean> mIsActiveVariable;
	private final String mProcessorName;

	public StackProcessorBase(final String pProcessorName)
	{
		super();
		mProcessorName = pProcessorName;
		mIsActiveVariable = new ObjectVariable<Boolean>(pProcessorName,
																										false);
	}

	@Override
	public boolean isActive()
	{
		return mIsActiveVariable.get();
	}

	@Override
	public void setActive(final boolean pIsActive)
	{
		mIsActiveVariable.set(pIsActive);
	}

	public ObjectVariable<Boolean> getIsActiveBooleanVariable()
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
