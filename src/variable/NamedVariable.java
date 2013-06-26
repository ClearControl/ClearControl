package variable;

public abstract class NamedVariable<O>
{

	private String mVariableName;

	public NamedVariable(final String pVariableName)
	{
		super();
		mVariableName = pVariableName;
	}

	public String getName()
	{
		return mVariableName;
	}

	public void setVariableName(final String variableName)
	{
		mVariableName = variableName;
	}

	public abstract O get();

	@Override
	public String toString()
	{
		return String.format(	"NamedVariable [mVariableName=%s]",
													getName(),
													get().toString());
	}

}
