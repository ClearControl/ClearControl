package rtlib.stack.server;

import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongLongHashMap;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

import rtlib.core.variable.bundle.VariableBundle;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;

public abstract class StackBase<T> implements Closeable
{
	protected final TLongDoubleHashMap mStackIndexToTimeStampInSecondsMap = new TLongDoubleHashMap();
	protected final TLongLongHashMap mStackIndexToBinaryFilePositionMap = new TLongLongHashMap();
	protected final HashMap<Long, StackRequest<Stack<T>>> mStackIndexToStackRequestMap = new HashMap<Long, StackRequest<Stack<T>>>();

	public StackBase() throws IOException
	{
		super();
	}

	public abstract VariableBundle getMetaDataVariableBundle();

	public long getNumberOfStacks()
	{
		return mStackIndexToTimeStampInSecondsMap.size();
	}

	public double getStackTimeStampInSeconds(final long pStackIndex)
	{
		return mStackIndexToTimeStampInSecondsMap.get(pStackIndex);
	}

}
