package rtlib.kam.memory;

import java.nio.ByteBuffer;

public interface ByteBufferBacked
{
	public ByteBuffer getUnderlyingByteBuffer();
}
