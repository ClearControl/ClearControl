package rtlib.core.recycling.test;

import rtlib.core.recycling.RecyclerRequest;

class LongRequest implements RecyclerRequest<RecyclableTestClass>
{
	public LongRequest(long pL)
	{
		value = pL;
	}

	public long value;
}