package recycling;

public interface RecyclableInterface<O extends RecyclableInterface<O>>
{
	void initialize(int... pParameters);
	void setRecycler(Recycler<O> pRecycler);
	void setReleased(boolean pIsReleased);

}
