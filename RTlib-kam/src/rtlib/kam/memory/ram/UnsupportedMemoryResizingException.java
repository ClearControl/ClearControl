package rtlib.kam.memory.ram;

public class UnsupportedMemoryResizingException	extends
																								RuntimeException
{

	private static final long serialVersionUID = 1L;

	public UnsupportedMemoryResizingException(String pString)
	{
		super(pString);
	}

	public UnsupportedMemoryResizingException(String pErrorMessage,
																						Throwable pE)
	{
		super(pErrorMessage, pE);
	}

}
