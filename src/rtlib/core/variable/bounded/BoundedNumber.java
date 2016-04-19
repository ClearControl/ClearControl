package rtlib.core.variable.bounded;

public class BoundedNumber<T extends Number> extends Number	implements
																														Bounded<T>
{

	private static final long serialVersionUID = 1L;

	private T mValue;
	private T mMin;
	private T mMax;

	public BoundedNumber(T pValue, T pMin, T pMax)
	{
		super();
		mValue = pValue;
		mMin = pMin;
		mMax = pMax;
	}

	private T clamp(T pValue)
	{
		if (pValue.doubleValue() > mMax.doubleValue())
			return mMax;
		if (pValue.doubleValue() < mMin.doubleValue())
			return mMin;
		if (pValue.longValue() > mMax.longValue())
			return mMax;
		if (pValue.longValue() < mMin.longValue())
			return mMin;

		return mValue;
	}

	public void set(T pValue)
	{
		mValue = clamp((T) pValue);
	}
	
	public T get()
	{
		return mValue;
	}

	@Override
	public int intValue()
	{
		return mValue.intValue();
	}

	@Override
	public long longValue()
	{
		return mValue.longValue();
	}

	@Override
	public float floatValue()
	{
		return mValue.floatValue();
	}

	@Override
	public double doubleValue()
	{
		return mValue.doubleValue();
	}

	@Override
	public T getMin()
	{
		return mMin;
	}

	@Override
	public T getMax()
	{
		return mMax;
	}

	@Override
	public void setMin(T pMin)
	{
		mMin = pMin;
	}

	@Override
	public void setMax(T pMax)
	{
		mMax = pMax;
	}



}
