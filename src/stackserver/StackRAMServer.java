package stackserver;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;

import recycling.Recycler;
import stack.Stack;
import variable.VariableInterface;
import variable.bundle.VariableBundle;

public class StackRAMServer	implements
														StackSinkInterface,
														StackSourceInterface
{

	ArrayList<Stack> mStackList = new ArrayList<Stack>();
	TLongArrayList mStackTimePointList = new TLongArrayList();

	protected final VariableBundle mMetaDataVariableBundle = new VariableBundle("MetaData");

	public StackRAMServer()
	{
		super();
	}

	@Override
	public boolean update()
	{
		return true;
	}

	@Override
	public long getNumberOfStacks()
	{
		return mStackList.size();
	}

	@Override
	public void setStackRecycler(final Recycler<Stack> pStackRecycler)
	{
	}

	@Override
	public Stack getStack(final long pStackIndex)
	{
		return mStackList.get((int) pStackIndex);
	}

	@Override
	public long getStackTimeStampInNanoseconds(final long pStackIndex)
	{
		return mStackTimePointList.get((int) pStackIndex);
	}

	@Override
	public boolean appendStack(final Stack pStack)
	{
		mStackTimePointList.add(pStack.mTimeStampInNanoseconds);
		return mStackList.add(pStack);
	}

	@Override
	public void addMetaDataVariable(final String pPrefix,
																	final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundle.addVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mMetaDataVariableBundle.removeAllVariables();
	}

	@Override
	public void removeMetaDataVariable(final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundle.removeVariable(pVariable);

	}

}
