package stackserver;

import gnu.trove.map.hash.TLongLongHashMap;

import java.io.Closeable;
import java.io.IOException;

import variable.bundle.VariableBundle;

public abstract class StackBase implements Closeable
{
	protected final TLongLongHashMap mStackIndexToTimeStampInNanosecondsMap = new TLongLongHashMap();
	protected final TLongLongHashMap mStackIndexToBinaryFilePositionMap = new TLongLongHashMap();

	public StackBase() throws IOException
	{
		super();
	}

	public abstract VariableBundle getVariableBundle();

	public long getNumberOfStacks()
	{
		return mStackIndexToTimeStampInNanosecondsMap.size();
	}

	public long getStackTimeStampInNanoseconds(final long pStackIndex)
	{
		return mStackIndexToTimeStampInNanosecondsMap.get(pStackIndex);
	}

}
