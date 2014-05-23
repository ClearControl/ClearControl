package rtlib.kam.memory.ram;

import rtlib.kam.memory.MemoryTyped;

public interface ReadAt extends MemoryTyped
{
	public byte getByte(final long pOffset);

	public char getChar(final long pOffset);

	public short getShort(final long pOffset);

	public int getInt(final long pOffset);

	public long getLong(final long pOffset);

	public float getFloat(final long pOffset);

	public double getDouble(final long pOffset);

}
