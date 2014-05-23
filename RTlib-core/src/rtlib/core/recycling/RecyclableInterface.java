package rtlib.core.recycling;

import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Freeable;

public interface RecyclableInterface<O extends RecyclableInterface<O, P>, P>	extends
																																							SizedInBytes,
																																							Freeable
{

	@SuppressWarnings("unchecked")
	boolean isCompatible(P... pParameters);

	@SuppressWarnings("unchecked")
	void initialize(P... pParameters);

	void setRecycler(Recycler<O, P> pRecycler);

	void setReleased(boolean pIsReleased);

}
