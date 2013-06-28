package stackserver;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;

import recycling.Recycler;
import stack.Stack;

public class StackRAMServer	implements
														StackSinkInterface,
														StackSourceInterface
{

	ArrayList<Stack> mStackList = new ArrayList<Stack>();
	TLongArrayList mStackTimePointList = new TLongArrayList();
	private Recycler<Stack> mStackRecycler;

	public StackRAMServer()
	{
		super();
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
		mStackTimePointList.add(pStack.timestampns);
		return mStackList.add(pStack);
	}

}
