package stackserver;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;

import stack.Stack;

import ndarray.InterfaceNDArray;

public class StackRAMServer	implements
														StackSinkInterface,
														StackSourceInterface
{

	ArrayList<Stack> mStackList = new ArrayList<Stack>();
	TLongArrayList mStackTimePointList = new TLongArrayList();

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
	public Stack getStack(	final long pStackIndex,
																		final Stack pStack)
	{
		return mStackList.get((int) pStackIndex);
	}

	@Override
	public long getStackTimeStampInNanoseconds(final long pStackIndex)
	{
		return mStackTimePointList.get((int) pStackIndex);
	}

	@Override
	public boolean appendStack(	final Stack pStack)
	{
		mStackTimePointList.add(pStack.timestampns);
		return mStackList.add(pStack);
	}

}
