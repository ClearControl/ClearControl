package rtlib.core.rgc;

public interface Freeable
{
	public void free();

	public boolean isFree();

	public default void complainIfFreed() throws FreedException
	{
		if (isFree())
		{
			final String lErrorMessage = "Underlying ressource has been freed!";
			throw new FreedException(lErrorMessage);
		}
	}

}
