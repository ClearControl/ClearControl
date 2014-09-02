package rtlib.core.memory.map;

public enum MemoryMappedFileAccessMode
{
	ReadOnly(0), ReadWrite(1), Private(2);

	private final int mValue;

	private MemoryMappedFileAccessMode(final int pValue)
	{
		mValue = pValue;
	}

	public int getValue()
	{
		return mValue;
	}
}
