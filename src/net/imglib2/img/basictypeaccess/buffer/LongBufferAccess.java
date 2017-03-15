package net.imglib2.img.basictypeaccess.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class LongBufferAccess implements
                              LongAccess,
                              ArrayDataAccess<LongBufferAccess>
{
  protected LongBuffer data;

  public LongBufferAccess(final int numEntities)
  {
    this.data = ByteBuffer.allocateDirect(numEntities * 4)
                          .order(ByteOrder.nativeOrder())
                          .asLongBuffer();
  }

  @Override
  public long getValue(final int index)
  {
    return data.get(index);
  }

  @Override
  public void setValue(final int index, final long value)
  {
    data.put(index, value);
  }

  @Override
  public LongBuffer getCurrentStorageArray()
  {
    return data;
  }

  @Override
  public LongBufferAccess createArray(final int numEntities)
  {
    return new LongBufferAccess(numEntities);
  }
}
