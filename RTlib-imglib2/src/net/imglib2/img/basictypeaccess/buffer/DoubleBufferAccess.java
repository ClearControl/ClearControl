package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class DoubleBufferAccess implements
																DoubleAccess,
															ArrayDataAccess<DoubleBufferAccess>
{
	protected DoubleBuffer data;

	public DoubleBufferAccess( final int numEntities )
	{
		this.data = ByteBuffer.allocateDirect(numEntities * 4)
													.order(ByteOrder.nativeOrder())
													.asDoubleBuffer();
	}

	@Override
	public double getValue(final int index)
	{
		return data.get( index );
	}

	@Override
	public void setValue(final int index, final double value)
	{
		data.put( index, value );
	}

	@Override
	public DoubleBuffer getCurrentStorageArray()
	{
		return data;
	}

	@Override
	public DoubleBufferAccess createArray( final int numEntities )
	{
		return new DoubleBufferAccess( numEntities );
	}
}
