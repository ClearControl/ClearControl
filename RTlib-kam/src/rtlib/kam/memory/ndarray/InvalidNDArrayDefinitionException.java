package rtlib.kam.memory.ndarray;

public class InvalidNDArrayDefinitionException extends
																							RuntimeException
{

	private static final long serialVersionUID = 1L;

	public InvalidNDArrayDefinitionException(String pErrorMessage)
	{
		super(pErrorMessage);
	}

}
