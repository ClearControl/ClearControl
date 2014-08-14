package rtlib.symphony;

public abstract class NameableAbstract
{
	private String mName;

	@SuppressWarnings("unused")
	private NameableAbstract()
	{
		super();
	}

	public NameableAbstract(final String pName)
	{
		super();
		mName = pName;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(final String name)
	{
		mName = name;
	}

	@Override
	public String toString()
	{
		return String.format("ScoreAbstract [mName=%s]", mName);
	}
}
