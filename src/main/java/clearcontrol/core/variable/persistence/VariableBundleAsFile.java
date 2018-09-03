package clearcontrol.core.variable.persistence;

import java.io.File;
import java.util.Collection;
import java.util.Formatter;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableListener;
import clearcontrol.core.variable.bundle.VariableBundle;

/**
 * Variable bundle as file
 *
 * @author royer
 */
public class VariableBundleAsFile extends VariableBundle
                                  implements AutoCloseable
{
  private final ExecutorService cSingleThreadExecutor =
                                                      Executors.newSingleThreadExecutor();

  private final ConcurrentSkipListMap<String, Variable<?>> mPrefixWithNameToVariableMap =
                                                                                        new ConcurrentSkipListMap<String, Variable<?>>();

  @SuppressWarnings("rawtypes")
  private final VariableListener mVariableListener;

  private final File mFile;

  private final Object mLock = new Object();

  /**
   * Instantiates a variable-bundle-as-file with a given bundle name and file.
   * 
   * @param pBundleName
   *          bundle name
   * @param pFile
   *          file to use to store and retrieve the bundle's variable values.
   */
  public VariableBundleAsFile(final String pBundleName,
                              final File pFile)
  {
    this(pBundleName, pFile, false);
  }

  /**
   * Instantiates a variable-bundle-as-file object.
   * 
   * @param pBundleName
   *          bundle name
   * @param pFile
   *          file to useto store and retrieve the bundle's variable values.
   * @param pAutoReadOnGet
   *          if true the file will be read for every get
   */
  @SuppressWarnings("rawtypes")
  public VariableBundleAsFile(final String pBundleName,
                              final File pFile,
                              final boolean pAutoReadOnGet)
  {
    super(pBundleName);
    mFile = pFile;

    mVariableListener = new VariableListener()
    {

      @Override
      public void getEvent(final Object pCurrentValue)
      {
        if (pAutoReadOnGet)
        {
          read();
        }
      }

      @Override
      public void setEvent(final Object pCurrentValue,
                           final Object pNewValue)
      {
        write();
      }
    };

  }

  @Override
  public <O> void addVariable(final Variable<O> pVariable)
  {
    this.addVariable("", pVariable);
  }

  /**
   * Adds a variable to this bundle but adds a prefix that is used to store the
   * variable value in the file.
   * 
   * @param pPrefix
   *          prefix
   * @param pVariable
   *          variable
   */
  public <O> void addVariable(final String pPrefix,
                              final Variable<O> pVariable)
  {
    super.addVariable(pVariable);
    final String lKey = pPrefix + (pPrefix.isEmpty() ? "" : ".")
                        + pVariable.getName();
    mPrefixWithNameToVariableMap.put(lKey.trim(), pVariable);
    registerListener(pVariable);
  }

  @Override
  public <O> void removeVariable(final Variable<O> pVariable)
  {
    unregisterListener(pVariable);
    super.removeVariable(pVariable);
  }

  @Override
  public void removeAllVariables()
  {
    unregisterListenerForAllVariables();
    super.removeAllVariables();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Variable<?> getVariable(final String pPrefixAndName)
  {
    return mPrefixWithNameToVariableMap.get(pPrefixAndName);
  }

  @SuppressWarnings("unchecked")
  private void registerListener(final Variable<?> pVariable)
  {
    final Variable<?> lObjectVariable = pVariable;
    lObjectVariable.addListener(mVariableListener);
  }

  @SuppressWarnings("unchecked")
  private void unregisterListener(final Variable<?> pVariable)
  {
    pVariable.removeListener(mVariableListener);
  }

  @SuppressWarnings("unchecked")
  private void unregisterListenerForAllVariables()
  {
    final Collection<Variable<?>> lAllVariables = getAllVariables();
    for (final Variable<?> lVariable : lAllVariables)
    {
      lVariable.removeListener(mVariableListener);
    }
  }

  /**
   * Reads file to update variable values
   * 
   * @return true for success
   */
  public boolean read()
  {

    try
    {
      synchronized (mLock)
      {
        Scanner lScanner = null;
        if (mFile.exists())
        {
          try
          {
            lScanner = new Scanner(mFile);

            while (lScanner.hasNextLine())
            {
              final String lLine = lScanner.nextLine();
              final String[] lEqualsSplitStringArray =
                                                     lLine.split("\t?=\t?");

              final String lKey = lEqualsSplitStringArray[0].trim();
              final String lValue = lEqualsSplitStringArray[1].trim();

              final Variable<?> lVariable =
                                          mPrefixWithNameToVariableMap.get(lKey);

              if (lVariable != null)
              {
                if (lVariable.get() instanceof Number)
                {
                  readDoubleVariable(lValue, lVariable);
                }
                else if (lVariable instanceof Variable<?>)
                {
                  readObjectVariable(lValue, lVariable);
                }
              }
            }
          }
          catch (final Exception e)
          {
            e.printStackTrace();
            return false;
          }
          finally
          {
            lScanner.close();
          }
        }

        return true;
      }
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      return false;
    }

  }

  private void readDoubleVariable(final String lValue,
                                  final Variable<?> pVariable)
  {
    @SuppressWarnings("unchecked")
    final Variable<Double> lDoubleVariable =
                                           (Variable<Double>) pVariable;

    final String[] lSplitValueFloatExactStringArray =
                                                    lValue.split("\t");
    final String lApproximateFloatValueString =
                                              lSplitValueFloatExactStringArray[0];
    final double lApproximateDoubleValue =
                                         Double.parseDouble(lApproximateFloatValueString);

    double lDoubleValue = lApproximateDoubleValue;
    if (lSplitValueFloatExactStringArray.length == 2)
    {
      final String lExactLongValueString =
                                         lSplitValueFloatExactStringArray[1];
      final long lExactLongValue =
                                 Long.parseLong(lExactLongValueString);
      final double lExactDoubleValue =
                                     Double.longBitsToDouble(lExactLongValue);
      lDoubleValue = lExactDoubleValue;
    }

    lDoubleVariable.set(lDoubleValue);
  }

  private void readObjectVariable(final String lValue,
                                  final Variable<?> lVariable)
  {
    final Variable<?> lObjectVariable = lVariable;

    @SuppressWarnings("unchecked")
    final Variable<String> lStringVariable =
                                           (Variable<String>) lObjectVariable;
    lStringVariable.set(lValue);
  }

  /**
   * Writes the values of the variables in this bundle to the file
   * 
   * @return true for success
   */
  public boolean write()
  {
    synchronized (mLock)
    {
      Formatter lFormatter = null;
      try
      {
        lFormatter = new Formatter(mFile);
        for (final Map.Entry<String, Variable<?>> lVariableEntry : mPrefixWithNameToVariableMap.entrySet())
        {
          final String lVariablePrefixAndName =
                                              lVariableEntry.getKey();
          final Variable<?> lVariable = lVariableEntry.getValue();

          // System.out.println(lVariable);

          if (lVariable.get() instanceof Number)
          {
            @SuppressWarnings("unchecked")
            final Variable<Number> lDoubleVariable =
                                                   (Variable<Number>) lVariable;

            lFormatter.format("%s\t=\t%g\n",
                              lVariablePrefixAndName,
                              lDoubleVariable.get().doubleValue());

          }
          else if (lVariable instanceof Variable<?>)
          {
            final Variable<?> lObjectVariable = lVariable;

            lFormatter.format("%s\t=\t%s\n",
                              lVariablePrefixAndName,
                              lObjectVariable.get());
          }
        }

        lFormatter.flush();
        if (lFormatter != null)
        {
          // System.out.println("close formatter");
          lFormatter.close();
        }
        return true;
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
        return false;
      }

    }

  }

  public void close()
  {
    cSingleThreadExecutor.shutdown();
    try
    {
      cSingleThreadExecutor.awaitTermination(100, TimeUnit.SECONDS);
    }
    catch (final InterruptedException e)
    {
    }
  }

}
