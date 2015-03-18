package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.variable.booleanv.BooleanVariable;

public abstract class StackProcessorBase<TI extends NativeType<TI>, AI extends ArrayDataAccess<AI>, TO extends NativeType<TO>, AO extends ArrayDataAccess<AO>>	implements
																																																																StackProcessorInterface<TI, AI, TO, AO>
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
