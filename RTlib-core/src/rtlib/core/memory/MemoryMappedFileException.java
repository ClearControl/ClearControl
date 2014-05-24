package rtlib.core.memory;

public class MemoryMappedFileException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public MemoryMappedFileException(String lErrorMessage, Throwable pE)
	{
		super(lErrorMessage, pE);
	}

	public MemoryMappedFileException(Exception pE)
	{
		super(pE);
	}

}
