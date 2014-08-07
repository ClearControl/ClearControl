package rtlib.kam.memory;

public interface PointerAccessible extends
																	MemoryTyped,
																	ByteBufferWrappable
{
	long getAddress();

	long getSizeInBytes();
}
