package rtlib.kam.memory;

import java.nio.ByteBuffer;

public interface ByteBufferWrappable
{
	public ByteBuffer passNativePointerToByteBuffer(Class<?> pTargetClass);
}
