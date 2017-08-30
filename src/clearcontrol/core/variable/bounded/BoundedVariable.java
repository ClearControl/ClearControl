package clearcontrol.core.variable.bounded;

import clearcontrol.core.variable.Variable;

/**
 * Bounded Variables have min, max and granularity values defined in addition to
 * the variable value itself. Of course, bounded variables only work for numeric
 * types.
 *
 * @param <N>
 *          numeric value type
 * @author royer
 */
public class BoundedVariable<N extends Number> extends Variable<N>
{

  Variable<N> mMin, mMax, mGranularity;

  /**
   * Instanciates a bounded variable with a given variable name and inicial
   * value
   * 
   * @param pVariableName
   *          variable name
   * @param pReference
   *          inicial value
   */
  @SuppressWarnings("unchecked")
  public BoundedVariable(String pVariableName, N pReference)
  {
    super(pVariableName, pReference);

    if (pReference instanceof Double)
    {
      mMin =
           (Variable<N>) new Variable<Double>(pVariableName + "Min",
                                              Double.NEGATIVE_INFINITY);
      mMax =
           (Variable<N>) new Variable<Double>(pVariableName + "Max",
                                              Double.POSITIVE_INFINITY);
      mGranularity = (Variable<N>) new Variable<Double>(pVariableName
                                                        + "Max", 0.0);
    }
    else if (pReference instanceof Float)
    {
      mMin =
           (Variable<N>) new Variable<Float>(pVariableName + "Min",
                                             Float.NEGATIVE_INFINITY);
      mMax =
           (Variable<N>) new Variable<Float>(pVariableName + "Max",
                                             Float.POSITIVE_INFINITY);
      mGranularity = (Variable<N>) new Variable<Float>(pVariableName
                                                       + "Max", 0.0f);
    }
    else if (pReference instanceof Long)
    {
      mMin = (Variable<N>) new Variable<Long>(pVariableName + "Min",
                                              Long.MIN_VALUE);
      mMax = (Variable<N>) new Variable<Long>(pVariableName + "Max",
                                              Long.MAX_VALUE);
      mGranularity = (Variable<N>) new Variable<Long>(pVariableName
                                                      + "Max", 0L);
    }
    else if (pReference instanceof Integer)
    {
      mMin =
           (Variable<N>) new Variable<Integer>(pVariableName + "Min",
                                               Integer.MIN_VALUE);
      mMax =
           (Variable<N>) new Variable<Integer>(pVariableName + "Max",
                                               Integer.MAX_VALUE);
      mGranularity = (Variable<N>) new Variable<Integer>(pVariableName
                                                         + "Max", 0);
    }
    else if (pReference instanceof Short)
    {
      mMin = (Variable<N>) new Variable<Short>(pVariableName + "Min",
                                               Short.MIN_VALUE);
      mMax = (Variable<N>) new Variable<Short>(pVariableName + "Max",
                                               Short.MAX_VALUE);
      mGranularity = (Variable<N>) new Variable<Short>(pVariableName
                                                       + "Max",
                                                       (short) 0);
    }
    else if (pReference instanceof Byte)
    {
      mMin = (Variable<N>) new Variable<Byte>(pVariableName + "Min",
                                              Byte.MIN_VALUE);
      mMax = (Variable<N>) new Variable<Byte>(pVariableName + "Max",
                                              Byte.MAX_VALUE);
      mGranularity = (Variable<N>) new Variable<Byte>(pVariableName
                                                      + "Max",
                                                      (byte) 0);
    }

  }

  /**
   * Instanciates a bounded variable with given variable name, inicial value,
   * min and max values and a null granularity (not used).
   * 
   * @param pVariableName
   *          variable name
   * @param pReference
   *          inicial value
   * @param pMin
   *          min value
   * @param pMax
   *          max value
   */
  @SuppressWarnings("unchecked")
  public BoundedVariable(String pVariableName,
                         N pReference,
                         N pMin,
                         N pMax)
  {
    this(pVariableName, pReference, pMin, pMax, (N) (new Double(0)));
  }

  /**
   * Instanciates a bounded variable with given variable name, inicial value,
   * min, max and granularity values.
   * 
   * @param pVariableName
   *          variable name
   * @param pReference
   *          inicial value
   * @param pMin
   *          min value
   * @param pMax
   *          max value
   * @param pGranularity
   *          granularity
   */
  public BoundedVariable(String pVariableName,
                         N pReference,
                         N pMin,
                         N pMax,
                         N pGranularity)
  {
    super(pVariableName, pReference);

    mMin = new Variable<N>(pVariableName + "Min", pMin);
    mMax = new Variable<N>(pVariableName + "Max", pMax);
    mGranularity = new Variable<N>(pVariableName + "Granularity",
                                   pGranularity);
  }

  @Override
  public void set(N pNewReference)
  {
    super.set(clampAndSnap(pNewReference));
  }

