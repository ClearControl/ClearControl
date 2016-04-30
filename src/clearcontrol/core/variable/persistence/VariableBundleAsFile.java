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

public class VariableBundleAsFile extends VariableBundle
{
	private final ExecutorService cSingleThreadExecutor = Executors.newSingleThreadExecutor();

	private final ConcurrentSkipListMap<String, Variable<?>> mPrefixWithNameToVariableMap = new ConcurrentSkipListMap<String, Variable<?>>();

	private final VariableListener mVariableListener;

	private final File mFile;

	private final Object mLock = new Object();

	public VariableBundleAsFile(final String pBundleName,
															final File pFile)
	{
		this(pBundleName, pFile, false);
	}

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
			public void setEvent(	final Object pCurrentValue,
														final Object pNewValue)
			{
				writeAsynchronously();
			}
		};

	}

	@Override
	public <O> void addVariable(final Variable<O> pVariable)
	{
		this.addVariable("", pVariable);
	}

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

	@Override
	public Variable<?> getVariable(final String pPrefixAndName)
	{
		return mPrefixWithNameToVariableMap.get(pPrefixAndName);
	}

	private void registerListener(final Variable<?> pVariable)
	{
		final Variable<?> lObjectVariable = pVariable;
		lObjectVariable.addListener(mVariableListener);
	}

	private void unregisterListener(final Variable<?> pVariable)
	{
		pVariable.removeListener(mVariableListener);
	}

	private void unregisterListenerForAllVariables()
	{
		final Collection<Variable<?>> lAllVariables = getAllVariables();
		for (final Variable<?> lVariable : lAllVariables)
		{
			lVariable.removeListener(mVariableListener);
		}
	}

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
							final String[] lEqualsSplitStringArray = lLine.split("\t?=\t?");

							final String lKey = lEqualsSplitStringArray[0].trim();
							final String lValue = lEqualsSplitStringArray[1].trim();

							final Variable<?> lVariable = mPrefixWithNameToVariableMap.get(lKey);

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
		final Variable<Double> lDoubleVariable = (Variable<Double>) pVariable;

		final String[] lSplitValueFloatExactStringArray = lValue.split("\t");
		final String lApproximateFloatValueString = lSplitValueFloatExactStringArray[0];
		final double lApproximateDoubleValue = Double.parseDouble(lApproximateFloatValueString);

		double lDoubleValue = lApproximateDoubleValue;
		if (lSplitValueFloatExactStringArray.length == 2)
		{
			final String lExactLongValueString = lSplitValueFloatExactStringArray[1];
			final long lExactLongValue = Long.parseLong(lExactLongValueString);
			final double lExactDoubleValue = Double.longBitsToDouble(lExactLongValue);
			lDoubleValue = lExactDoubleValue;
		}

		lDoubleVariable.set(lDoubleValue);
	}

	private void readObjectVariable(final String lValue,
																	final Variable<?> lVariable)
	{
		final Variable<?> lObjectVariable = lVariable;

		final Variable<String> lStringVariable = (Variable<String>) lObjectVariable;
		lStringVariable.set(lValue);
	}

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
					final String lVariablePrefixAndName = lVariableEntry.getKey();
					final Variable<?> lVariable = lVariableEntry.getValue();

					// System.out.println(lVariable);

					if (lVariable.get() instanceof Number)
					{
						final Variable<Number> lDoubleVariable = (Variable<Number>) lVariable;

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

	private void writeAsynchronously()
	{
		// cSingleThreadExecutor.execute(mFileWriterRunnable);
		write();
	}

	private final Runnable mFileWriterRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			write();
		}
	};

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
