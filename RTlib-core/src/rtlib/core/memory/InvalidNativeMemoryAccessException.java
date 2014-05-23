package rtlib.core.memory;

public class InvalidNativeMemoryAccessException	extends
																								RuntimeException
{

	private static final long serialVersionUID = 1L;

	public InvalidNativeMemoryAccessException(String pString)
	{
		super(pString);
	}

}
