package score;

public abstract class ScoreAbstract
{
	private String mName;

	@SuppressWarnings("unused")
	private ScoreAbstract()
	{
		super();
	}

	public ScoreAbstract(String pName)
	{
		super();
		mName = pName;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		mName = name;
	}

	@Override
	public String toString()
	{
		return String.format("ScoreAbstract [mName=%s]", mName);
	}
}
