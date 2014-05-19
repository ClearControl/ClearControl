package rtlib.kam.memory.ram;

import rtlib.kam.memory.MappableMemory;

public abstract class RAMMappedAbstract extends RAMAbstract	implements
																														MappableMemory

{

	protected boolean mIsMapped;

	@Override
	public abstract long map();

	@Override
	public abstract void unmap();

	@Override
	public boolean isCurrentlyMapped()
	{
		return mIsMapped;
	}

	protected void setCurrentlyMapped(boolean pMapped)
	{
		mIsMapped = pMapped;
	}
}