  @SuppressWarnings("unchecked")
  private N clampAndSnap(N pNewReference)
  {
    if (pNewReference == null)
      return null;

    if (pNewReference instanceof Double
        || pNewReference instanceof Float)
    {
      double lNewValue = pNewReference.doubleValue();

      if (mGranularity != null && mGranularity.get() != null
          && mGranularity.get().doubleValue() != 0.0)
      {
        double lGranularity = mGranularity.get().doubleValue();
        lNewValue =
                  lGranularity * Math.round(lNewValue / lGranularity);
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
          return (N) new Double(lNewValue);
        else if (pNewReference instanceof Float)
          return (N) new Float(lNewValue);
      }
    }
    else if (pNewReference instanceof Long
             || pNewReference instanceof Integer
             || pNewReference instanceof Short
             || pNewReference instanceof Byte)
    {
      long lNewValue = pNewReference.longValue();

      if (mGranularity != null && mGranularity.get() != null
          && mGranularity.get().longValue() != 0L)
      {
        long lGranularity = mGranularity.get().longValue();
        lNewValue =
                  lGranularity * Math.round(lNewValue / lGranularity);
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
          return (N) new Long(lNewValue);
        else if (pNewReference instanceof Integer)
          return (N) new Integer((int) lNewValue);
        if (pNewReference instanceof Short)
          return (N) new Short((short) lNewValue);
        else if (pNewReference instanceof Byte)
          return (N) new Byte((byte) lNewValue);
      }
    }

    return pNewReference;
  }

  /**
   * Returns the variable holding the min value
   * 
   * @return min variable
   */
  public Variable<N> getMinVariable()
  {
    return mMin;
  }

  /**
   * Returns the variable holding the max value
   * 
   * @return max variable
   */
  public Variable<N> getMaxVariable()
  {
    return mMax;
  }

  /**
   * Returns the variable holding the granularity value
   * 
   * @return granularity variable
   */
  public Variable<N> getGranularityVariable()
  {
    return mGranularity;
  }

  /**
   * Returns the current min value
   * 
   * @return current min value
   */
  public N getMin()
  {
    return mMin.get();
  }

  /**
   * Returns the current max value
   * 
   * @return current max value
   */
  public N getMax()
  {
    return mMax.get();
  }

  /**
   * Returns the current granularity value
   * 
   * @return current granularity value
   */
  public N getGranularity()
  {
    return mGranularity.get();
  }

  /**
   * Sets the value, min, max and granularity of this variable to that of the
   * given variable.
   * 
   * @param pVariable
   *          variable
   */
  public void set(BoundedVariable<N> pVariable)
  {
    mMin.set(pVariable.getMin());
    mMax.set(pVariable.getMax());
    mGranularity.set(pVariable.getGranularity());
    super.set(pVariable);
  }

  /**
   * Sets the min max value.
   * 
   * @param pMin
   *          min
   * @param pMax
   *          max
   */
  public void setMinMax(N pMin, N pMax)
  {
    if (pMin.doubleValue() <= pMax.doubleValue())
    {
      mMin.set(pMin);
      mMax.set(pMax);
    }
    else
    {
      mMin.set(pMax);
      mMax.set(pMin);
    }
  }

  /**
   * Sets the min max value.
   * 
   * @param pGranularity
   *          granularity
   * 
   */
  public void setGranularity(N pGranularity)
  {
    mGranularity.set(pGranularity);
  }

  /**
   * Sets the min max value (double parameter inputs)
   * 
   * @param pMin
   *          min
   * @param pMax
   *          max
   */
  @SuppressWarnings("unchecked")
  public void setMinMax(double pMin, double pMax)
  {
    double lReordedMin = Math.min(pMin, pMax);
    double lReordedlMax = Math.max(pMin, pMax);

    if (mValue instanceof Double)
    {
      mMin.set((N) new Double(lReordedMin));
      mMax.set((N) new Double(lReordedlMax));
    }
    else if (mValue instanceof Float)
    {
      mMin.set((N) new Float(lReordedMin));
      mMax.set((N) new Float(lReordedlMax));
    }
    else if (mValue instanceof Long)
    {
      mMin.set((N) new Long((long) lReordedMin));
      mMax.set((N) new Long((long) lReordedlMax));
    }
    else if (mValue instanceof Integer)
    {
      mMin.set((N) new Integer((int) lReordedMin));
      mMax.set((N) new Integer((int) lReordedlMax));
    }
    else if (mValue instanceof Short)
    {
      mMin.set((N) new Short((short) lReordedMin));
      mMax.set((N) new Short((short) lReordedlMax));
    }
    else if (mValue instanceof Byte)
    {
      mMin.set((N) new Byte((byte) lReordedMin));
      mMax.set((N) new Byte((byte) lReordedlMax));
    }
  }

  /**
   * Sets the granularity (double parameter input)
   * 
   * @param pGranularity
   *          granularity
   */
  @SuppressWarnings("unchecked")
  public void setGranularity(double pGranularity)
  {
    if (mValue instanceof Double)
    {
      mGranularity.set((N) new Double(pGranularity));
    }
    else if (mValue instanceof Float)
    {
      mGranularity.set((N) new Float(pGranularity));
    }
    else if (mValue instanceof Long)
    {
      mGranularity.set((N) new Long((long) pGranularity));
    }
    else if (mValue instanceof Integer)
    {
      mGranularity.set((N) new Integer((int) pGranularity));
    }
    else if (mValue instanceof Short)
    {
      mGranularity.set((N) new Short((short) pGranularity));
    }
    else if (mValue instanceof Byte)
    {
      mGranularity.set((N) new Byte((byte) pGranularity));
    }
  }

}
