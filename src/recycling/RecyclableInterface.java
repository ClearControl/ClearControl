package recycling;

public interface RecyclableInterface
{
	void initialize(int... pParameters);
	void setRecycler(Recycler pRecycler);
	void setReleased(boolean pIsReleased);

}
