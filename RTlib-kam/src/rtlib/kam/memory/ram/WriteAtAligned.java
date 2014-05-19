package rtlib.kam.memory.ram;

import rtlib.kam.memory.MemoryTyped;


public interface WriteAtAligned extends MemoryTyped
{
	public void setByteAligned(final long pOffset, final byte pValue);

	public void setCharAligned(final long pOffset, final char pValue);

	public void setShortAligned(final long pOffset, final short pValue);

	public void setIntAligned(final long pOffset, final int pValue);

	public void setLongAligned(final long pOffset, final long pValue);

	public void setFloatAligned(final long pOffset, final float pValue);

	public void setDoubleAligned(final long pOffset, final double pValue);
	
}
