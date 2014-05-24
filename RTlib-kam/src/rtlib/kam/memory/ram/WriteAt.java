package rtlib.kam.memory.ram;

import rtlib.kam.memory.MemoryTyped;

public interface WriteAt extends MemoryTyped
{
	public void setByte(final long pOffset, final byte pValue);

	public void setChar(final long pOffset, final char pValue);

	public void setShort(final long pOffset, final short pValue);

	public void setInt(final long pOffset, final int pValue);

	public void setLong(final long pOffset, final long pValue);

	public void setFloat(final long pOffset, final float pValue);

	public void setDouble(final long pOffset, final double pValue);

}
