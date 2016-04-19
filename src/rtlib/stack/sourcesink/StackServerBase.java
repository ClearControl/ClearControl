package rtlib.stack.sourcesink;

import java.io.IOException;
import java.util.HashMap;

import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.stack.StackRequest;

public abstract class StackServerBase implements AutoCloseable
{
	protected final TLongDoubleHashMap mStackIndexToTimeStampInSecondsMap = new TLongDoubleHashMap();
	protected final TLongLongHashMap mStackIndexToBinaryFilePositionMap = new TLongLongHashMap();
	protected final HashMap<Long, StackRequest> mStackIndexToStackRequestMap = new HashMap<Long, StackRequest>();

	public StackServerBase() throws IOException
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
