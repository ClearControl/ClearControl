package rtlib.kam.memory.ram;

import rtlib.kam.memory.MemoryTyped;

public interface ReadAtAligned extends MemoryTyped
{
	public byte getByteAligned(final long pOffset);

	public char getCharAligned(final long pOffset);

	public short getShortAligned(final long pOffset);

	public int getIntAligned(final long pOffset);

	public long getLongAligned(final long pOffset);

	public float getFloatAligned(final long pOffset);

	public double getDoubleAligned(final long pOffset);

}
