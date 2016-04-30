package clearcontrol.stack.sourcesink;

import java.io.IOException;
import java.util.HashMap;

import clearcontrol.core.variable.bundle.VariableBundle;
import clearcontrol.stack.StackRequest;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongLongHashMap;

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
