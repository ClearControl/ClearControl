package rtlib.core.variable.bounded;

import rtlib.core.variable.Variable;

public class BoundedVariable<T extends Number> extends Variable<T>
{

	Variable<T> mMin, mMax, mGranularity;

	@SuppressWarnings("unchecked")
	public BoundedVariable(String pVariableName, T pReference)
	{
		super(pVariableName, pReference);

		if (pReference instanceof Double)
		{
			mMin = (Variable<T>) new Variable<Double>(pVariableName + "Min",
																								Double.NEGATIVE_INFINITY);
			mMax = (Variable<T>) new Variable<Double>(pVariableName + "Max",
																								Double.POSITIVE_INFINITY);
			mGranularity = (Variable<T>) new Variable<Double>(pVariableName + "Max",
																												0.0);
		}
		else if (pReference instanceof Float)
		{
			mMin = (Variable<T>) new Variable<Float>(	pVariableName + "Min",
																								Float.NEGATIVE_INFINITY);
			mMax = (Variable<T>) new Variable<Float>(	pVariableName + "Max",
																								Float.POSITIVE_INFINITY);
			mGranularity = (Variable<T>) new Variable<Float>(	pVariableName + "Max",
																												0.0f);
		}
		else if (pReference instanceof Long)
		{
			mMin = (Variable<T>) new Variable<Long>(pVariableName + "Min",
																							Long.MIN_VALUE);
			mMax = (Variable<T>) new Variable<Long>(pVariableName + "Max",
																							Long.MAX_VALUE);
			mGranularity = (Variable<T>) new Variable<Long>(pVariableName + "Max",
																											0L);
		}
		else if (pReference instanceof Integer)
		{
			mMin = (Variable<T>) new Variable<Integer>(	pVariableName + "Min",
																									Integer.MIN_VALUE);
			mMax = (Variable<T>) new Variable<Integer>(	pVariableName + "Max",
																									Integer.MAX_VALUE);
			mGranularity = (Variable<T>) new Variable<Integer>(	pVariableName + "Max",
																													0);
		}
		else if (pReference instanceof Short)
		{
			mMin = (Variable<T>) new Variable<Short>(	pVariableName + "Min",
																								Short.MIN_VALUE);
			mMax = (Variable<T>) new Variable<Short>(	pVariableName + "Max",
																								Short.MAX_VALUE);
			mGranularity = (Variable<T>) new Variable<Short>(	pVariableName + "Max",
																												(short) 0);
		}
		else if (pReference instanceof Byte)
		{
			mMin = (Variable<T>) new Variable<Byte>(pVariableName + "Min",
																							Byte.MIN_VALUE);
			mMax = (Variable<T>) new Variable<Byte>(pVariableName + "Max",
																							Byte.MAX_VALUE);
			mGranularity = (Variable<T>) new Variable<Byte>(pVariableName + "Max",
																											(byte) 0);
		}

	}

	public BoundedVariable(	String pVariableName,
													T pReference,
													T pMin,
													T pMax)
	{
		this(pVariableName, pReference, pMin, pMax, null);
	}

	public BoundedVariable(	String pVariableName,
													T pReference,
													T pMin,
													T pMax,
													T pGranularity)
	{
		super(pVariableName, pReference);

		mMin = new Variable<T>(pVariableName + "Min", pMin);
		mMax = new Variable<T>(pVariableName + "Max", pMax);
		mGranularity = new Variable<T>(	pVariableName + "Granularity",
																		pGranularity);
	}

	@Override
	public void set(T pNewReference)
	{
		super.set(clampAndSnap(pNewReference));
	}

	@SuppressWarnings("unchecked")
	private T clampAndSnap(T pNewReference)
	{
		if (pNewReference == null)
			return null;

		if (pNewReference instanceof Double || pNewReference instanceof Float)
		{
			double lNewValue = pNewReference.doubleValue();

			if (mGranularity != null && mGranularity.get().doubleValue() != 0.0)
			{
				double lGranularity = mGranularity.get().doubleValue();
				lNewValue = lGranularity * Math.round(lNewValue / lGranularity);
			}

			double lMin = mMin.get().doubleValue();
			double lMax = mMax.get().doubleValue();

			if (lNewValue < lMin)
				return mMin.get();
			else if (lNewValue > lMax)
				return mMax.get();
			else
			{
				if (pNewReference instanceof Double)
					return (T) new Double(lNewValue);
				else if (pNewReference instanceof Float)
					return (T) new Float(lNewValue);
			}
		}
		else if (pNewReference instanceof Long || pNewReference instanceof Integer
							|| pNewReference instanceof Short
							|| pNewReference instanceof Byte)
		{
			long lNewValue = pNewReference.longValue();

			if (mGranularity != null && mGranularity.get().longValue() != 0L)
			{
				long lGranularity = mGranularity.get().longValue();
				lNewValue = lGranularity * Math.round(lNewValue / lGranularity);
			}

			long lMin = mMin.get().longValue();
			long lMax = mMax.get().longValue();

			if (lNewValue < lMin)
				return mMin.get();
			else if (lNewValue > lMax)
				return mMax.get();
			else
			{
				if (pNewReference instanceof Long)
					return (T) new Long(lNewValue);
				else if (pNewReference instanceof Integer)
					return (T) new Integer((int) lNewValue);
				if (pNewReference instanceof Short)
					return (T) new Short((short) lNewValue);
				else if (pNewReference instanceof Byte)
					return (T) new Byte((byte) lNewValue);
			}
		}

		return pNewReference;
	}

	public Variable<T> getMinVariable()
	{
		return mMin;
	}

	public Variable<T> getMaxVariable()
	{
		return mMax;
	}

	public Variable<T> getGranularityVariable()
	{
		return mGranularity;
	}

}
