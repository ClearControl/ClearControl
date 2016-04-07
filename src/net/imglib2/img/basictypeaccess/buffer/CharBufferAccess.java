package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class CharBufferAccess	implements
															CharAccess,
															ArrayDataAccess<CharBufferAccess>
{
	protected CharBuffer data;

	public CharBufferAccess(final int numEntities)
	{
		this.data = ByteBuffer.allocateDirect(2 * numEntities)
													.order(ByteOrder.nativeOrder())
													.asCharBuffer();
	}

	@Override
	public char getValue(final int index)
	{
		return data.get(index);
	}

	@Override
	public void setValue(final int index, final char value)
	{
		data.put(index, value);
	}

	@Override
	public CharBuffer getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public CharBufferAccess createArray(final int numEntities)
	{
		return new CharBufferAccess(numEntities);
	}
}
