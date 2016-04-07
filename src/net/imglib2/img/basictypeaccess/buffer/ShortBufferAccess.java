package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class ShortBufferAccess implements
															ShortAccess,
															ArrayDataAccess<ShortBufferAccess>
{
	protected ShortBuffer data;

	public ShortBufferAccess(final int numEntities)
	{
		this.data = ByteBuffer.allocateDirect(2 * numEntities)
													.order(ByteOrder.nativeOrder())
													.asShortBuffer();
	}

	@Override
	public short getValue(final int index)
	{
		return data.get(index);
	}

	@Override
	public void setValue(final int index, final short value)
	{
		data.put(index, value);
	}

	@Override
	public ShortBuffer getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public ShortBufferAccess createArray(final int numEntities)
	{
		return new ShortBufferAccess(numEntities);
	}
}
