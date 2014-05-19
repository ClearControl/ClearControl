package rtlib.kam.memory.impl.java;

import rtlib.kam.memory.MemoryTyped;

public interface JavaAccessible<T> extends MemoryTyped
{
	Object getArray();

	Class<T> getDataType();

	long getLengthInElements();
}
