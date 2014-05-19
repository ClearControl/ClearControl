package rtlib.stack.server;

import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongLongHashMap;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

import rtlib.core.variable.bundle.VariableBundle;

public abstract class StackBase implements Closeable
{
	protected final TLongDoubleHashMap mStackIndexToTimeStampInSecondsMap = new TLongDoubleHashMap();
	protected final TLongLongHashMap mStackIndexToBinaryFilePositionMap = new TLongLongHashMap();
	protected final HashMap<Long, Long[]> mStackIndexToStackDimensionsMap = new HashMap<Long, Long[]>();

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
