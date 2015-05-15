package rtlib.symphony.staves;

import rtlib.symphony.functions.Set;

public class ConstantStave extends StaveAbstract implements
																								StaveInterface
{
	public volatile double mValue;

	public ConstantStave(final String pName, final double pValue)
	{
		super(pName + "(size=" + pValue + ")");
		mValue = pValue;
	}

	@Override
	public void updateStaveArray()
	{
		Set.write(this, mValue);
	}

}
