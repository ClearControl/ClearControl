package stackserver;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;

import ndarray.InterfaceNDArray;

public class StackRAMServer	implements
														StackSinkInterface,
														StackSourceInterface
{

	ArrayList<InterfaceNDArray> mStackList = new ArrayList<InterfaceNDArray>();
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
	public InterfaceNDArray getStack(	final long pStackIndex,
																		final InterfaceNDArray pStack)
	{
		return mStackList.get((int) pStackIndex);
	}

	@Override
	public long getStackTimeStampInNanoseconds(final long pStackIndex)
	{
		return mStackTimePointList.get((int) pStackIndex);
	}

	@Override
	public boolean appendStack(	final long pTimeStampInNanoseconds,
															final InterfaceNDArray pStack)
	{
		mStackTimePointList.add(pTimeStampInNanoseconds);
		return mStackList.add(pStack);
	}

}
