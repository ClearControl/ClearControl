package frames;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BufferHeader
{
	private ByteBuffer mHeader = ByteBuffer.allocate(2*4);

	
	public BufferHeader()
	{
		super();
	}
	
	public BufferHeader(final BufferType pBufferType, final int pLength)
	{
		super();
		set(pBufferType,pLength);
	}
	
	
	public final void set(final BufferType pBufferType, final int pLength)
	{
		mHeader.rewind();
		mHeader.putInt(pBufferType.value);
		mHeader.putInt(pLength);
	}
	
	public final void writeTo(FileChannel pFileChannel) throws IOException
	{
		mHeader.rewind();
		pFileChannel.write(mHeader);
	}

	public final void writeTo(ByteBuffer pByteBuffer)
	{
		mHeader.rewind();
		pByteBuffer.put(mHeader);
	}
	
	
}
