package frames;

import java.util.Collections;
import java.util.Iterator;


public enum BufferType
{
	FrameBuffer8Bit(0),
	FrameBuffer16Bit(1),
	FrameBufferJpegCompressed(2),
	FrameBuffer8BitTo16BitLookupTable(3);
	
	public final int value;
	
	BufferType(final int value)
	{
		this.value = value;
	}

	public long value()
	{
		return this.value;
	}

	public Iterator<BufferType> iterator()
	{
		return Collections.singleton(this).iterator();
	}

}
