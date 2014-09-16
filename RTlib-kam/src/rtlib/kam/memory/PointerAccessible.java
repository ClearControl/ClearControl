package rtlib.kam.memory;

public interface PointerAccessible extends
																	MemoryTyped,
																	ByteBufferWrappable,
																	BridJPointerWrappable
{
	long getAddress();

	long getSizeInBytes();
}
