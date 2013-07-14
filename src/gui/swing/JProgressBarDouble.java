package gui.swing;

import java.awt.EventQueue;

import javax.swing.JProgressBar;

import variable.doublev.DoubleVariable;

public class JProgressBarDouble extends JProgressBar
{
	private final DoubleVariable mJProgressBarDoubleVariable;

	private final JProgressBarDouble mThis;

	private final double mMin, mMax;

	public JProgressBarDouble(final String pName,
														final double pMin,
														final double pMax,
														final double pInicialValue)
	{
		super(0, 65535);
		mThis = this;
		mMin = pMin;
		mMax = pMax;

		mJProgressBarDoubleVariable = new DoubleVariable(	pName,
																											pInicialValue)
		{
			@Override
			public double setEventHook(final double pNewValue)
			{
				final int lProgressBarNewIntegerValue = toInt(65535,
																											mMin,
																											mMax,
																											pNewValue);

				if (mThis.getValue() != lProgressBarNewIntegerValue)
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							mThis.setValue(lProgressBarNewIntegerValue);
						}
					});

				return super.setEventHook(pNewValue);
			}
		};

	}

	public DoubleVariable getDoubleVariable()
	{
		return mJProgressBarDoubleVariable;
	}

	private static double toDouble(	final int pResolution,
																	final double pMin,
																	final double pMax,
																	final int pIntValue)
	{
		return pMin + (((double) pIntValue) / (pResolution - 1))
						* (pMax - pMin);
	}

	private static int toInt(	final int pResolution,
														final double pMin,
														final double pMax,
														final double pValue)
	{
		return (int) (Math.round((pResolution - 1) * (clamp(pMin,
																												pMax,
																												pValue) - pMin)) / (pMax - pMin));
	}

	private static double clamp(final double pMin,
															final double pMax,
															final double pValue)
	{
		return Math.min(pMax, Math.max(pMin, pValue));
	}

}
