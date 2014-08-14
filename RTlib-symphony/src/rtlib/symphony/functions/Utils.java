package rtlib.symphony.functions;

public class Utils
{
	static final short clampToShort(int pValue)
	{
		if (pValue > Short.MAX_VALUE)
			return Short.MAX_VALUE;
		else if (pValue < Short.MIN_VALUE)
			return Short.MIN_VALUE;
		else
			return (short) pValue;
	}

	static final short clampToShort(double pValue)
	{
		if (pValue > Short.MAX_VALUE)
			return Short.MAX_VALUE;
		else if (pValue < Short.MIN_VALUE)
			return Short.MIN_VALUE;
		else
			return (short) pValue;
	}
}
