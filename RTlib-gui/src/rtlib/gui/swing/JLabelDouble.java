package rtlib.gui.swing;

import java.awt.EventQueue;

import javax.swing.JLabel;

import rtlib.core.variable.doublev.DoubleVariable;

public class JLabelDouble extends JLabel
{
	private final DoubleVariable mDoubleVariable;
	private JLabelDouble mThis;
	private final String mFormatString;

	private volatile double mNewValue;

	private boolean mIntegerConstraint = false;

	public JLabelDouble(final String pLabelName,
											final boolean pIntegerConstraint,
											final String pFormatString,
											final double pInicialValue)
	{
		super(getTextFromValue(	pIntegerConstraint,
														pFormatString,
														pInicialValue));
		mFormatString = pFormatString;
		mIntegerConstraint = pIntegerConstraint;
		mThis = this;

		mDoubleVariable = new DoubleVariable(pLabelName, pInicialValue)
		{
			@Override
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewValue)
			{
				if (pNewValue != mNewValue)
				{
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							mThis.setText(getTextFromValue(	mIntegerConstraint,
																							pFormatString,
																							pNewValue));
							mNewValue = pNewValue;
						}

					});
				}
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				setText(getTextFromValue(	mIntegerConstraint,
																	pFormatString,
																	pInicialValue));
			}
		});

	}

	public DoubleVariable getDoubleVariable()
	{
		return mDoubleVariable;
	}

	private static String getTextFromValue(	final boolean pIntegerConstraint,
																					final String pFormatString,
																					final double pNewValue)
	{
		if (pIntegerConstraint)
		{
			return String.format(pFormatString, Math.round(pNewValue));
		}
		else
		{
			return String.format(pFormatString, pNewValue);
		}
	}

	public boolean isIntegerConstraint()
	{
		return mIntegerConstraint;
	}

	public void setIntegerConstraint(final boolean pIsIntegerConstraint)
	{
		mIntegerConstraint = pIsIntegerConstraint;
	}

}
